package fr.labri.starnet;

import java.util.Map;

import fr.labri.starnet.Message.Type;

public interface INode {
	Address getAddress();
	
	void send(Message msg);
	void sendTo(Address addr, Message msg);
	Message[] receive();
	
	Descriptor getDescriptor();
	double getPowerLevel();

	OrientedPosition getPosition();
	void move(OrientedPosition position);
	
	boolean isOnline();
	
	interface Descriptor {
		int getEmissionRange();
		double getEmissionWindow();
		int getMaxPower();
		EnergyModel getEneryModel();
	}
	
	interface EnergyModel {
		double distance(double rangeMax, double power);
		double energy(double rangeMax, double range);
	}

	public Message createMessage(Type type);
	public Message createMessage(Type type, Address dest);
	public Message createMessage(Type type, Address to, Map<String, Object> data);
	public Message createMessage(Type type, Address to, int payload);
	public Message createMessage(Type type, Address to, int payload, Map<String, Object> data);

	public Message forwardMessage(Message msg);
	public Message forwardMessage(Message msg, Map<String, Object> data);

	int newMessageID();
}
