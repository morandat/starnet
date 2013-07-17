package fr.labri.starnet.policies.commons;

import edu.uci.ics.jung.algorithms.shortestpath.DijkstraDistance;
import edu.uci.ics.jung.algorithms.shortestpath.PrimMinimumSpanningTree;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;
import fr.labri.starnet.Address;
import fr.labri.starnet.Message;
import fr.labri.starnet.Position;
import org.apache.commons.collections15.Factory;
import org.apache.commons.collections15.Transformer;

import java.awt.*;
import java.util.*;
import java.util.List;

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

    public static NeighborGraph createNeighborGraph(Graph<GraphNode, GraphEdge> neighborGraph, GraphNode gn) {
        for (GraphNode gn1 : neighborGraph.getVertices()) {
            if (gn1.getAddress() == gn.getAddress()) {
                return new NeighborGraph(gn1, neighborGraph);
            }
        }
        return null;

    }

    private NeighborGraph(GraphNode gn, Graph<GraphNode, GraphEdge> neighborGraph) {
        this.neighborGraph = neighborGraph;
        this.currentNode = gn;


    }

    public void transfromRNG() {
        List<GraphEdge> toRemove = new ArrayList<GraphEdge>();
        for (GraphEdge graphEdge : neighborGraph.getEdges()) {
            Pair<GraphNode> graphNodePair = neighborGraph.getEndpoints(graphEdge);
            GraphNode gn1 = graphNodePair.getFirst();
            GraphNode gn2 = graphNodePair.getSecond();
            for (GraphEdge graphEdge2 : neighborGraph.getOutEdges(gn1)) {
                GraphNode gn3 = neighborGraph.getDest(graphEdge2);
                GraphEdge graphEdge3 = neighborGraph.findEdge(gn2, gn3);
                if (graphEdge3 != null) {
                    if (graphEdge2.getPower() < graphEdge.getPower() && graphEdge3.getPower() < graphEdge.getPower()) {
                        toRemove.add(graphEdge);
                    }
                }


                if (!graphEdge.equals(graphEdge2) && graphEdge.getPower() < graphEdge2.getPower()) {

                }
            }
        }

        for (GraphEdge ge : toRemove) {
            neighborGraph.removeEdge(ge);
        }
    }

    public boolean isNeighbor(Address address){
        for (GraphNode gn : neighborGraph.getNeighbors(currentNode)){
            if (gn.getAddress() == address){
                return true;
            }
        }
        return false;
    }

    /**
     * return the power needed to reach the furthest node in the neighborhood after removing all the nodes contained in the addressCollection parameters
     *
     * @param addressCollection
     * @return
     */
    public double getPowerToReachFurthestNeighborWithout(Collection<Address> addressCollection) {
        double power = 0;

        for (GraphEdge ge : neighborGraph.getOutEdges(currentNode)){
             boolean find = false;
             for (Address address : addressCollection){
                 if (address == neighborGraph.getDest(ge).getAddress()){
                     find = true;
                 }
             }
             if (!find && ge.getPower()>power){
                 power  = ge.getPower();
             }
        }

        return power;
    }


    public Collection<Address> getReceivedNeighbors(Message m) {
        Position originPosition = m.getSenderPosition();
        double originDistance = originPosition.getNorm(currentNode.getPosition());
        Collection<Address> results = new ArrayList<Address>();

        for (GraphNode gn : neighborGraph.getNeighbors(currentNode)) {
            if (gn.getPosition().getNorm(originPosition) <= originDistance) {
                results.add(gn.getAddress());
            }
        }
        return results;
    }

    public String toString() {
        return toString(neighborGraph);
    }

    private String toString(Graph<GraphNode, GraphEdge> graph) {
        String result = "";
        for (GraphNode gn : graph.getVertices()){
            result+="Node " + gn.getAddress() + ", pos(" + gn.getPosition().getX() +","+gn.getPosition().getY()+")\n";
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
                 if (ad == gn.getAddress()){
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

    public void transformPrim() {
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

    private void addAllReverseEdge() {
        ArrayList<GrahEdgeWithNodes> toAdd = new ArrayList<GrahEdgeWithNodes>();
        for (GraphEdge ge : neighborGraph.getEdges()) {
            GraphNode sourceNode = neighborGraph.getSource(ge);
            GraphNode destNode = neighborGraph.getDest(ge);
            toAdd.add(new GrahEdgeWithNodes(new GraphEdge(ge.getPower(), ge.getDistance()), destNode, sourceNode));
        }

        for (GrahEdgeWithNodes gewn : toAdd) {
            neighborGraph.addEdge(gewn.getEdge(), gewn.getSourceNode(), gewn.getDestNode());
        }
    }


    private class GrahEdgeWithNodes {
        private GraphEdge edge;
        private GraphNode sourceNode;
        private GraphNode destNode;

        public GrahEdgeWithNodes(GraphEdge edge, GraphNode source, GraphNode dest) {
            this.edge = edge;
            this.sourceNode = source;
            this.destNode = dest;
        }

        public GraphEdge getEdge() {
            return this.edge;
        }

        public GraphNode getSourceNode() {
            return this.sourceNode;
        }

        public GraphNode getDestNode() {
            return this.destNode;
        }
    }

    public void transformKConnected(int k) {
        // List of edges sorted
        ArrayList<GraphEdge> edges = new ArrayList<>();

        // Kruskal Tree
        Graph<GraphNode, GraphEdge> kruskalTree = new DirectedSparseGraph<GraphNode, GraphEdge>();

        //edges init
        edges.addAll(neighborGraph.getEdges());
        Collections.sort(edges, new Comparator<GraphEdge>() {
            @Override
            public int compare(GraphEdge o1, GraphEdge o2) {
                GraphEdge ge1 = (GraphEdge) o1;
                GraphEdge ge2 = (GraphEdge) o2;
                return Double.compare(ge1.getPower(), ge2.getPower());
            }

            @Override
            public boolean equals(Object obj) {
                return false;
            }
        });

        // tree init
        for (GraphNode gn : neighborGraph.getVertices()) {
            kruskalTree.addVertex(gn);
        }
//        int i = 0;
        for(GraphEdge edge : edges){
//            i++;
            GraphNode sourceNode = neighborGraph.getSource(edge);
            GraphNode destNode = neighborGraph.getDest(edge);
            if (!isKConnected(k, kruskalTree, sourceNode, destNode)) {
                kruskalTree.addEdge(edge, sourceNode, destNode);
            } else {
                if (isKConnected(k, kruskalTree)) {
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
                    if (gn1.getAddress() != gn2.getAddress()){
                        if (alg.getDistance(gn1, gn2) == null){
                            return false;
                        }
                    }
                }
            }
            return true;
        }
        for (GraphNode gn : graph.getVertices()) {
            //remove and store the edges to be added back later
            Graph<GraphNode, GraphEdge> graph2 = copyGraph(graph);
            for (GraphEdge graphEdge : graph2.getIncidentEdges(gn)) {
                graph2.removeEdge(graphEdge);
            }
            graph2.removeVertex(gn);
            if (!isKConnected(k - 1, graph2)) {
                return false;
            }
        }
        return true;
    }

    public boolean isKConnected(int k) {
        return isKConnected(k, neighborGraph);
    }

    private boolean isKConnected(int k, Graph<GraphNode, GraphEdge> graph, GraphNode gn1, GraphNode gn2) {
        DijkstraDistance<GraphNode, GraphEdge> alg = new DijkstraDistance<GraphNode, GraphEdge>(graph);
        //System.out.println("k = " + k + ", gn1 = " + gn1.getAddress().asInt() + ", gn2 = " + gn2.getAddress().asInt());
        // System.out.println(toString(graph));
        if (k == 0) {
            return alg.getDistance(gn1, gn2) != null;
        }

        if (alg.getDistance(gn1, gn2) == null) {
            return false;
        }
        for (GraphNode gn : graph.getVertices()){
            if (!(gn.getAddress() == gn1.getAddress() || gn.getAddress() == gn2.getAddress()))   {
                //remove and store the edges to be added back later
                Graph<GraphNode, GraphEdge> graph2 = copyGraph(graph);
                for (GraphEdge graphEdge : graph2.getIncidentEdges(gn)) {
                    graph2.removeEdge(graphEdge);
                }
                graph2.removeVertex(gn);
                if (!isKConnected(k - 1, graph2, gn1, gn2)) {
                    return false;
                }
            }
        }
        return true;
    }


    private Graph<GraphNode, GraphEdge> copyGraph(Graph<GraphNode, GraphEdge> graph) {
        Graph<GraphNode, GraphEdge> results = new DirectedSparseGraph<GraphNode, GraphEdge>();
        for (GraphNode graphNode : graph.getVertices()) {
            results.addVertex(graphNode);
        }
        for (GraphEdge graphEdge : graph.getEdges()) {
            results.addEdge(graphEdge, graph.getSource(graphEdge), graph.getDest(graphEdge));
        }
        return results;
    }


    public void tranformToDelaunayTriangulation() {
        if (neighborGraph.getVertexCount() < 3) {
            return;
        }
        Graph<GraphNode, GraphEdge> delaunayGraph = new DirectedSparseGraph<GraphNode, GraphEdge>();
        Collection<GraphNode> vertices = neighborGraph.getVertices();
        int count = 0;
        ArrayList<Point> initPoints = new ArrayList<Point>();
        Triangulation triangulation = new Triangulation();
        for (GraphNode graphNode : neighborGraph.getVertices()) {
            delaunayGraph.addVertex(graphNode);
            Point toAdd = new Point(graphNode.getPosition().getX(), graphNode.getPosition().getY());
            triangulation.insertPoint(toAdd);
        }
        // add the delaunay edge in the delaunay graph.
        for (Point[] quadEdge : triangulation.computeEdges()) {

            GraphNode originNode = null, destNode = null;
            Point originPoint = quadEdge[0];
            Point destPoint = quadEdge[1];
            System.out.println(originPoint + ", " + destPoint);
            for (GraphNode graphNode : delaunayGraph.getVertices()) {
                if (originPoint.getX() == graphNode.getPosition().getX() && originPoint.getY() == graphNode.getPosition().getY()) {
                    //System.out.println("Origin found");
                    originNode = graphNode;
                }
                if (destPoint.getX() == graphNode.getPosition().getX() && destPoint.getY() == graphNode.getPosition().getY()) {
                    //System.out.println("dest found");
                    destNode = graphNode;
                }
                if (destNode != null && originNode != null) {
                    //System.out.println("Both Found");
                    for (GraphEdge graphEdge : neighborGraph.getOutEdges(originNode)) {
                        if (neighborGraph.getDest(graphEdge).getAddress() == destNode.getAddress()) {
                            //System.out.println("Edge Found");
                            delaunayGraph.addEdge(graphEdge, originNode, destNode);
                            for (GraphEdge graphEdge1 : neighborGraph.getOutEdges(destNode)) {
                                if (neighborGraph.getDest(graphEdge1).getAddress() == originNode.getAddress()) {
                                    delaunayGraph.addEdge(graphEdge1, destNode, originNode);
                                    destNode = null;
                                    originNode = null;
                                    break;
                                }
                            }
                            break;
                        }

                    }
                }

            }


        }
        neighborGraph = delaunayGraph;
    }

}
