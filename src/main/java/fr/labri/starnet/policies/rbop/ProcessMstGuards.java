package fr.labri.starnet.policies.rbop;

import fr.labri.tima.ITimedAutomata.PredicateAdapter;
import fr.labri.starnet.INode;

public class ProcessMstGuards {
	public static class AtLeastOneHelloMsg extends PredicateAdapter<INode> {
		public AtLeastOneHelloMsg() {}
		@Override
		public boolean isValid(INode context, String key) {
			return true;
		}
	}
	public static class DoProcessAndFlushHelloSet extends PredicateAdapter<INode> {
		public DoProcessAndFlushHelloSet() {}
		@Override
		public boolean isValid(INode context, String key) {
			
			return false;
		}
	}
}
