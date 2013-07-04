package fr.labri.starnet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import fr.labri.IntBitSet;
import fr.labri.starnet.INode.Descriptor;


public abstract class World {
	long time = -1L;

	protected YellowPages yp = newSimpleYellowPages();
	private final Position _dimension;
	
	private ArrayList<NodeObserver> _observers = new ArrayList<NodeObserver>();
	protected ArrayList<Node> participants = new ArrayList<Node>();
	
	Set<Integer> _usedPosition = new IntBitSet();

	public World(int w, int h) {
		_dimension = new Position(w, h);
	}
	
	static protected abstract class YellowPages {
		final Map<Node, Address> nodeToAddress = new HashMap<Node, Address>();
		final Map<Address, Node> addressToNode = new LinkedHashMap<Address, Node>();
		
		public final Address get(Node n) {
			return nodeToAddress.get(n);
		}
		
		public final Node get(Address a) {
			return addressToNode.get(a);
		}
		
		public final Collection<Node> getParticipants() {
			return addressToNode.values();
		}
		
		synchronized final private void put(Node n, Address a) { // TODO remove or not this synchronized keyword
			if(addressToNode.containsKey(a) || nodeToAddress.containsKey(n))
				throw new RuntimeException("Node or address already in use "+ a + " "+n);
			addressToNode.put(a, n);
			nodeToAddress.put(n, a);
		}
		
		final private Address assign(Node node) {
			Address address;
			synchronized (this) {
				address = addressForNode(node);
				put(node, address);
			}
			return address;
		}

		abstract protected Address addressForNode(Node node);
	}
	
	final static private YellowPages newSimpleYellowPages() {
		return new YellowPages() {
			AtomicInteger next = new AtomicInteger();
			@Override
			protected Address addressForNode(Node node) {
				return getSimpleAddress(next.incrementAndGet());
			}
		};
	}
	
	private static Address getSimpleAddress(final int value) {
		return new Address() {
			public String toString() {
				return Integer.toHexString(value);
			}

			public int asInt() {
				return value;
			}
		};
	}
	
	public Address register(Node node) {
		Address addr = yp.assign(node);
		participants.add(node);
		return addr;
	}
	
	public final List<Node> getParticipants() {
		return Collections.unmodifiableList(participants);
	}
	
	public Position getDimension() {
		return _dimension;
	}
	
	public void send(Node sender, double power, Message msg) {
		if(sender.getPowerLevel() < power)
			throw new RuntimeException("Sender "+ sender+ " has not enough power to send a message ("+ sender.getPowerLevel()+ "/"+power+")");
		
		Descriptor desc = sender.getDescriptor(); 
		double range = desc.getEmissionRange() * power;

		sender.consumePower(desc.getEneryModel().energy(range));
		for(NodeObserver obs: _observers)
			obs.messageSent(sender.asINode(), power);

		double window = desc.getEmissionWindow();

		send0(sender, window, range, msg);
	}
	
	public void addObserver(NodeObserver obs) {
		_observers.add(obs);
	}
	
	void deliver(Message msg, Node receiver) {
		receiver.deliver(msg);
		for(NodeObserver obs: _observers)
			obs.messageReceived(receiver.asINode());
	}
	
	void activate(Collection<Node> nodes) {
		for(Node node: nodes)
			if(node.isOnline()) {
				node.activate();
				int nPos = positionAsInt(node.getNextPosition());
				int oPos = positionAsInt(node.getPosition());
				if(oPos == nPos) {
					node.updatePosition();
				} else if(_usedPosition.add(nPos)) {
					node.updatePosition();
					_usedPosition.remove(oPos);
				} else
					node.cancelPosition();
			}
	}
	
	private int positionAsInt(Position position) {
		int x = (int) position._x;
		int y = (int) position._y * _dimension._x;
		if(y%2 == 1)
			x = _dimension._x - x;
		return x + y;
	}
	
	void play(Collection<Node> nodes) {
		for(Node node: nodes)
			if(node.isOnline())
				node.perform();
	}
	
	abstract public void doTick();
	
	public void nextTick() {
		time ++;
	}
	
	public long getTime() {
		return time;
	}
	
	abstract void init();
	
	protected void send0(Node sender, double window, double range, Message msg) {
		OrientedPosition pos = sender.getPosition();
		for(Node node: participants)
			if(sender != node && node.isOnline() && pos.inRange(node.getPosition(), window, range))
				deliver(msg, node);
	}

	static World newSimpleWorld(int w, int h) {
		return new World(w, h) {
			@Override
			void init() {
			}
			public void doTick() {
				nextTick();
				activate(participants);
				play(participants);
			}
		};
	}
	
	static World newParallelWorld(int w, int h) {
		return new World(w, h) {
			public static final int THREADS = 20;
			
			ArrayList<Runnable> _activate = new ArrayList<Runnable>(THREADS); 
			ArrayList<Runnable> _play = new ArrayList<Runnable>(THREADS); 
			final ExecutorService _pool = Executors.newFixedThreadPool(THREADS);
			final AtomicInteger _lock = new AtomicInteger();
			
			@Override
			void init() {
				_usedPosition = Collections.synchronizedSet(_usedPosition); // FIXME this is a huge lock
				int nb = participants.size() / THREADS;
				for(int i = 0; i < THREADS; i++){
					_activate.add(createActivate(participants.subList(i * nb, i*(nb + 1) - 1)));
					_play.add(createPlay(participants.subList(i * nb, i*(nb + 1) - 1)));
				}
			}
			public void doTick() {
				nextTick();
				executeTasks(_activate);
				executeTasks(_activate);
			}
			void executeTasks(Collection<Runnable> tasks) {
				for(Runnable task: _activate)
					_pool.execute(task);
				while(_lock.get() > 0)
					try {
						_lock.wait();
					} catch (InterruptedException e) {	}
				
			}
			Runnable createActivate(final List<Node> nodes) {
				return new Runnable() {
					public void run() {
						activate(nodes);
						_lock.getAndDecrement();
						_lock.notify();
					}
				};
			}
			Runnable createPlay(final List<Node> nodes) {
				return new Runnable() {
					public void run() {
						play(nodes);
						_lock.getAndDecrement();
						_lock.notify();
					}
				};
			}
		};
	}
}
