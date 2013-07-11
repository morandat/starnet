package fr.labri.starnet.policies.flssk;

import fr.labri.timedautomata.TimedAutomata.TransitionAdapter;
import fr.labri.starnet.INode;
import fr.labri.starnet.Message;

public class FLSSKMasterGuards {
	public static class ShouldForwardMsgNow extends TransitionAdapter<INode> {
		public ShouldForwardMsgNow() {}
		@Override
		public boolean isValid(INode context) {
			context.receive();
			return true;
		}
	}
	public static class ShouldForwardMsgLater extends TransitionAdapter<INode> {
		public ShouldForwardMsgLater() {}
		@Override
		public boolean isValid(INode context) {
			
			return false;
		}
	}
	
	public static class PopAndIsHelloMsg extends TransitionAdapter<INode> {
		public PopAndIsHelloMsg() {}
		@Override
		public boolean isValid(INode context) {
			Message[] r = context.receive();
			for(Message m: r)
				if(m.getType() == Message.Type.HELLO)
					//Ajouter les voisins dans un graph
					return true;
			return false;
		}
	}
	
	public static class PopAndIsDataMsg extends TransitionAdapter<INode> {
		public PopAndIsDataMsg() {}
	
		@Override
		public boolean isValid(INode context) {
			Message[] r = context.receive();
			for(Message m: r)
				if(m.getType() == Message.Type.HELLO)
					//Ajouter les voisins dans un graph
					return true;
			return false;		
			}
	}
	
	public static class MailBoxEmpty extends TransitionAdapter<INode> {
		public MailBoxEmpty() {}
		@Override
		public boolean isValid(INode context) {
			
			return false;
		}
	}
	
	public static class NeighborsIsNotEmpty extends TransitionAdapter<INode> {
		public NeighborsIsNotEmpty() {}
		@Override
		public boolean isValid(INode context) {
			
			return false;
		}
	}
	
	public static class AlreadyReceivedMsg extends TransitionAdapter<INode> {
		public AlreadyReceivedMsg() {}
		@Override
		public boolean isValid(INode context) {
			
			return true;
		}
	}
	
	public static class ShouldUpdateMST extends TransitionAdapter<INode> {
		public ShouldUpdateMST() {}
		@Override
		public boolean isValid(INode context) {
			//Get Hello Messages and say if greater to one that we can update MST
			return true;
		}
	}

	public static class ShouldIgnoreMsg extends TransitionAdapter<INode> {
		public ShouldIgnoreMsg() {}
		@Override
		public boolean isValid(INode context) {
			return true;
		}
	}
	public static class ShouldNotIgnoreMsg extends TransitionAdapter<INode> {
		public ShouldNotIgnoreMsg() {}
		@Override
		public boolean isValid(INode context) {
			return true;
		}
	}
	public static class NeighborsIsEmpty extends TransitionAdapter<INode> {
		public NeighborsIsEmpty() {}
		@Override
		public boolean isValid(INode context) {
			return true;
		}	
	}
}
