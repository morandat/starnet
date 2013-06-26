package fr.labri.starnet;

public interface NodeObserver {
	void messageReceived(INode receiver);
	void messageSent(INode sender, double range);
}
