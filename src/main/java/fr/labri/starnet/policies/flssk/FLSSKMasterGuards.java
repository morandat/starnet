package fr.labri.starnet.policies.flssk;

import fr.labri.tima.ITimedAutomata.PredicateAdapter;
import fr.labri.starnet.INode;
import fr.labri.starnet.Message;

public class FLSSKMasterGuards {
	public static class ShouldForwardMsgNow extends PredicateAdapter<INode> {
		public ShouldForwardMsgNow() {}
		@Override
		public boolean isValid(INode context, String key) {
			context.receive();
			return true;
		}
	}
	public static class ShouldForwardMsgLater extends PredicateAdapter<INode> {
		public ShouldForwardMsgLater() {}
		@Override
		public boolean isValid(INode context, String key) {
			
			return false;
		}
	}
	
	public static class PopAndIsHelloMsg extends PredicateAdapter<INode> {
		public PopAndIsHelloMsg() {}
		@Override
		public boolean isValid(INode context, String key) {
			Message[] r = context.receive();
			for(Message m: r)
				if(m.getType() == Message.Type.HELLO)
					//Ajouter les voisins dans un graph
					return true;
			return false;
		}
	}
	
	public static class PopAndIsDataMsg extends PredicateAdapter<INode> {
		public PopAndIsDataMsg() {}
	
		@Override
		public boolean isValid(INode context, String key) {
			Message[] r = context.receive();
			for(Message m: r)
				if(m.getType() == Message.Type.HELLO)
					//Ajouter les voisins dans un graph
					return true;
			return false;		
			}
	}
	
	public static class MailBoxEmpty extends PredicateAdapter<INode> {
		public MailBoxEmpty() {}
		@Override
		public boolean isValid(INode context, String key) {
			
			return false;
		}
	}
	
	public static class NeighborsIsNotEmpty extends PredicateAdapter<INode> {
		public NeighborsIsNotEmpty() {}
		@Override
		public boolean isValid(INode context, String key) {
			
			return false;
		}
	}
	
	public static class AlreadyReceivedMsg extends PredicateAdapter<INode> {
		public AlreadyReceivedMsg() {}
		@Override
		public boolean isValid(INode context, String key) {
			
			return true;
		}
	}
	
	public static class ShouldUpdateMST extends PredicateAdapter<INode> {
		public ShouldUpdateMST() {}
		@Override
		public boolean isValid(INode context, String key) {
			//Get Hello Messages and say if greater to one that we can update MST
			return true;
		}
	}

	public static class ShouldIgnoreMsg extends PredicateAdapter<INode> {
		public ShouldIgnoreMsg() {}
		@Override
		public boolean isValid(INode context, String key) {
			return true;
		}
	}
	public static class ShouldNotIgnoreMsg extends PredicateAdapter<INode> {
		public ShouldNotIgnoreMsg() {}
		@Override
		public boolean isValid(INode context, String key) {
			return true;
		}
	}
	public static class NeighborsIsEmpty extends PredicateAdapter<INode> {
		public NeighborsIsEmpty() {}
		@Override
		public boolean isValid(INode context, String key) {
			return true;
		}	
	}
}
