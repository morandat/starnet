package fr.labri.starnet.policies.push;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;

import fr.labri.starnet.INode;
import fr.labri.starnet.Message;
import fr.labri.starnet.policies.commons.HelloSet;
import fr.labri.starnet.policies.commons.actions.CommonVar;
import fr.labri.starnet.policies.push.GossipPushActions;
import fr.labri.timedautomata.ITimedAutomata;
import fr.labri.timedautomata.TimedAutomata.StateAdapter;

public class GossipPushActions {

	public static class InitEnv extends StateAdapter<INode> {
		public Map<String, Object> storage;

		@Override
		public void preAction(INode context, ITimedAutomata<INode> auto) {
			storage = context.getStorage();
			storage.put(CommonVar.HELLO_SET, new HelloSet());
			storage.put(CommonVar.SAVED_MAILBOX,
					new ArrayDeque<Message>());
		}
	}

	public static class SaveMailBox extends StateAdapter<INode> {
		public Map<String, Object> storage;

		@Override
		public void postAction(INode context, ITimedAutomata<INode> auto) {
			storage = context.getStorage();
			Message[] r = context.receive();
			Deque<Message> stack = new ArrayDeque<Message>(Arrays.asList(r));
			storage.put(CommonVar.SAVED_MAILBOX, stack);
		}
	}

	public static class AddToHelloSet extends StateAdapter<INode> {
		Map<String, Object> storage;
		HelloSet hello_set;

		@Override
		public void postAction(INode context, ITimedAutomata<INode> auto) {
			storage = context.getStorage();
			Message msg = (Message) storage
					.get(CommonVar.CURRENT_MESSAGE);
			hello_set = (HelloSet) storage.get(CommonVar.HELLO_SET);
			hello_set.add(msg);
		}
	}

	public static class AddToDataSet extends StateAdapter<INode> {
		Map<String, Object> storage;
		HelloSet data_set;

		@Override
		public void postAction(INode context, ITimedAutomata<INode> auto) {
			storage = context.getStorage();
			Message msg = (Message) storage
					.get(CommonVar.CURRENT_MESSAGE);
			data_set = (HelloSet) storage.get(CommonVar.DATA_SET);
			data_set.add(msg);
		}
	}
	
	public static class CleanHelloSet extends StateAdapter<INode> {
		Map<String, Object> storage;
		HelloSet hello_set;
		
		@Override
		public void postAction(INode context, ITimedAutomata<INode> auto) {
			storage = context.getStorage();
			hello_set = (HelloSet) storage.get(CommonVar.HELLO_SET);
			hello_set.clean(CommonVar.TIMEOUT, context.getTime());
		}
	}

		public static class DecreaseTTL extends StateAdapter<INode> {
			Map<String, Object> storage;
			Map<String, Object> fields;

			@Override
			public void postAction(INode context, ITimedAutomata<INode> auto) {
				int newttl;
				int oldttl;
				storage = context.getStorage();
				Message msg = (Message) storage
						.get(CommonVar.CURRENT_MESSAGE);
				oldttl = (Integer) msg.getField(CommonVar.TTL);
				fields = new HashMap<String, Object>();
				newttl = oldttl--;
				fields.put(CommonVar.TTL, newttl);
				msg = context.forwardMessage(msg, fields);
				storage.put(CommonVar.CURRENT_MESSAGE, msg);
			}
		}

		public static class ForwardMsgToRandomNeighbors extends
				StateAdapter<INode> {
			Map<String, Object> storage;
			HelloSet hello_set;
			Message selectedMsg;
			double distance;
			double power;

			@Override
			public void postAction(INode context, ITimedAutomata<INode> auto) {
				storage = context.getStorage();
				hello_set = (HelloSet) storage.get(CommonVar.HELLO_SET);
				Random rand = new Random();
				int rnd = rand.nextInt(hello_set.size());
				selectedMsg = hello_set.get(rnd);
				distance = context.getPosition().getNorm(
						selectedMsg.getSenderPosition());
				power = distance / context.getDescriptor().getEmissionRange();
				context.send(power, (Message)storage.get(CommonVar.CURRENT_MESSAGE));
			}
		}

		public static class SendHello extends StateAdapter<INode> {
			@Override
			public void postAction(INode context, ITimedAutomata<INode> auto) {
				context.send(context.createMessage(Message.Type.HELLO));
			}

		}
	}
