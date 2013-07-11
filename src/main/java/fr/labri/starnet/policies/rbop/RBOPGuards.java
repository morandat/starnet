package fr.labri.starnet.policies.rbop;

import fr.labri.starnet.INode;
import fr.labri.timedautomata.TimedAutomata.TransitionAdapter;

public class RBOPGuards {
	
	public static class IsRngNeighbor extends TransitionAdapter<INode> {
		public IsRngNeighbor() {}
		@Override
		public boolean isValid(INode context) {
			//if the emitter is a RNG neighbor remove it from the RNG neihbor list
			return true;
		}
	}
	
	public static class ShouldUpdateMST extends TransitionAdapter<INode> {
		public ShouldUpdateMST() {}
		@Override
		public boolean isValid(INode context) {
			//Get Hello Messages and say if greater to one that we can update MST
			return true;
		}
	}

	public static class NeighborsSetIsEmpty extends TransitionAdapter<INode> {
		public NeighborsSetIsEmpty() {}
		@Override
		public boolean isValid(INode context) {
			return true;
		}	
	}
}
