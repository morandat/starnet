package fr.labri.starnet.policies.push;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import fr.labri.starnet.INode;
import fr.labri.starnet.Message;
import fr.labri.starnet.policies.commons.CommonVar;
import fr.labri.starnet.policies.commons.HelloSet;
import fr.labri.tima.ITimedAutomata.ActionAdapter;

public class GossipPushActions {

		public static class DecreaseTTL extends ActionAdapter<INode> {
			Map<String, Object> storage;
			Map<String, Object> fields;

			@Override
			public void postAction(INode context, String key) {
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

		public static class ForwardMsgToRandomNeighbors extends ActionAdapter<INode> {
			Map<String, Object> storage;
			HelloSet hello_set;
			Message selectedMsg;
			double distance;
			double power;

			@Override
			public void postAction(INode context, String key) {
				storage = context.getStorage();
				hello_set = (HelloSet) storage.get(CommonVar.HELLO_SET);
				Random rand = context.getRandom();
				int rnd = rand.nextInt(hello_set.size());
				selectedMsg = hello_set.get(rnd);
				distance = context.getPosition().getNorm(
						selectedMsg.getSenderPosition());
				power = distance / context.getDescriptor().getEmissionRange();
				context.send(power, (Message)storage.get(CommonVar.CURRENT_MESSAGE));
			}
		}

	}
