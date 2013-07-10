package fr.labri.starnet.policies.commons.guards;

import java.util.Deque;
import java.util.Map;

import fr.labri.starnet.INode;
import fr.labri.starnet.Message;
import fr.labri.starnet.policies.commons.actions.CommonVar;
import fr.labri.timedautomata.TimedAutomata.TransitionAdapter;

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
	
	@SuppressWarnings("unchecked")
	static public boolean isEmpty(Map<String,Object> storage, String setName){
		Deque<Message> mailbox = (Deque<Message>) storage.get(setName);
		return mailbox.isEmpty();
	}
	
	public static class PopAndIsHelloMsg extends TransitionAdapter<INode> {
		public PopAndIsHelloMsg() {}
		@Override
		public boolean isValid(INode context) {
			return popIfTypeAndSetCurrent(context.getStorage(), Message.Type.HELLO);
		}
	}
	
	public static class PopAndIsDataMsg extends TransitionAdapter<INode> {
		public PopAndIsDataMsg() {}	
		@Override
		public boolean isValid(INode context) {
			return popIfTypeAndSetCurrent(context.getStorage(), Message.Type.DATA);
		}
	}	
	
	public static class PopAndIsProbeMsg extends TransitionAdapter<INode> {
		public PopAndIsProbeMsg() {}	
		@Override
		public boolean isValid(INode context) {
			return popIfTypeAndSetCurrent(context.getStorage(), Message.Type.PROBE);
		}
	}	
		
	public static class IsEmpty extends TransitionAdapter<INode> {
		public IsEmpty() {}
		@Override
		public boolean isValid(INode context) {
			return isEmpty(context.getStorage(),CommonVar.SAVED_MAILBOX);
		}
	}
	
	public static class IsNotEmpty extends TransitionAdapter<INode> {
		public IsNotEmpty() {}
		@Override
		public boolean isValid(INode context) {
			return !isEmpty(context.getStorage(),CommonVar.SAVED_MAILBOX);
		}
	}
}
