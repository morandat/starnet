package fr.labri.starnet;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

import fr.labri.starnet.INode.Descriptor;
import fr.labri.starnet.INode.MessageFactory;
import fr.labri.starnet.Message.Type;

public class Node {
	private Address _address;
	
	private int _msgID = 0;
	private OrientedPosition _position;
	private OrientedPosition _nextPosition;
	private double _power;
	
	final private Descriptor _descriptor;
	final private World _world;
	
	private boolean _online;
	//To change
	//[04/07/13 10:05:14] Floréal: 	final private PolicyAdapter _adapter;
	//[04/07/13 10:05:28] Floréal: new RandomPolicyAdapter(new RoutingPolicy[]{ new SimpleRouting() });
	// new RandomPolicyAdapter(new RoutingPolicy[]{ new TimedAutomataPolicy(TimedAutomata.loadFormXML("blabla").compile()) });
	final private PolicyAdapter _adapter;
	
	final private Map<String, Object> _storage = new HashMap<>();
	
	private Message[] _mailbox;
	final private Collection<Message> _receivebox = new ConcurrentLinkedQueue<Message>();
	
	final INode _inode = new INode() {
		@Override
		public Address getAddress() {
			return _address;
		}

		@Override
		public void send(Message msg) {
			_adapter.getCurrentPolicy().send(this, msg);
		}

		@Override
		public void sendTo(Address addr, Message msg) {
			_adapter.getCurrentPolicy().sendTo(this, addr, msg);
		}

		@Override
		public Message[] receive() {
			return _mailbox;
		}

		@Override
		public Descriptor getDescriptor() {
			return Node.this._descriptor;
		}

		@Override
		public double getPowerLevel() {
			return Node.this._power;
		}

		@Override
		public OrientedPosition getPosition() {
			return Node.this.getPosition();
		}

		@Override
		public void move(OrientedPosition position) {
			Node.this.move(position);			
		}

		@Override
		public boolean isOnline() {
			return Node.this._online;
		}

		@Override
		public MessageFactory newMessage() {
			return _messageFactory;
		}

		@Override
		public void send(double power, Message msg) {
			Node.this.send(power, msg);
		}

		@Override
		public Map<String, Object> getStorage() {
			return _storage;
		}

		@Override
		public long getTime() {
			return _world.getTime();
		}

		@Override
		public Random getRandom() {
			return _world.getRandom();
		}
	};
	
	MessageFactory _messageFactory = new MessageFactory() {
		final public Message create(Type type) {
			return AbstractMessage.createMessage(_world.getTime(), type, Node.this, null, 1, null);
		}
		final public Message create(Type type, Address to) {
			return AbstractMessage.createMessage(_world.getTime(), type, Node.this, to, 1, null);
		}
		final public Message create(Type type, Address to, Map<String, Object> data) {
			return AbstractMessage.createMessage(_world.getTime(), type, Node.this, to, 1, data);
		}
		final public Message create(Type type, Address to, int payload) {
			return AbstractMessage.createMessage(_world.getTime(), type, Node.this, to, payload, null);
		}
		final public Message create(Type type, Address to, int payload, Map<String, Object> data) {
			return AbstractMessage.createMessage(_world.getTime(), type, Node.this, to, payload, data);
		}
		final public Message from(Message msg) {
			return AbstractMessage.forwardMessage(_world.getTime(), msg, Node.this, null);
		}
		final public Message from(Message msg, Map<String, Object> data) {
			return AbstractMessage.forwardMessage(_world.getTime(), msg, Node.this, data);
		}
	};
	
	public interface PolicyAdapter {
		RoutingPolicy adaptPolicy();
		RoutingPolicy getCurrentPolicy();
	}

	public interface PolicyAdapterFactory {
		PolicyAdapter getPolicyAdapter(INode node);
	}
	
	
	public interface SensorAggregator {
		Sensor[] getSensors();
	}
	
	Node(World world, PolicyAdapterFactory adapter, Descriptor descriptor) {
		 _world = world;
		 _descriptor = descriptor;
		 _position = OrientedPosition.ORIGIN;
		 _address = world.register(this);
		 _power = descriptor.getMaxPower();
		 _online = true;
		 _adapter = adapter.getPolicyAdapter(asINode());
	}
	
	public int newMessageID() {
		return _msgID++;
	}
	
	final public void send(double power, Message msg) {
		_world.send(Node.this, power, msg);
	}
	
	public final void deliver(Message msg) {
		if(_online)
			_receivebox.add(msg);
	}

	public final void activate() {
		_mailbox = new Message[_receivebox.size()];
		_receivebox.toArray(_mailbox);
		_receivebox.clear();
	}

	final public Address getAddress() {
		return _address;
	}
	
	final public Descriptor getDescriptor() {
		return _descriptor;
	}

	final public OrientedPosition getPosition() {
		return _position;
	}

	public void perform() {
		_adapter.adaptPolicy().maintain(asINode());
	}

	final public void setPosition(OrientedPosition newPosition) {
		if(newPosition != null)
			_nextPosition = _position = newPosition;
	}

	final public double getPowerLevel() {
		return _power;
	}
	
	final public void consumePower(double power) {
		_power -= power;
	}

	final public boolean isOnline() {
		return _online;
	}

	public void setOnline(boolean b) {
		_online = b;
	}

	final public void move(OrientedPosition newPos) {
		_nextPosition = newPos;
	}

	final public void updatePosition() {
		_position = _nextPosition;
	}

	final public OrientedPosition getNextPosition() {
		return _nextPosition;
	}

	final public void cancelPosition() {
		_nextPosition = _position;
	}

	final public INode asINode() {
		return _inode;
	}
}
