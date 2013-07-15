package fr.labri.starnet.policies.flssk;


import fr.labri.starnet.INode;
import fr.labri.starnet.Message;
import fr.labri.tima.ITimedAutomata.ActionAdapter;


public class FLSSKMasterActions {
	
	public static class SaveMsg extends ActionAdapter<INode> {
		@Override
		public void eachAction(INode context, String key) {
			//Get messages and if type is Data, save current received messages for further processing
		}
	}
	
	public static class AddToHelloSet extends ActionAdapter<INode> {
		@Override
		public void postAction(INode context, String key) {
			context.send(context.createMessage(Message.Type.DATA));
		}
	}
	
	public static class ForwardMsg extends ActionAdapter<INode> {
		@Override
		public void postAction(INode context, String key) {
			context.send(context.createMessage(Message.Type.DATA));
		}
	}	
	public static class UpdateNeighbors extends ActionAdapter<INode> {
		@Override
		public void postAction(INode context, String key) {
			context.send(context.createMessage(Message.Type.HELLO));
		}
	}	
}
