package fr.labri.starnet.policies;


import fr.labri.starnet.INode;
import fr.labri.starnet.Message;
import fr.labri.timedautomata.TimedAutomata.StateAdapter;


public class RBOPActions {

	public static class SendHello extends StateAdapter<INode> {
		@Override
		public void postAction(INode context) {
			context.send(context.createMessage(Message.Type.HELLO));
		}
	}
	
	public static class SaveHello extends StateAdapter<INode> {
		@Override
		public void eachAction(INode context) {
			//Get messages and if type is Hello, save it for further processing
		}
	}
	
	public static class SaveMsg extends StateAdapter<INode> {
		@Override
		public void eachAction(INode context) {
			//Get messages and if type is Data, save current received messages for further processing
		}
	}
	
	public static class Mst extends StateAdapter<INode> {
		@Override
		public void postAction(INode context) {
			context.send(context.createMessage(Message.Type.DATA));
		}
	}
	public static class ForwardMsg extends StateAdapter<INode> {
		@Override
		public void postAction(INode context) {
			context.send(context.createMessage(Message.Type.DATA));
		}
	}	
	public static class UpdateNeighbors extends StateAdapter<INode> {
		@Override
		public void postAction(INode context) {
			context.send(context.createMessage(Message.Type.DATA));
		}
	}	
}
