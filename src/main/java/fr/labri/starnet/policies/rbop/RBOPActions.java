package fr.labri.starnet.policies.rbop;


import java.util.Collection;

import fr.labri.starnet.INode;
import fr.labri.starnet.Message;
import fr.labri.starnet.policies.commons.BasicActions;
import fr.labri.starnet.policies.commons.CommonVar;
import fr.labri.starnet.policies.commons.DataSet;
import fr.labri.starnet.policies.commons.MessageSet;
import fr.labri.starnet.policies.commons.NeighborGraph;
import fr.labri.starnet.policies.commons.Utils;
import fr.labri.timedautomata.ITimedAutomata.ActionAdapter;

public class RBOPActions {
	
	public static class InitEnv extends BasicActions.InitEnv {
		@Override
		public void preAction(INode context, String key) {
			super.preAction(context, key);
			storage.put(CommonVar.FORWARDED_MESSAGE_SET, new DataSet());
		}
	}
			
	public static class ForwardMsg extends ActionAdapter<INode> {
		Collection<Message> forward_set;
		@Override
		public void postAction(INode context, String key) {
			//get the furthest neighbors to send the message with the adequate range
		}
	}
	
	public static class UpdateNeighbors extends ActionAdapter<INode> {
		NeighborGraph rng;
		@Override
		public void postAction(INode context, String key) {
			rng=(NeighborGraph) context.getStorage().get(RBOPVar.NEIGHBOR_GRAPH);
			
			//remove nodes that received the current msg from the associated neighborhood list
		}
	}
	
	public static class DoRngProcess extends ActionAdapter<INode> {
		NeighborGraph rng;
		@SuppressWarnings("unchecked")
		@Override
		public void postAction(INode context, String key) {
			
			rng=Utils.doRng((MessageSet<Message>)context.getStorage().get(CommonVar.HELLO_SET),
								context.getPosition(), 
								context.getAddress(), 
								context.getDescriptor().getEmissionRange(), 
								context.getDescriptor().getMaxPower());
			context.getStorage().put(RBOPVar.NEIGHBOR_GRAPH, rng);
		}
	}
	
	public static class SaveMsg extends ActionAdapter<INode> {
		@Override
		public void postAction(INode context, String key) {
			//save msg to be forwarded later
		}
	}	
}
