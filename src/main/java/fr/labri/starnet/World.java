package fr.labri.starnet;

import java.util.List;

public interface World {
	Address register(Node node);
	
	void send(Node sender, double power, Message msg);
	
	void doTick();
	
	long getTime();

	List<Node> getParticipants();
	
	Position getDimension();
	void addObserver(NodeObserver obs);
}
