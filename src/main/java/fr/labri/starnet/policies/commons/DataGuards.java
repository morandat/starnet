package fr.labri.starnet.policies.commons;

import fr.labri.starnet.INode;
import fr.labri.tima.ITimedAutomata.PredicateAdapter;

public class DataGuards {
		
	public static class IsAlreadyReceived extends PredicateAdapter<INode> {
		public IsAlreadyReceived() {}
		@Override
		public boolean isValid(INode context, String key) {
			return Utils.isContained(context.getStorage(),CommonVar.CURRENT_MESSAGE,CommonVar.DATA_SET);
		}
	}
	
	public static class IsAlreadyForwarded extends PredicateAdapter<INode> {
		public IsAlreadyForwarded() {}
		@Override
		public boolean isValid(INode context, String key) {
			return Utils.isContained(context.getStorage(),CommonVar.CURRENT_MESSAGE,CommonVar.FORWARDED_MESSAGE_SET);
		}
	}
}
