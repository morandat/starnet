package fr.labri.starnet.policies.commons;

import com.sun.javafx.collections.transformation.SortedList;
import edu.uci.ics.jung.algorithms.shortestpath.DijkstraDistance;
import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.algorithms.shortestpath.PrimMinimumSpanningTree;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;
import fr.labri.starnet.Address;
import fr.labri.starnet.Message;
import fr.labri.starnet.Position;
import org.apache.commons.collections15.Factory;
import org.apache.commons.collections15.Transformer;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: jbourcie
 * Date: 11/07/13
 * Time: 16:00
 * To change this template use File | Settings | File Templates.
 */
public class NeighborGraph {

    private GraphNode currentNode;

    private Graph<GraphNode, GraphEdge> neighborGraph;

    public static NeighborGraph createNeighborGraph(Graph<GraphNode, GraphEdge> neighborGraph, GraphNode gn){
        for (GraphNode gn1 : neighborGraph.getVertices()){
            if (gn1.getAddress().asInt() == gn.getAddress().asInt()){
                return new NeighborGraph(gn1, neighborGraph);
            }
        }
        return null;

    }

    private NeighborGraph(GraphNode gn, Graph<GraphNode, GraphEdge> neighborGraph){
        this.neighborGraph = neighborGraph;
        this.currentNode = gn;


    }

    public void transfromRNG(){
        List<GraphEdge> toRemove = new ArrayList<GraphEdge>();
        for (GraphEdge graphEdge : neighborGraph.getEdges()){
            Pair<GraphNode> graphNodePair = neighborGraph.getEndpoints(graphEdge);
            GraphNode gn1 = graphNodePair.getFirst();
            GraphNode gn2 = graphNodePair.getSecond();
            for (GraphEdge graphEdge2 : neighborGraph.getOutEdges(gn1)) {
                GraphNode gn3 = neighborGraph.getDest(graphEdge2);
                GraphEdge graphEdge3 = neighborGraph.findEdge(gn2, gn3);
                if (graphEdge3!=null){
                    if (graphEdge2.getPower()<graphEdge.getPower() && graphEdge3.getPower()<graphEdge.getPower()) {
                        toRemove.add(graphEdge);
                    }
                }


                if (!graphEdge.equals(graphEdge2) && graphEdge.getPower()<graphEdge2.getPower()){

                }
            }
        }

        for (GraphEdge ge : toRemove){
            neighborGraph.removeEdge(ge);
        }
    }

    public boolean isNeighbor(Address address){
        for (GraphNode gn : neighborGraph.getNeighbors(currentNode)){
            if (gn.getAddress().asInt() == address.asInt()){
                return true;
            }
        }
        return false;
    }

    /**
     * return the power needed to reach the furthest node in the neighborhood after removing all the nodes contained in the addressCollection parameters
     * @param addressCollection
     * @return
     */
    public double getPowerToReachFurthestNeighborWithout(Collection<Address> addressCollection){
        double power = 0;

        for (GraphEdge ge : neighborGraph.getOutEdges(currentNode)){
             boolean find = false;
             for (Address address : addressCollection){
                 if (address.asInt() == neighborGraph.getDest(ge).getAddress().asInt()){
                     find = true;
                 }
             }
             if (!find && ge.getPower()>power){
                 power  = ge.getPower();
             }
        }

        return power;
    }


    public Collection<Address> getReceivedNeighbors(Message m){
        Position originPosition = m.getSenderPosition();
        double originDistance = originPosition.getNorm(currentNode.getPosition());
        Collection<Address> results= new ArrayList<Address>();

        for (GraphNode gn : neighborGraph.getNeighbors(currentNode)){
            if (gn.getPosition().getNorm(originPosition)<=originDistance){
                results.add(gn.getAddress());
            }
        }
        return results;
    }

    public String toString(){
        return toString(neighborGraph);
    }

    private String toString(Graph<GraphNode,GraphEdge> graph){
        String result = "";
        for (GraphNode gn : graph.getVertices()){
            result+="Node " + gn.getAddress().asInt() + ", pos(" + gn.getPosition().getX() +","+gn.getPosition().getY()+")\n";
            for (GraphEdge ge : graph.getOutEdges(gn)){
                result += "\tEdge to " + graph.getDest(ge).getAddress().asInt() +" dist = " + ge.getDistance() + "\n";
            }
        }
        return result;
    }

    public boolean isStillRngNodes(Collection<Address> addressCollection){
         for (GraphNode gn : neighborGraph.getNeighbors(currentNode)){
             boolean find = false;
             for (Address ad : addressCollection){
                 if (ad.asInt() == gn.getAddress().asInt()){
                     find = true;
                     break;
                 }
             }
             if (find==false){
                 return false;
             }
         }
        return true;
    }

    public void transformPrim(){
        PrimMinimumSpanningTree<GraphNode, GraphEdge> pmst = new PrimMinimumSpanningTree<GraphNode, GraphEdge>(
        new Factory<DirectedSparseGraph<GraphNode, GraphEdge>>() {
            @Override
            public DirectedSparseGraph<GraphNode, GraphEdge> create() {
                return new DirectedSparseGraph<GraphNode, GraphEdge>();  //To change body of implemented methods use File | Settings | File Templates.
            }
        }, new Transformer<GraphEdge, Double>() {
            @Override
            public Double transform(GraphEdge graphEdge) {
                return graphEdge.getPower();
            }
        }
        );

        neighborGraph = pmst.transform(neighborGraph);
        addAllReverseEdge();
    }

    private void addAllReverseEdge(){
        ArrayList<GrahEdgeWithNodes> toAdd = new ArrayList<GrahEdgeWithNodes>();
        for (GraphEdge ge : neighborGraph.getEdges()){
            GraphNode sourceNode = neighborGraph.getSource(ge);
            GraphNode destNode = neighborGraph.getDest(ge);
            toAdd.add(new GrahEdgeWithNodes(new GraphEdge(ge.getPower(), ge.getDistance()), destNode, sourceNode));
        }

        for (GrahEdgeWithNodes gewn : toAdd){
            neighborGraph.addEdge(gewn.getEdge(), gewn.getSourceNode(), gewn.getDestNode());
        }
    }


    private class GrahEdgeWithNodes {
        private GraphEdge edge;
        private GraphNode sourceNode;
        private GraphNode destNode;

        public GrahEdgeWithNodes(GraphEdge edge, GraphNode source, GraphNode dest){
            this.edge = edge;
            this. sourceNode = source;
            this.destNode = dest;
        }

        public GraphEdge getEdge(){
            return this.edge;
        }

        public GraphNode getSourceNode () {
            return this.sourceNode;
        }

        public GraphNode getDestNode(){
            return this.destNode;
        }
    }

    public void transformKConnected(int k){
        // List of edges sorted
        ArrayList<GraphEdge> edges = new ArrayList<>();

        // Kruskal Tree
        Graph<GraphNode, GraphEdge> kruskalTree = new DirectedSparseGraph<GraphNode, GraphEdge>();

        //edges init
        edges.addAll(neighborGraph.getEdges());
        Collections.sort(edges, new Comparator<GraphEdge>() {
            @Override
            public int compare(GraphEdge o1, GraphEdge o2) {
                GraphEdge ge1 = (GraphEdge)o1;
                GraphEdge ge2 = (GraphEdge) o2;
                return Double.compare(ge1.getPower(), ge2.getPower());
            }

            @Override
            public boolean equals(Object obj) {
                return false;
            }
        });

        // tree init
        for (GraphNode gn : neighborGraph.getVertices()){
            kruskalTree.addVertex(gn);
        }
        int i = 0;
        for(GraphEdge edge : edges){
            i++;
            GraphNode sourceNode = neighborGraph.getSource(edge);
            GraphNode destNode = neighborGraph.getDest(edge);
            if (!isKConnected(k, kruskalTree, sourceNode, destNode)){
                kruskalTree.addEdge(edge, sourceNode, destNode);
            } else {
                if (isKConnected(k, kruskalTree)){
                    neighborGraph = kruskalTree;
                    break;
                }
            }


        }
    }

    private boolean isKConnected(int k, Graph<GraphNode, GraphEdge> graph){
        if (k == 0){
            DijkstraDistance<GraphNode,GraphEdge> alg = new DijkstraDistance<GraphNode, GraphEdge>(graph);
            for (GraphNode gn1 : graph.getVertices()){
                for (GraphNode gn2 : graph.getVertices()){
                    if (gn1.getAddress().asInt() != gn2.getAddress().asInt()){
                        if (alg.getDistance(gn1, gn2) == null){
                            return false;
                        }
                    }
                }
            }
            return true;
        }
        for (GraphNode gn : graph.getVertices()){
            //remove and store the edges to be added back later
            Graph<GraphNode, GraphEdge> graph2 = copyGraph(graph);
            for (GraphEdge graphEdge : graph2.getIncidentEdges(gn)){
                graph2.removeEdge(graphEdge);
            }
            graph2.removeVertex(gn);
            if (!isKConnected(k-1, graph2)){
                return false;
            }
        }
        return true;
    }

    public boolean  isKConnected(int k){
        return isKConnected(k, neighborGraph);
    }

    private boolean isKConnected(int k, Graph<GraphNode, GraphEdge> graph, GraphNode gn1, GraphNode gn2){
        DijkstraDistance<GraphNode,GraphEdge> alg = new DijkstraDistance<GraphNode, GraphEdge>(graph);
        //System.out.println("k = " + k + ", gn1 = " + gn1.getAddress().asInt() + ", gn2 = " + gn2.getAddress().asInt());
       // System.out.println(toString(graph));
        if (k == 0){
            return alg.getDistance(gn1, gn2)!=null;
        }

        if (alg.getDistance(gn1, gn2) == null){
            return false;
        }
        for (GraphNode gn : graph.getVertices()){
            if (!(gn.getAddress().asInt() == gn1.getAddress().asInt() || gn.getAddress().asInt() == gn2.getAddress().asInt()))   {
                //remove and store the edges to be added back later
                Graph<GraphNode, GraphEdge> graph2 = copyGraph(graph);
                for (GraphEdge graphEdge : graph2.getIncidentEdges(gn)){
                    graph2.removeEdge(graphEdge);
                }
                graph2.removeVertex(gn);
                if (!isKConnected(k-1, graph2, gn1, gn2)){
                    return false;
                }
            }
        }
        return true;
    }


    private Graph<GraphNode,GraphEdge> copyGraph(Graph<GraphNode,GraphEdge> graph) {
        Graph<GraphNode, GraphEdge> results = new DirectedSparseGraph<GraphNode, GraphEdge>();
        for (GraphNode graphNode : graph.getVertices()) {
            results.addVertex(graphNode);
        }
        for (GraphEdge graphEdge : graph.getEdges()) {
            results.addEdge(graphEdge, graph.getSource(graphEdge), graph.getDest(graphEdge));
        }
        return results;
    }

}
