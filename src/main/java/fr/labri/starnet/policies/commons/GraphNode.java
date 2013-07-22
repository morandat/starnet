package fr.labri.starnet.policies.commons;

import fr.labri.starnet.Address;
import fr.labri.starnet.Position;

/**
 * Created with IntelliJ IDEA.
 * User: jbourcie
 * Date: 11/07/13
 * Time: 12:09
 * To change this template use File | Settings | File Templates.
 */
public class GraphNode {

    private Position pos;
    private Address ad;

    public GraphNode(Position pos, Address ad){
        this.pos = pos;
        this.ad = ad;
    }

    public Position getPosition(){
        return this.pos;
    }

    public Address getAddress(){
        return this.ad;
    }
}
