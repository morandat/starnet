package fr.labri.starnet.policies.rbop;


import java.util.Collection;
import java.util.Map;

import fr.labri.starnet.Address;
import fr.labri.starnet.INode;
import fr.labri.starnet.Message;
import fr.labri.starnet.policies.commons.BasicActions;
import fr.labri.starnet.policies.commons.CommonVar;
import fr.labri.starnet.policies.commons.DataSet;
import fr.labri.starnet.policies.commons.MessageSet;
import fr.labri.starnet.policies.commons.NeighborGraph;
import fr.labri.starnet.policies.commons.Utils;
import fr.labri.tima.ITimedAutomata.ActionAdapter;

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
		Map<String,Object> spawn_storage;
		Map<String,Object> node_storage;
		NeighborGraph rng;
		Message msg;
		Double power;
		@Override
		public void postAction(INode context, String key) {
			
			rng=(NeighborGraph) context.getStorage().get(RBOPVar.NEIGHBOR_GRAPH);
			
			if(key==null){
				msg=(Message) context.getStorage().get(CommonVar.CURRENT_MESSAGE);
			}else{
				spawn_storage=(Map<String, Object>) context.getStorage().get(key);
				msg=(Message) spawn_storage.get(CommonVar.CURRENT_MESSAGE);
				
			}
			power = rng.getPowerToReachFurthestNeighborWithout(rng.getReceivedNeighbors(msg));
			
		}
	}
	
	public static class UpdateRng extends ActionAdapter<INode> {
		NeighborGraph rng;
		@Override
		public void preAction(INode context, String key) {
			rng=(NeighborGraph) context.getStorage().get(RBOPVar.NEIGHBOR_GRAPH);
			
		}
	}
	
	public static class IsRngEmpty extends ActionAdapter<INode> {
		NeighborGraph rng;
		@Override
		public void preAction(INode context, String key) {
			rng=(NeighborGraph) context.getStorage().get(RBOPVar.NEIGHBOR_GRAPH);

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
}
