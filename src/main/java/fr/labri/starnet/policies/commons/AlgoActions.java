package fr.labri.starnet.policies.commons;


import edu.uci.ics.jung.graph.DirectedSparseGraph;
import fr.labri.starnet.INode;
import fr.labri.starnet.Message;
import fr.labri.timedautomata.ITimedAutomata;
import fr.labri.timedautomata.TimedAutomata.StateAdapter;


public class AlgoActions {
	public static final String NEIGHBOR_GRAPH = "neighbor_graph";


	public static class DoProcessAndFlushHelloSet extends StateAdapter<INode> {
		@Override
		public void postAction(INode context, ITimedAutomata<INode> auto) {
            //TODO add the Neighborgraph initialization in initEnv


            DirectedSparseGraph<GraphNode, GraphEdge> g = new DirectedSparseGraph<GraphNode, GraphEdge>();
            HelloSet hs = (HelloSet)context.getStorage().get(CommonVar.HELLO_SET);
            GraphNode currentNode = new GraphNode(context.getPosition(), context.getAddress());
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
                    if (gn1.getAddress().asInt()!= gn2.getAddress().asInt() && distance <= context.getDescriptor().getEmissionRange() * context.getDescriptor().getMaxPower()){
                        GraphEdge ge = new GraphEdge(distance/context.getDescriptor().getEmissionRange(), distance);
                        g.addEdge(ge , gn1, gn2);
                    }
                }
            }

            NeighborGraph ng = NeighborGraph.createNeighborGraph(g, currentNode);
            ng.transfromRNG();
            context.getStorage().put(AlgoActions.NEIGHBOR_GRAPH, ng);
		}
	}
	
	public static class ForwardCurrentStoredMsg extends StateAdapter<INode> {
		@Override
		public void postAction(INode context, ITimedAutomata<INode> auto) {
			context.send(context.createMessage(Message.Type.HELLO));
		}
	}
	
}
