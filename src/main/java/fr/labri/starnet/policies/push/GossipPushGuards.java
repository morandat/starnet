package fr.labri.starnet.policies.push;

import java.util.Collection;
import java.util.Deque;
import java.util.HashSet;
import java.util.Map;

import fr.labri.starnet.INode;
import fr.labri.starnet.Message;
import fr.labri.timedautomata.TimedAutomata.TransitionAdapter;

public class GossipPushGuards {

	public static class PopAndIsHelloMsg extends TransitionAdapter<INode> {
		public  Map<String,Object> storage;
		public PopAndIsHelloMsg() {}
		@Override
		public boolean isValid(INode context) {
			storage =context.getStorage();
			@SuppressWarnings("unchecked")
			Deque<Message> mailbox = (Deque<Message>) storage.get(GossipPushActions.SAVED_MAILBOX);
			if(mailbox.peek().getType()==Message.Type.HELLO){
				storage.put(GossipPushActions.CURRENT_MESSAGE, mailbox.pop());
				return true;
			}
			return false;
		}
	}
	
	public static class PopAndIsDataMsg extends TransitionAdapter<INode> {
		public  Map<String,Object> storage;	
		Collection<Message> data_set;
		Message msg;
		public PopAndIsDataMsg() {}
		@SuppressWarnings("unchecked")
		@Override
		public boolean isValid(INode context) {
			storage =context.getStorage();
			Deque<Message> mailbox = (Deque<Message>) storage.get(GossipPushActions.SAVED_MAILBOX);
			
			if(mailbox.peek().getType()==Message.Type.DATA){
				Message msg=mailbox.pop();
				storage.put(GossipPushActions.CURRENT_MESSAGE, msg);
				data_set=(Collection<Message>) storage.get(GossipPushActions.DATA_SET);
				if(data_set==null){
					data_set= new HashSet<>();
				}
				data_set.add(msg);
				return true;
			}
			return false;
		}
	}
		
	public static class MailBoxIsNotEmpty extends TransitionAdapter<INode> {
		public  Map<String,Object> storage;	
		public MailBoxIsNotEmpty() {}
		@Override
		public boolean isValid(INode context) {
			storage =context.getStorage();
			@SuppressWarnings("unchecked")
			Deque<Message> mailbox = (Deque<Message>) storage.get(GossipPushActions.SAVED_MAILBOX);
			if (!mailbox.isEmpty())	
				return true;
			return false;
		}
	}
	
	public static class ShouldForwardMsg extends TransitionAdapter<INode> {
		Map<String,Object> storage;
		Collection<Message> data_set;
		@SuppressWarnings("unchecked")
		@Override
		public boolean isValid(INode context) {
			storage= context.getStorage();
			Message msg = (Message) storage.get(GossipPushActions.CURRENT_MESSAGE);
			data_set=(Collection<Message>) storage.get(GossipPushActions.HELLO_SET);
			Integer ttl=(Integer) msg.getField(GossipPushActions.TTL);
			if(data_set.contains(msg)&&(ttl.intValue()>0))
				return true;
			return false;
		}
	}
}
