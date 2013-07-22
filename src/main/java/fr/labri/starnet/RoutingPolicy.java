package fr.labri.starnet;

public interface RoutingPolicy {
	void send(INode node, Message msg);
	void sendTo(INode node, Address addr, Message msg);
	
	void maintain(INode node);
}
