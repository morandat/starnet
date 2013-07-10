package fr.labri.starnet.policies.commons;

import java.util.Map;

import fr.labri.starnet.INode;
import fr.labri.starnet.Message;
import fr.labri.timedautomata.TimedAutomata.TransitionAdapter;

public class DataGuards {

	public static class AlreadyReceivedMsg extends TransitionAdapter<INode> {
		Map<String,Object> storage;
		DataSet data_set;
		public AlreadyReceivedMsg() {}
		@Override
		public boolean isValid(INode context) {
			storage= context.getStorage();
			Message msg = (Message) storage.get(CommonVar.CURRENT_MESSAGE);
			data_set=(DataSet) storage.get(CommonVar.DATA_SET);
			if(data_set.contains(msg))
				return true;
			return false;
		}
	}
}
