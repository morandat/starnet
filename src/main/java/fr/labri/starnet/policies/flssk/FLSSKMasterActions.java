package fr.labri.starnet.policies.flssk;


import fr.labri.starnet.INode;
import fr.labri.starnet.Message;
import fr.labri.timedautomata.ITimedAutomata;
import fr.labri.timedautomata.TimedAutomata.StateAdapter;


public class FLSSKMasterActions {
	
	public static class SaveMsg extends StateAdapter<INode> {
		@Override
		public void eachAction(INode context,ITimedAutomata<INode> auto) {
			//Get messages and if type is Data, save current received messages for further processing
		}
	}
	
	public static class AddToHelloSet extends StateAdapter<INode> {
		@Override
		public void postAction(INode context,ITimedAutomata<INode> auto) {
			context.send(context.createMessage(Message.Type.DATA));
		}
	}
	
	public static class ForwardMsg extends StateAdapter<INode> {
		@Override
		public void postAction(INode context, ITimedAutomata<INode> auto) {
			context.send(context.createMessage(Message.Type.DATA));
		}
	}	
	public static class UpdateNeighbors extends StateAdapter<INode> {
		@Override
		public void postAction(INode context, ITimedAutomata<INode> auto) {
			context.send(context.createMessage(Message.Type.HELLO));
		}
	}	
}
