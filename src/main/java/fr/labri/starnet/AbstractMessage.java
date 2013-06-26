package fr.labri.starnet;

import java.util.Map;

public abstract class AbstractMessage implements Message {
	final private INode _sender;
	final private Position _position;
	final private long _timestamp;
	
	private AbstractMessage(INode sender, long timestamp) {
		_sender = sender;
		_timestamp = timestamp;
		_position = sender.getPosition();
	}
	public long getEmitTime() {
		return _timestamp;
	}

	public Address getSenderAddress() {
		return _sender.getAddress();
	}

	public Position getSenderPosition() {
		return _position;
	}

	public int getHops() {
		int hops = 0;
		Message parent = getParent();
		while(parent != null) {
			parent = getParent();
			hops++;
		}
		return hops;
	}

	final static Message createMessage(long timestamp, Type type, INode from, Address to, int payload, Map<String, Object> data) {
		if(data == null)
			return new RootSimpleMessage(timestamp, to, type, from, payload);
		else
			return new RootDataMessage(timestamp, to, type, from, payload, data);
	}
	
	final static Message forwardMessage(long timestamp, Message msg, INode sender, Map<String, Object> data) {
		if(data == null)
			return new ForwardedSimpleMessage(sender, msg, timestamp);
		else
			return new ForwardedDataMessage(sender, msg, timestamp, data);
	}
	
	static abstract class RootMessage extends AbstractMessage {
		final private long _id;
		final private Type _type;
		final private int _size;
		final private Address _to;
		
		private RootMessage(long timestamp, Address to, Type type, INode sender, int payload) {
			super(sender, timestamp);
			_id = (sender.getAddress().asInt() << Integer.SIZE) | sender.newMessageID();
			_type = type;
			_size = payload;
			_to = to;
		}
		
		public long getMessageID() {
			return _id;
		}

		public Type getType() {
			return _type;
		}

		public int getSize() {
			return _size;
		}
		
		public Address getReceiverAddress() {
			return _to;
		}

		public Message getParent() {
			return null;
		}
	}
	
	private static class RootSimpleMessage extends RootMessage {
		RootSimpleMessage(long timestamp, Address to, Type type, INode sender, int payload) {
			super(timestamp, to, type, sender, payload);
		}
		public <A> A getField(String name) {
			return null;
		}
	}
	
	private abstract static class ForwardedMessage extends AbstractMessage {
		final Message _parent; 
		private ForwardedMessage(INode sender, Message parent, long timestamp) {
			super(sender, timestamp);
			if(parent == null) throw new NullPointerException("Forwarding a null message.");
			_parent = parent;
		}
		
		public long getMessageID() {
			return _parent.getMessageID();
		}

		public Type getType() {
			return _parent.getType();
		}

		public int getSize() {
			return _parent.getSize();
		}
		public Address getReceiverAddress() {
			return _parent.getReceiverAddress();
		}

		public Message getParent() {
			return _parent;
		}
	}
	
	private static class ForwardedSimpleMessage extends ForwardedMessage {
		private ForwardedSimpleMessage(INode sender, Message parent, long timestamp) {
			super(sender, parent, timestamp);
		}
		
		public <A> A getField(String name) {
			return getParent().getField(name);
		}
	}
	
	private static class RootDataMessage extends RootMessage {
		final private Map<String, Object> _data;
		
		RootDataMessage(long timestamp, Address to, Type type, INode sender, int payload, Map<String, Object> data) {
			super(timestamp, to, type, sender, payload);
			_data = data;
		}
		
		@SuppressWarnings("unchecked")
		public <A> A getField(String name) {
			return (A)_data.get(name);
		}
	}
	
	private static class ForwardedDataMessage extends ForwardedMessage {
		final private Map<String, Object> _data;

		ForwardedDataMessage(INode sender, Message parent, long timestamp, Map<String, Object> override) {
			super(sender, parent, timestamp);
			_data = override;
		}
		
		@SuppressWarnings("unchecked")
		public <A> A getField(String name) {
			if(_data.containsKey(name))
				return (A)_data.get(name);
			return getParent().getField(name);
		}
	}
}
