package fr.labri.starnet;

import fr.labri.tima.ITimedAutomata;
import fr.labri.tima.Executor;
import fr.labri.tima.ITimedAutomata.ContextProvider;

public class TimedAutomataPolicy implements RoutingPolicy {
	final Executor<INode> _automatas;
	final INode _node;

	TimedAutomataPolicy(ITimedAutomata<INode> automata, INode node) {
		_automatas = new Executor<>(new ContextProvider<INode>(){
			@Override
			public INode getContext() {
				return _node;
			}
			
		});
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
		_automatas.next();
	}
}
