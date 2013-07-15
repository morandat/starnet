package fr.labri.starnet.policies.flssk;


import fr.labri.starnet.INode;
import fr.labri.starnet.Message;
import fr.labri.tima.ITimedAutomata.ActionAdapter;


public class HeartBeatActions {
	
	public static class SendHello extends ActionAdapter<INode> {
		@Override
		public void postAction(INode context, String key) {
			context.send(context.createMessage(Message.Type.HELLO));
		}
	}
}
