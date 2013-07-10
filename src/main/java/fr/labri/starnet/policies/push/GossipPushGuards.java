package fr.labri.starnet.policies.push;

import java.util.Collection;
import java.util.Map;

import fr.labri.starnet.INode;
import fr.labri.starnet.Message;
import fr.labri.starnet.policies.commons.CommonVar;
import fr.labri.timedautomata.TimedAutomata.TransitionAdapter;

public class GossipPushGuards {
			
	public static class ShouldForwardMsg extends TransitionAdapter<INode> {
		Map<String,Object> storage;
		Collection<Message> data_set;
		@SuppressWarnings("unchecked")
		@Override
		public boolean isValid(INode context) {
			storage= context.getStorage();
			Message msg = (Message) storage.get(CommonVar.CURRENT_MESSAGE);
			data_set=(Collection<Message>) storage.get(CommonVar.HELLO_SET);
			Integer ttl=(Integer) msg.getField(CommonVar.TTL);
			if(data_set.contains(msg)&&(ttl.intValue()>0))
				return true;
			return false;
		}
	}
}
