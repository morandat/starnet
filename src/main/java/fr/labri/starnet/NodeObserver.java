package fr.labri.starnet;

public interface NodeObserver {
	void messageReceived(Node receiver);
	void messageSent(Node sender, double range);
}
