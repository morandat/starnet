package fr.labri.starnet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import fr.labri.IntBitSet;
import fr.labri.starnet.INode.Descriptor;
import fr.labri.starnet.INode.Observer;


public abstract class World {
	long time = -1L;

	protected YellowPages yp = newSimpleYellowPages();
	private final Position _dimension;
	
	private ArrayList<Observer> _observers = new ArrayList<Observer>();
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
		
		final private void put(Node n, Address a) {
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
		for(Observer obs: _observers)
			obs.messageSent(sender.asINode(), power);

		double window = desc.getEmissionWindow();

		send0(sender, window, range, msg);
	}
	
	public void addObserver(Observer obs) {
		_observers.add(obs);
	}
	
	void deliver(Message msg, Node receiver) {
		receiver.deliver(msg);
		for(Observer obs: _observers)
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
	
	abstract public Random getRandom();
	
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
			Random _random = new Random(); // FIXME set seed
			
			@Override
			void init() { }
			
			public void doTick() {
				nextTick();
				activate(participants);
				play(participants);
			}
			
			@Override
			public Random getRandom() {
				return _random;
			}
		};
	}
	
	static World newParallelWorld(int w, int h) {
		return new World(w, h) {
			public final int THREADS = Runtime.getRuntime().availableProcessors();
			
			Random _random = new Random(); // FIXME set seed

			ArrayList<RunGroup> _activate = new ArrayList<RunGroup>(THREADS); 
			ArrayList<RunGroup> _play = new ArrayList<RunGroup>(THREADS); 
			CountDownLatch _lock;
			final ExecutorService _pool = Executors.newFixedThreadPool(THREADS);
			
			@Override
			void init() {
				_usedPosition = Collections.synchronizedSet(_usedPosition); // FIXME this is a huge lock
				int nb = participants.size() / THREADS;
				int rest = participants.size() % THREADS;

				for(int i = 0, j = 0; i < THREADS; i++){
					int k = j;
					j += nb + ((rest --) > 0 ? 1 : 0);
					_activate.add(createActivate(participants.subList(k, j)));
					_play.add(createPlay(participants.subList(k, j)));
				}
			}
			
			public void doTick() {
				nextTick();
				try {
					executeTasks(_activate);
					executeTasks(_play);
				} catch (InterruptedException e) {
					System.err.println("Thread interupted, waiting for: " + _lock.getCount());
					e.printStackTrace();
				}
			}
			void executeTasks(Collection<RunGroup> tasks) throws InterruptedException {
				_lock = new CountDownLatch(THREADS);

				for(Runnable task: tasks)
					_pool.execute(task);
				
					Thread.interrupted(); // FIXME remove this, only used to clear the flag ... (need to remove this from simulation first)
					_lock.await();
			}
			
			RunGroup createActivate(final List<Node> nodes) {
				return new RunGroup() {
					 void doTask() {
						activate(nodes);
					}
				};
			}
			
			RunGroup createPlay(final List<Node> nodes) {
				return new RunGroup() {
					public void doTask() {
						play(nodes);
					}
				};
			}

			abstract class RunGroup implements Runnable {
				@Override
				public void run() {
					doTask();
					_lock.countDown();
				}

				abstract void doTask();
			}
			
			@Override
			public Random getRandom() { // TODO create facade
				return _random;
			}
		};
	}
}
