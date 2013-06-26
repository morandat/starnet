package fr.labri.starnet;

public interface RoutingPolicy {
	void send(Message msg);
	void sendTo(Address addr, Message msg);
	
	void maintain();
}
