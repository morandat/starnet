package fr.labri.starnet.policies.push;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;

import fr.labri.starnet.INode;
import fr.labri.starnet.Message;
import fr.labri.timedautomata.ITimedAutomata;
import fr.labri.timedautomata.TimedAutomata.StateAdapter;

public class GossipPushActions {
	
	public static final String SAVED_MAILBOX ="saved_mailbox";
	public static final String CURRENT_MESSAGE ="current";
	public static final String HELLO_SET ="hello_set";
	public static final String DATA_SET ="data_set";
	public static final String TTL ="ttl";
	
	public static class SaveMailBox extends StateAdapter<INode> {
		public  Map<String,Object> storage;	
		@Override
		public void postAction(INode context,ITimedAutomata<INode> auto) {
			storage= context.getStorage();
			Message[] r = context.receive();
			Deque<Message> stack = new ArrayDeque<Message>(Arrays.asList(r));
			storage.put(GossipPushActions.SAVED_MAILBOX, stack);			
		}
	}
	
	public static class AddToHelloSet extends StateAdapter<INode> {
		Map<String,Object> storage;
		Collection<Message> hello_set;
		@SuppressWarnings("unchecked")
		@Override
		public void postAction(INode context,ITimedAutomata<INode> auto) {
			storage= context.getStorage();
			Message msg = (Message) storage.get(GossipPushActions.CURRENT_MESSAGE);
			hello_set=(Collection<Message>) storage.get(GossipPushActions.HELLO_SET);
			if(hello_set==null){
				hello_set= new HashSet<>();
			}
			hello_set.add(msg);
		}
	}
	
	public static class FlushHelloSet extends StateAdapter<INode> {
		Map<String,Object> storage;
		Collection<Message> hello_set;
		@SuppressWarnings("unchecked")
		@Override
		public void postAction(INode context,ITimedAutomata<INode> auto) {
			storage= context.getStorage();
			hello_set=(Collection<Message>) storage.get(GossipPushActions.HELLO_SET);
			if(hello_set!=null)
				hello_set.clear();
		}
	}
	
	public static class DecreaseTTL extends StateAdapter<INode> {
		Map<String,Object> storage;
		Map<String,Object> fields;
		Collection<Message> hello_set;
		@Override
		public void postAction(INode context,ITimedAutomata<INode> auto) {
			int newttl;
			int oldttl;
			storage= context.getStorage();
			Message msg = (Message) storage.get(GossipPushActions.CURRENT_MESSAGE);
			oldttl=(Integer) msg.getField(GossipPushActions.TTL);
			fields=new HashMap<String, Object>(); 
			newttl = oldttl --;
			fields.put(GossipPushActions.TTL, newttl);
			msg = context.forwardMessage(msg,fields);
			storage.put(GossipPushActions.CURRENT_MESSAGE, msg);
			}
	}

	public static class ForwardMsgToRandomNeighbors extends StateAdapter<INode> {
		Map<String,Object> storage;
		Collection<Message> hello_set;
		Message selectedMsg;
		double distance;
		double power;
		@SuppressWarnings("unchecked")
		@Override
		public void postAction(INode context, ITimedAutomata<INode> auto) {
			storage= context.getStorage();
			hello_set=(Collection<Message>) storage.get(GossipPushActions.HELLO_SET);
			List<Message> msgList = new ArrayList<Message>(hello_set);
			Random rand = new Random(); 
			int tmp=rand.nextInt(msgList.size()); 
			selectedMsg=msgList.get(tmp);
			distance=context.getPosition().getNorm(selectedMsg.getSenderPosition());
			power=distance/context.getDescriptor().getEmissionRange();
			context.send(power,context.createMessage(Message.Type.HELLO));
		}
	}	
		
	public static class SendHello extends StateAdapter<INode> {
		@Override
		public void postAction(INode context, ITimedAutomata<INode> auto) {
			context.send(context.createMessage(Message.Type.HELLO));
		}
	}	
}
