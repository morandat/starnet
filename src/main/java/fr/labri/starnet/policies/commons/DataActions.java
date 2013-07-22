package fr.labri.starnet.policies.commons;

import fr.labri.starnet.INode;
import fr.labri.tima.ITimedAutomata;

public class DataActions {

	public static class AddToDataSet extends ITimedAutomata.ActionAdapter<INode> {
		@Override
		public void postAction(INode context, String key) {
			System.out.println("AddToDataSet");
			Utils.addToSet(context.getStorage(), CommonVar.DATA_SET, CommonVar.CURRENT_MESSAGE);
		}
	}
	
	public static class AddToForwardedSet extends ITimedAutomata.ActionAdapter<INode> {
		@Override
		public void postAction(INode context, String key) {
			System.out.println("AddToForwardSet");
			Utils.addToSet(context.getStorage(), CommonVar.FORWARDED_MESSAGE_SET, CommonVar.CURRENT_MESSAGE);
		}
	}
	
}
