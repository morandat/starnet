package fr.labri.starnet.policies.rbop;

import java.util.Collection;
import java.util.HashMap;

import fr.labri.starnet.Address;
import fr.labri.starnet.INode;
import fr.labri.starnet.Message;
import fr.labri.starnet.policies.commons.CommonVar;
import fr.labri.starnet.policies.commons.NeighborGraph;
import fr.labri.tima.ITimedAutomata.SpawnAdapter;

public class Identifier extends SpawnAdapter<INode> {
	@Override
	public String getSpawnerKey(INode context, String parentKey) {
		Message m = (Message)context.getStorage().get(CommonVar.CURRENT_MESSAGE);
		String id = ""+m.getMessageID();
		NeighborGraph ng = (NeighborGraph) context.getStorage().get(RBOPVar.NEIGHBOR_GRAPH);
		Collection<Address> ca = ng.getReceivedNeighbors(m);
		HashMap<String, Object> hm = new HashMap<String, Object>();
		hm.put(CommonVar.CURRENT_MESSAGE, m);
		hm.put(RBOPVar.NEIGHBOR_GRAPH_RECEIVED, ca);
		context.getStorage().put(id, hm);
		return id;
		
	}
}
