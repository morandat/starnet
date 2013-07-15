package fr.labri.starnet.policies.push;

import java.util.Map;

import fr.labri.starnet.INode;
import fr.labri.starnet.Message;
import fr.labri.starnet.policies.commons.CommonVar;
import fr.labri.starnet.policies.commons.DataSet;
import fr.labri.tima.ITimedAutomata.PredicateAdapter;

public class GossipPushGuards {
			
	public static class ShouldForwardMsg extends PredicateAdapter<INode> {
		Map<String,Object> storage;
		DataSet data_set;
		@Override
		public boolean isValid(INode context, String key) {
			storage= context.getStorage();
			Message msg = (Message) storage.get(CommonVar.CURRENT_MESSAGE);
			data_set=(DataSet) storage.get(CommonVar.DATA_SET);
			Integer ttl=(Integer) msg.getField(CommonVar.TTL);
			if(data_set.contains(msg)&&(ttl.intValue()>0))
				return true;
			return false;
		}
	}
}
