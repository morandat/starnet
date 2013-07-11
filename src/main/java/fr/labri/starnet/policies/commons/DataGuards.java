package fr.labri.starnet.policies.commons;

import fr.labri.starnet.INode;
import fr.labri.timedautomata.TimedAutomata.TransitionAdapter;

public class DataGuards {
		
	public static class IsAlreadyReceived extends TransitionAdapter<INode> {
		public IsAlreadyReceived() {}
		@Override
		public boolean isValid(INode context) {
			return Utils.isContained(context.getStorage(),CommonVar.CURRENT_MESSAGE,CommonVar.DATA_SET);
		}
	}
	
	public static class IsAlreadyForwarded extends TransitionAdapter<INode> {
		public IsAlreadyForwarded() {}
		@Override
		public boolean isValid(INode context) {
			return Utils.isContained(context.getStorage(),CommonVar.CURRENT_MESSAGE,CommonVar.FORWARDED_MESSAGE_SET);
		}
	}
}
