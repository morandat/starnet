package fr.labri.starnet.policies.commons;

import java.util.Collection;
import java.util.Map;

import edu.uci.ics.jung.graph.DirectedSparseGraph;
import fr.labri.starnet.Address;
import fr.labri.starnet.Message;
import fr.labri.starnet.Position;

public class Utils {
	
	static public boolean isEmpty(Map<String,Object> storage, String setName){
		Collection<?> set = (Collection<?>) storage.get(setName);
		return set.isEmpty();
	}
	
	static public boolean isContained(Map<String,Object> storage, String setName,String msgName){
		Object obj = storage.get(msgName);
		Collection<?> set= (Collection<?>) storage.get(setName);
		if(set.contains(obj))
			return true;
		return false;
	}
	
	@SuppressWarnings("unchecked")
	static public void addToSet(Map<String,Object> storage, String setName,String msgName){	
		Object msg =  storage.get(msgName);
		Collection<Message> set = (Collection<Message>) storage.get(setName);
		set.add((Message) msg);
	}
	
	static public NeighborGraph doRng(MessageSet<Message> hs, Position myPos,Address myAddr, double emissionRange, double maxPower)
	{
        DirectedSparseGraph<GraphNode, GraphEdge> g = new DirectedSparseGraph<GraphNode, GraphEdge>();
        GraphNode currentNode = new GraphNode(myPos, myAddr);
        g.addVertex(currentNode);

        // add the robot in the graph
        for (Message m : hs.getAll()){
            GraphNode gn = new GraphNode(m.getSenderPosition(), m.getSenderAddress());
            g.addVertex(gn);
        }

        // add all the possible edge in the graph
        for (GraphNode gn1 : g.getVertices()){
            for (GraphNode gn2 : g.getVertices()){
                double distance = gn1.getPosition().getNorm(gn2.getPosition());
                if (gn1.getAddress() != gn2.getAddress() && distance <= emissionRange * maxPower){
                    GraphEdge ge = new GraphEdge(distance/emissionRange, distance);
                    g.addEdge(ge , gn1, gn2);
                }
            }
        }

        NeighborGraph ng = NeighborGraph.createNeighborGraph(g, currentNode);
        ng.transfromRNG();
		return ng;

	}
}
