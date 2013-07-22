package fr.labri.starnet.policies;

import fr.labri.starnet.Address;
import fr.labri.starnet.Message;
import fr.labri.starnet.INode;
import fr.labri.starnet.RoutingPolicy;

public class SimpleRouting implements RoutingPolicy {
	@Override
	public void send(INode node, Message msg) {
		node.send(1, msg);
	}

	@Override
	public void sendTo(INode node, Address addr, Message msg) {
		if(msg.getReceiverAddress() != node.getAddress())
			node.send(1, msg);
	}

	@Override
	public void maintain(INode node ) {
	}
}
