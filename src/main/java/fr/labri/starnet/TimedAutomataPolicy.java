package fr.labri.starnet;

import fr.labri.timedautomata.ITimedAutomata;

public class TimedAutomataPolicy implements RoutingPolicy {
	final ITimedAutomata<INode> _automata;
	final INode _node;

	TimedAutomataPolicy(ITimedAutomata<INode> automata, INode node) {
		_automata = automata;
		_node = node;
	}
	
	@Override
	public void send(Message msg) {
		_node.send(1, msg);
	}

	@Override
	public void sendTo(Address addr, Message msg) {
		if(msg.getReceiverAddress() != _node.getAddress())
			_node.send(1, msg);
	}

	@Override
	public void maintain() {
		_automata.nextState();
	}
}
