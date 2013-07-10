package fr.labri.starnet.policies.pull;

import java.util.Map;
import java.util.Stack;

import fr.labri.starnet.INode;
import fr.labri.starnet.Message;
import fr.labri.timedautomata.TimedAutomata.TransitionAdapter;

public class GossipPullGuards {
	public static class MailboxIsNotEmpty extends TransitionAdapter<INode> {
		public MailboxIsNotEmpty() {}
		@Override
		public boolean isValid(INode context) {
			if (context.receive().length > 0)
			    return true;
            return false;
		}
	}
	public static class PopAndIsHelloMsg extends TransitionAdapter<INode> {
		public PopAndIsHelloMsg() {}
		@Override
		public boolean isValid(INode context) {
            Map<String,Object> storage = context.getStorage();
            if (!storage.containsKey(GossipPullActions.SAVED_MAILBOX)) {
                return false;
            } else{
                Stack savedMailbox = (Stack)storage.get(GossipPullActions.SAVED_MAILBOX);
                Message m = (Message)savedMailbox.peek();
                if (m.getType().equals(Message.Type.HELLO)){
                    Message popedMessage = (Message)savedMailbox.pop();
                    storage.put(GossipPullActions.CURRENT_MESSAGE, popedMessage);
                    return true;
                }
            }
			return false;
		}
	}
	
	public static class PopAndIsDataMsg extends TransitionAdapter<INode> {
		public PopAndIsDataMsg() {}
		@Override
		public boolean isValid(INode context) {
            Map<String,Object> storage = context.getStorage();
            if (!storage.containsKey(GossipPullActions.SAVED_MAILBOX)) {
                return false;
            } else{
                Stack savedMailbox = (Stack)storage.get(GossipPullActions.SAVED_MAILBOX);
                Message m = (Message)savedMailbox.peek();
                if (m.getType().equals(Message.Type.DATA)){
                    Message popedMessage = (Message)savedMailbox.pop();
                    storage.put(GossipPullActions.CURRENT_MESSAGE, popedMessage);
                    return true;
                }
            }
            return false;
		}
	}
	
	public static class PopAndIsProbeMsg extends TransitionAdapter<INode> {
		public PopAndIsProbeMsg() {}
	
		@Override
		public boolean isValid(INode context) {
            Map<String,Object> storage = context.getStorage();
            if (!storage.containsKey(GossipPullActions.SAVED_MAILBOX)) {
                return false;
            } else{
                Stack savedMailbox = (Stack)storage.get(GossipPullActions.SAVED_MAILBOX);
                Message m = (Message)savedMailbox.peek();
                if (m.getType().equals(Message.Type.PROBE)){
                    Message popedMessage = (Message)savedMailbox.pop();
                    storage.put(GossipPullActions.CURRENT_MESSAGE, popedMessage);
                    return true;
                }
            }
            return false;
		}
	}
}
