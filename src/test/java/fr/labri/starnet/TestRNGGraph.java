package fr.labri.starnet;

import edu.uci.ics.jung.graph.DirectedSparseGraph;
import fr.labri.starnet.policies.commons.GraphEdge;
import fr.labri.starnet.policies.commons.GraphNode;
import fr.labri.starnet.policies.commons.NeighborGraph;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: jbourcie
 * Date: 11/07/13
 * Time: 17:44
 * To change this template use File | Settings | File Templates.
 */
public class TestRNGGraph {

    public static void main (String[] args){

        DirectedSparseGraph<GraphNode, GraphEdge> g = new DirectedSparseGraph<GraphNode, GraphEdge>();



        GraphNode g1 = new GraphNode(new Position(3,3), new Address() {
            @Override
            public int asInt() {
                return 1;  //To change body of implemented methods use File | Settings | File Templates.
            }
        });
        g.addVertex(g1);
        GraphNode g2 = new GraphNode(new Position(1,5), new Address() {
            @Override
            public int asInt() {
                return 2;  //To change body of implemented methods use File | Settings | File Templates.
            }
        });
        g.addVertex(g2);
        GraphNode g3 = new GraphNode(new Position(1,1), new Address() {
            @Override
            public int asInt() {
                return 3;  //To change body of implemented methods use File | Settings | File Templates.
            }
        });
        g.addVertex(g3);
        GraphNode g4 = new GraphNode(new Position(4,2), new Address() {
            @Override
            public int asInt() {
                return 4;  //To change body of implemented methods use File | Settings | File Templates.
            }
        });
        g.addVertex(g4);
        GraphNode g5 = new GraphNode(new Position(4,0), new Address() {
            @Override
            public int asInt() {
                return 5;  //To change body of implemented methods use File | Settings | File Templates.
            }
        });
        g.addVertex(g5);
        GraphNode g6 = new GraphNode(new Position(5,4), new Address() {
            @Override
            public int asInt() {
                return 6;  //To change body of implemented methods use File | Settings | File Templates.
            }
        });
        g.addVertex(g6);

        // add the robot in the graph

        // add all the possible edge in the graph
        for (GraphNode gn1 : g.getVertices()){
            for (GraphNode gn2 : g.getVertices()){
                double distance = gn1.getPosition().getNorm(gn2.getPosition());
                if (gn1.getAddress().asInt()!= gn2.getAddress().asInt() && distance <= 5){
                    GraphEdge ge = new GraphEdge(distance/1, distance);
                    g.addEdge(ge , gn1, gn2);
                }
            }
        }

        NeighborGraph ng = NeighborGraph.createNeighborGraph(g, g1);
        System.out.println("Before RNG");
        System.out.println(ng.toString());

        ng.transfromRNG();
        System.out.println("After RNG");
        System.out.println(ng.toString());

        System.out.println("IS 2 neighbors of 1? ");
        System.out.println(ng.isNeighbor(g2.getAddress()));
        System.out.println("IS 3 neighbors of 1? ");
        System.out.println(ng.isNeighbor(g3.getAddress()));
        System.out.println("IS 4 neighbors of 1? ");
        System.out.println(ng.isNeighbor(g4.getAddress()));
        System.out.println("IS 5 neighbors of 1? ");
        System.out.println(ng.isNeighbor(g5.getAddress()));
        System.out.println("IS 6 neighbors of 1? ");
        System.out.println(ng.isNeighbor(g6.getAddress()));

        Collection<Address> addressCollection = new ArrayList<Address>();
        addressCollection.add(g5.getAddress());
        System.out.println("Power needed without G5 : " +ng.getPowerToReachFurthestNeighborWithout(addressCollection)) ;
        System.out.println("IsStillNeighbors needed without G5 : " +ng.isStillRngNodes(addressCollection)) ;

        addressCollection.add(g2.getAddress());
        System.out.println("Power needed without G5, G2 : " +ng.getPowerToReachFurthestNeighborWithout(addressCollection)) ;
        System.out.println("IsStillNeighbors needed without G5,G2 : " +ng.isStillRngNodes(addressCollection)) ;

        addressCollection.add(g3.getAddress());
        System.out.println("Power needed without G5, G2, G3 : " +ng.getPowerToReachFurthestNeighborWithout(addressCollection)) ;
        System.out.println("IsStillNeighbors needed without G5, G2, G3 : " +ng.isStillRngNodes(addressCollection)) ;

        addressCollection.add(g6.getAddress());
        System.out.println("Power needed without G5, G2, G3, G6 : " +ng.getPowerToReachFurthestNeighborWithout(addressCollection)) ;
        System.out.println("IsStillNeighbors needed without G5, G2, G3, G6 : " +ng.isStillRngNodes(addressCollection)) ;

        addressCollection.add(g4.getAddress());
        System.out.println("Power needed without G5, G2, G3, G6, G4 : " +ng.getPowerToReachFurthestNeighborWithout(addressCollection)) ;
        System.out.println("IsStillNeighbors needed without G5, G2, G3, G6, G4 : " +ng.isStillRngNodes(addressCollection)) ;
    }
}
