package fr.labri.starnet.policies.flssk;

import fr.labri.timedautomata.TimedAutomata.TransitionAdapter;
import fr.labri.starnet.INode;

public class ProcessMstGuards {
	public static class AtLeastOneHelloMsg extends TransitionAdapter<INode> {
		public AtLeastOneHelloMsg() {}
		@Override
		public boolean isValid(INode context) {
			return true;
		}
	}
	public static class DoProcessAndFlushHelloSet extends TransitionAdapter<INode> {
		public DoProcessAndFlushHelloSet() {}
		@Override
		public boolean isValid(INode context) {
			
			return false;
		}
	}
}
