package fr.labri.starnet;

import fr.labri.tima.ITimedAutomata;
import fr.labri.tima.Executor;
import fr.labri.tima.ITimedAutomata.ContextProvider;

public class TimedAutomataPolicy implements RoutingPolicy {
	final Executor<INode> _executor;
	final INode _node;

	TimedAutomataPolicy(ITimedAutomata<INode> automata, INode node) {
		_executor = new Executor<>(new ContextProvider<INode>(){
			@Override
			public INode getContext() {
				return _node;
			}
			
		});
		_node = node;
	}
	
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
	public void maintain(INode node) {
		_executor.next();
	}
}
