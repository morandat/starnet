package fr.labri.starnet.policies.flssk;


import fr.labri.starnet.INode;
import fr.labri.starnet.Message;
import fr.labri.timedautomata.ITimedAutomata;
import fr.labri.timedautomata.TimedAutomata.StateAdapter;


public class ForwardMsgActions {
	
	public static class StoreCurrentMsg extends StateAdapter<INode> {
		@Override
		public void postAction(INode context, ITimedAutomata<INode> auto) {
			context.send(context.createMessage(Message.Type.HELLO));
		}
	}
	
	
	
	
}
