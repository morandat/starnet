package fr.labri.starnet;

public interface Message {
	enum Type { HELLO, DATA };
	
	long getMessageID();
	
	Message getParent();
	
	Type getType();
	int getSize();
	long getEmitTime();
	
	Address getSenderAddress();
	Position getSenderPosition();
	
	Address getReceiverAddress();
	
	int getHops();
	
	<A> A getField(String name);
}