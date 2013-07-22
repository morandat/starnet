package fr.labri.starnet;

import java.util.Map;
import java.util.Random;

import fr.labri.starnet.Message.Type;
import fr.labri.starnet.models.EnergyModel;

public interface INode {
	Address getAddress();
	
	void send(Message msg);
	void sendTo(Address addr, Message msg);
	
	/**
	 * Low-level send
	 * @param power % of power used (i.e., in the range [0..1])
	 * @param msg to send
	 */
	void send(double power, Message msg);

	Message[] receive();
	
	Descriptor getDescriptor();
	double getPowerLevel();

	OrientedPosition getPosition();
	void move(OrientedPosition position);
	Map<String, Object> getStorage();
	
	boolean isOnline();
	
	MessageFactory newMessage();
	
	public interface Descriptor {
		double getEmissionRange();
		double getEmissionWindow();
		double getMaxPower();
		EnergyModel getEneryModel();
	}
	
	public interface Observer {
		void messageReceived(INode receiver);
		void messageSent(INode sender, double range);
	}

	public interface MessageFactory {
		public Message create(Type type);
		public Message create(Type type, Address dest);
		public Message create(Type type, Address to, Map<String, Object> data);
		public Message create(Type type, Address to, int payload);
		public Message create(Type type, Address to, int payload, Map<String, Object> data);

		public Message from(Message msg);
		public Message from(Message msg, Map<String, Object> data);
	}
	
	public long getTime();
	public Random getRandom();
}
