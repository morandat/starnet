package fr.labri.starnet.policies.rbop;

import fr.labri.starnet.INode;
import fr.labri.starnet.Message;
import fr.labri.starnet.policies.commons.CommonVar;
import fr.labri.starnet.policies.commons.NeighborGraph;
import fr.labri.timedautomata.TimedAutomata.TransitionAdapter;

public class RBOPGuards {
	
	public static class IsRngNeighbor extends TransitionAdapter<INode> {
		public IsRngNeighbor() {}
		@Override
		public boolean isValid(INode context) {
			NeighborGraph rng = (NeighborGraph) context.getStorage().get(RBOPVar.NEIGHBOR_GRAPH);
			Message msg=(Message) context.getStorage().get(CommonVar.CURRENT_MESSAGE);
			return rng.isNeighbor(msg.getSenderAddress());
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
			NeighborGraph rng = (NeighborGraph) context.getStorage().get(RBOPVar.NEIGHBOR_GRAPH);
			return false;
		}	
	}
}
