package fr.labri.starnet.policies.rbop;

import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.util.Pair;
import fr.labri.starnet.Address;
import fr.labri.starnet.Message;
import fr.labri.starnet.Position;

import java.util.ArrayList;
import java.util.Collection;
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

    private DirectedSparseGraph<GraphNode, GraphEdge> neighborGraph;

    public static NeighborGraph createNeighborGraph(DirectedSparseGraph<GraphNode, GraphEdge> neighborGraph, GraphNode gn){
        for (GraphNode gn1 : neighborGraph.getVertices()){
            if (gn1.getAddress().asInt() == gn.getAddress().asInt()){
                return new NeighborGraph(gn1, neighborGraph);
            }
        }
        return null;

    }

    private NeighborGraph(GraphNode gn, DirectedSparseGraph<GraphNode, GraphEdge> neighborGraph){
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
    public double getPowerToReachFurthestNeighbor(Collection<Address> addressCollection){
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


    public Collection<Address> getNonReceivedNeighbors(Message m){
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
}
