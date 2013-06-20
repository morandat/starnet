package fr.labri.starnet;

import fr.labri.starnet.Message.Type;

public interface Node {
	Address getAddress();
	
	void send(Message msg);
	void sendTo(Address addr, Message msg);
	Message[] receive();
	
	Descriptor getDescriptor();
	OrientedPosition getPosition();
	void setPosition(OrientedPosition newPosition);
	
	World getWorld();
	
	boolean isOnline();
	public void activate();
	public void perform();
	
	public void deliver(Message msg);
	
	public int newMessageID();
	
	interface NetworkManager {
		void send(double power, Message msg);
	}
	
	interface PolicyAdapter {
		RoutingPolicy adaptPolicy();
		RoutingPolicy getCurrentPolicy();
	}
	
	interface SensorAggregator {
		Sensor[] getSensors();
	}
	
	interface Descriptor {
		int getEmissionRange();
		double getEmissionWindow();
		int getMaxPower();
	}

	double getPowerLevel();
	void consumePower(double power);

	void setOnline(boolean b);

	Message newMessage(Type type, Address dst);
}
