package fr.labri.starnet.policies.flssk;


import fr.labri.starnet.INode;
import fr.labri.starnet.Message;
import fr.labri.tima.ITimedAutomata.ActionAdapter;


public class ProcessMstActions {
	
	public static class DoProcessAndFlushHelloSet extends ActionAdapter<INode> {
		@Override
		public void postAction(INode context, String key) {
			context.send(context.createMessage(Message.Type.HELLO));
		}
	}
	
	public static class ForwardCurrentStoredMsg extends ActionAdapter<INode> {
		@Override
		public void postAction(INode context, String key) {
			context.send(context.createMessage(Message.Type.HELLO));
		}
	}
	
}
