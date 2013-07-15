package fr.labri.starnet.policies.rbop;

import fr.labri.starnet.INode;
import fr.labri.starnet.Message;
import fr.labri.starnet.policies.commons.CommonVar;
import fr.labri.starnet.policies.commons.NeighborGraph;
import fr.labri.tima.ITimedAutomata.PredicateAdapter;

public class RBOPGuards {
	
	public static class IsRngNeighbor extends PredicateAdapter<INode> {
		public IsRngNeighbor() {}
		@Override
		public boolean isValid(INode context, String key) {
			NeighborGraph rng = (NeighborGraph) context.getStorage().get(RBOPVar.NEIGHBOR_GRAPH);
			Message msg=(Message) context.getStorage().get(CommonVar.CURRENT_MESSAGE);
			return rng.isNeighbor(msg.getSenderAddress());
		}
	}
	
	public static class ShouldUpdateMST extends PredicateAdapter<INode> {
		public ShouldUpdateMST() {}
		@Override
		public boolean isValid(INode context, String key) {
			//Get Hello Messages and say if greater to one that we can update MST
			return true;
		}
	}

	public static class NeighborsSetIsEmpty extends PredicateAdapter<INode> {
		public NeighborsSetIsEmpty() {}
		@Override
		public boolean isValid(INode context, String key) {
			NeighborGraph rng = (NeighborGraph) context.getStorage().get(RBOPVar.NEIGHBOR_GRAPH);
			return false;
		}	
	}
}
