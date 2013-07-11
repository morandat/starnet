package fr.labri.starnet.policies.rbop;

/**
 * Created with IntelliJ IDEA.
 * User: jbourcie
 * Date: 11/07/13
 * Time: 14:51
 * To change this template use File | Settings | File Templates.
 */
public class GraphEdge {
    private double power;
    private double distance;

    public GraphEdge(double power, double distance){
        this.distance = distance;
        this.power = power;
    }

    public double getPower(){
        return power;
    }

    public double getDistance(){
        return distance;
    }
}
