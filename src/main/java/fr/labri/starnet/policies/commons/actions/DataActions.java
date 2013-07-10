package fr.labri.starnet.policies.commons.actions;

import java.util.Map;

import fr.labri.starnet.INode;
import fr.labri.starnet.Message;
import fr.labri.starnet.policies.commons.HelloSet;
import fr.labri.timedautomata.ITimedAutomata;
import fr.labri.timedautomata.TimedAutomata.StateAdapter;

public class DataActions {

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
}
