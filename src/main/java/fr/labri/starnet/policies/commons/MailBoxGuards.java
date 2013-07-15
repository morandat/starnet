package fr.labri.starnet.policies.commons;

import java.util.Deque;
import java.util.Map;

import fr.labri.starnet.policies.commons.Utils;
import fr.labri.starnet.INode;
import fr.labri.starnet.Message;
import fr.labri.tima.ITimedAutomata.PredicateAdapter;

public class MailBoxGuards {
	
	static public boolean popIfTypeAndSetCurrent(Map<String,Object> storage, Message.Type type) {
			@SuppressWarnings("unchecked")
			Deque<Message> mailbox = (Deque<Message>) storage.get(CommonVar.SAVED_MAILBOX);
			if(mailbox.peek().getType()==type){
				storage.put(CommonVar.CURRENT_MESSAGE, mailbox.pop());
				return true;
			}
			return false;
		}
		
	public static class PopAndIsHelloMsg extends PredicateAdapter<INode> {
		public PopAndIsHelloMsg() {}
		@Override
		public boolean isValid(INode context, String key) {
			return popIfTypeAndSetCurrent(context.getStorage(), Message.Type.HELLO);
		}
	}
	
	public static class PopAndIsDataMsg extends PredicateAdapter<INode> {
		public PopAndIsDataMsg() {}	
		@Override
		public boolean isValid(INode context, String key) {
			return popIfTypeAndSetCurrent(context.getStorage(), Message.Type.DATA);
		}
	}	
	
	public static class PopAndIsProbeMsg extends PredicateAdapter<INode> {
		public PopAndIsProbeMsg() {}	
		@Override
		public boolean isValid(INode context, String key) {
			return popIfTypeAndSetCurrent(context.getStorage(), Message.Type.PROBE);
		}
	}	
		
	public static class IsEmpty extends PredicateAdapter<INode> {
		public IsEmpty() {}
		@Override
		public boolean isValid(INode context, String key) {
			return Utils.isEmpty(context.getStorage(),CommonVar.SAVED_MAILBOX);
		}
	}
	
	public static class IsNotEmpty extends PredicateAdapter<INode> {
		public IsNotEmpty() {}
		@Override
		public boolean isValid(INode context, String key) {
			return !Utils.isEmpty(context.getStorage(),CommonVar.SAVED_MAILBOX);
		}
	}
}
