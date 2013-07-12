package fr.labri.starnet.policies.commons;

import com.sun.javafx.collections.transformation.SortedList;
import edu.uci.ics.jung.algorithms.shortestpath.PrimMinimumSpanningTree;
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
        String result = "";
        for (GraphNode gn : neighborGraph.getVertices()){
            result+="Node " + gn.getAddress().asInt() + ", pos(" + gn.getPosition().getX() +","+gn.getPosition().getY()+")\n";
            for (GraphEdge ge : neighborGraph.getOutEdges(gn)){
                result += "\tEdge to " + neighborGraph.getDest(ge).getAddress().asInt() +" dist = " + ge.getDistance() + "\n";
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




    public void transformKruskal() {

        // List of edges sorted
        SortedSet<GraphEdge> edges = new TreeSet<GraphEdge>(new Comparator<GraphEdge>() {
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

        // Collections giving the set of connex nodes
        Collection<Collection<GraphNode>> connexCompo = new ArrayList<Collection<GraphNode>>();

        // Kruskal Tree
        Graph<GraphNode, GraphEdge> kruskalTree = new DirectedSparseGraph<GraphNode, GraphEdge>();

        GraphNode sourceNode;
        GraphNode extNode;
        GraphEdge currentEdge;

        //edges init
        edges.addAll(neighborGraph.getEdges());

        // tree init
        for (GraphNode gn : neighborGraph.getVertices()){
            kruskalTree.addVertex(gn);
        }

        // connexCompo initialisation
        Collection<GraphNode> ensSommet = neighborGraph.getVertices();
        for (GraphNode somCourant : ensSommet) {
            Collection<GraphNode> temp = new ArrayList<GraphNode>();
            temp.add(somCourant);
            connexCompo.add(temp);
        }
        // boucle qui trouve les aretes de l'arbre
        while (connexCompo.size() > 1 && !edges.isEmpty()) {
            currentEdge = edges.first();
            // weight
            sourceNode = (GraphNode) neighborGraph.getSource(currentEdge);
            extNode = (GraphNode) neighborGraph.getDest(currentEdge);

            if (!belongsToConnexCompo(connexCompo, sourceNode, extNode)) {
                fusionComposanteConnexe(connexCompo, sourceNode, extNode);


                kruskalTree.addEdge(new GraphEdge(currentEdge.getPower(), currentEdge.getDistance()), neighborGraph.getSource(currentEdge), neighborGraph.getDest(currentEdge));
                kruskalTree.addEdge(new GraphEdge(currentEdge.getPower(), currentEdge.getDistance()), neighborGraph.getDest(currentEdge), neighborGraph.getSource(currentEdge));
            }
            edges.remove(currentEdge);

        }

        neighborGraph = kruskalTree;
    }

    private boolean belongsToConnexCompo(Collection<Collection<GraphNode>> connexCompo, GraphNode sourceNode, GraphNode destNode) {

        for (Collection<GraphNode> graphNodeCollection : connexCompo){
            boolean find1 = false, find2 = false;
            for (GraphNode gn : graphNodeCollection){
                if (gn.getAddress().asInt() == sourceNode.getAddress().asInt()){
                    find1 = true;
                }
                if (gn.getAddress().asInt() == destNode.getAddress().asInt()){
                    find2 = true;
                }
                if (find1 && find2){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * fusionne les deux composants connexes de somOri et somExt qui sont
     * distinctes
     *
     * @param sourceNode
     *            Ã©quivalent du x
     * @param destNode
     *            Ã©quivalent du y
     */
    private void fusionComposanteConnexe(Collection<Collection<GraphNode>> connexCompo, GraphNode sourceNode, GraphNode destNode) {

        Collection<GraphNode> sourceGraphNodes = null;
        Collection<GraphNode> destGraphNodes = null;
        for (Collection<GraphNode> graphNodes : connexCompo){
            for (GraphNode gn : graphNodes){
                if (gn.getAddress().asInt() == sourceNode.getAddress().asInt()){
                    sourceGraphNodes = graphNodes;
                }
                if (gn.getAddress().asInt() == destNode.getAddress().asInt()){
                    destGraphNodes = graphNodes;
                }
                if (sourceGraphNodes!=null && destGraphNodes!=null){
                    break;
                }
            }
        }

        if (sourceGraphNodes != null && destGraphNodes != null){
            sourceGraphNodes.addAll(destGraphNodes);
            connexCompo.remove(destGraphNodes);
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

}
