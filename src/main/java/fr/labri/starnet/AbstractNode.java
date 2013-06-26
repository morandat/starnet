package fr.labri.starnet;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import fr.labri.starnet.Message.Type;

public class AbstractNode implements Node {
	private Address _address;
	
	private int _msgID = 0;
	private OrientedPosition _position;
	private OrientedPosition _newPosition;
	private double _power;
	
	final private Descriptor _descriptor;
	final private World _world;
	
	private boolean _online;
	final private PolicyAdapter _adapter = new RandomPolicyAdapter(new RoutingPolicy[]{ new SimpleRouting() });
	
	private Message[] _mailbox;
	final private Collection<Message> _receivebox = new ConcurrentLinkedQueue<Message>();
	
	AbstractNode(World world, Descriptor descriptor) {
		 _world = world;
		 _descriptor = descriptor;
		 _position = OrientedPosition.ORIGIN;
		 _address = world.register(this);
		 _power = descriptor.getMaxPower();
		 _online = true;
	}
	
	public void send(Message msg) {
		_adapter.getCurrentPolicy().send(msg);
	}
	
	public void sendTo(Address addr, Message msg) {
		_adapter.getCurrentPolicy().sendTo(addr, msg);
	}
	
	public final Message[] receive() {
		return _mailbox;
	}
	

	final NetworkManager _network = new NetworkManager() {
		public void send(double power, Message msg) {
			getWorld().send(AbstractNode.this, power, msg);
		}
	};
	
	public final void deliver(Message msg) {
		if(_online)
			_receivebox.add(msg);
	}

	public final void activate() {
		_mailbox = new Message[_receivebox.size()];
		_receivebox.toArray(_mailbox);
		_receivebox.clear();
	}

	final public Message createMessage(Type type) {
		return AbstractMessage.createMessage(getWorld().getTime(), type, this, null, 1, null);
	}
	final public Message createMessage(Type type, Address to) {
		return AbstractMessage.createMessage(getWorld().getTime(), type, this, to, 1, null);
	}
	final public Message createMessage(Type type, Address to, Map<String, Object> data) {
		return AbstractMessage.createMessage(getWorld().getTime(), type, this, to, 1, data);
	}
	final public Message createMessage(Type type, Address to, int payload) {
		return AbstractMessage.createMessage(getWorld().getTime(), type, this, to, payload, null);
	}
	final public Message createMessage(Type type, Address to, int payload, Map<String, Object> data) {
		return AbstractMessage.createMessage(getWorld().getTime(), type, this, to, payload, data);
	}

	final public Message forwardMessage(Message msg) {
		return AbstractMessage.forwardMessage(getWorld().getTime(), msg, this, null);
	}
	final public Message forwardMessage(Message msg, Map<String, Object> data) {
		return AbstractMessage.forwardMessage(getWorld().getTime(), msg, this, data);
	}

	public Address getAddress() {
		return _address;
	}

	public Descriptor getDescriptor() {
		return _descriptor;
	}

	public OrientedPosition getPosition() {
		return _position;
	}

	public World getWorld() {
		return _world;
	}

	public void perform() {
		
	}

	public int newMessageID() {
		return _msgID++;
	}
	
	public class SimpleRouting implements RoutingPolicy {
		public void send(Message msg) {
			_network.send(1, msg);
		}

		public void sendTo(Address addr, Message msg) {
			if(msg.getReceiverAddress() != _address)
				_network.send(1, msg);
		}
	}

	public void setPosition(OrientedPosition newPosition) {
		_position = newPosition;
	}

	public double getPowerLevel() {
		return _power;
	}
	
	public void consumePower(double power) {
		_power -= power;
	}

	public boolean isOnline() {
		return _online;
	}

	public void setOnline(boolean b) {
		_online = b;
	}

	public Message newMessage(Type type, Address dst) {
		return AbstractMessage.createMessage(_world.getTime(), type, this, dst, 1, null);
	}
	
	public void move(OrientedPosition newPos) {
		_newPosition = newPos;
	}

	@Override
	public void updatePosition() {
		_position = _newPosition;
	}

	@Override
	public OrientedPosition getNewPosition() {
		return _newPosition;
	}

	@Override
	public void cancelPosition() {
		_newPosition = _position;
	}
}
