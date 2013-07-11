package fr.labri.starnet.policies.commons;

import fr.labri.starnet.INode;
import fr.labri.timedautomata.ITimedAutomata;
import fr.labri.timedautomata.TimedAutomata.StateAdapter;

public class DataActions {

	public static class AddToDataSet extends StateAdapter<INode> {
		@Override
		public void postAction(INode context, ITimedAutomata<INode> auto) {
			Utils.addToSet(context.getStorage(), CommonVar.DATA_SET, CommonVar.CURRENT_MESSAGE);
		}
	}
	
	public static class AddToForwardedSet extends StateAdapter<INode> {
		@Override
		public void postAction(INode context, ITimedAutomata<INode> auto) {
			Utils.addToSet(context.getStorage(), CommonVar.FORWARDED_MESSAGE_SET, CommonVar.CURRENT_MESSAGE);
		}
	}
	
}
