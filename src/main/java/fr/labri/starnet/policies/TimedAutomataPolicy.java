package fr.labri.starnet.policies;

import fr.labri.starnet.Address;
import fr.labri.starnet.INode;
import fr.labri.starnet.Message;
import fr.labri.starnet.RoutingPolicy;
import fr.labri.tima.BasicExecutor;
import fr.labri.tima.ITimedAutomata;
import fr.labri.tima.ITimedAutomata.Executor;
import fr.labri.tima.ITimedAutomata.ContextProvider;

public class TimedAutomataPolicy implements RoutingPolicy {
	final Executor<INode> _executor;

	public TimedAutomataPolicy(Executor<INode> executor) {
		_executor = executor;
	}
	
	public TimedAutomataPolicy(ITimedAutomata<INode> automata, final INode node) {
		_executor = new BasicExecutor<>(new ContextProvider<INode>(){
			@Override
			public INode getContext() {
				return node;
			}
			
		});
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
