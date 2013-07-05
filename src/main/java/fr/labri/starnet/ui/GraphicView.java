package fr.labri.starnet.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.swing.JPanel;

import fr.labri.IntBitSet;
import fr.labri.starnet.INode;
import fr.labri.starnet.Node;
import fr.labri.starnet.NodeObserver;
import fr.labri.starnet.OrientedPosition;
import fr.labri.starnet.Simulation;
import fr.labri.starnet.Simulation.State;
import fr.labri.starnet.SimulationObserver;
import fr.labri.starnet.INode.Descriptor;
import fr.labri.starnet.World;

public class GraphicView extends JPanel implements SimulationObserver, NodeObserver {
	private static final long serialVersionUID = 1L;
	public static final int NODE_SIZE = Integer.getInteger("starnet.node.size", 10);
	
	public static final Color NODE_COLOR = Color.getColor("starnet.node.color", Color.BLACK);
	public static final Color OFFLINE_NODE_COLOR = Color.getColor("starnet.offlinenode.color", Color.GRAY);
	public static final float[] RANGE_COLOR = Color.getColor("starnet.range.color", Color.GREEN).getColorComponents(null);
	public static final Color RANGE_COLOR_END = new Color(RANGE_COLOR[0], RANGE_COLOR[1], RANGE_COLOR[2], 0.2f);
	public static final Color RANGE_COLOR_START = RANGE_COLOR_END.darker();
	public static final Color TRANSMISSION_COLOR_END = Color.getColor("starnet.transmission.color", Color.RED);
	public static final Color TRANSMISSION_COLOR_START = TRANSMISSION_COLOR_END.darker();
	
	public static final long REFRESH_RATE = 10;

	final private List<Node> _participants;
	final private Dimension _worldDim;
	
	Set<Integer> received = Collections.synchronizedSet(new IntBitSet());
	Set<Integer> sending = Collections.synchronizedSet(new IntBitSet());

	Set<Integer> currentReceived = new IntBitSet();
	Set<Integer> currentSending = new IntBitSet();

	public GraphicView(Simulation simulation) {
		_participants = simulation.getWorld().getParticipants();
		_worldDim = simulation.getWorld().getDimension().toDimension();
		setPreferredSize(_worldDim);
	}

	
	@Override
	protected void paintComponent(Graphics g) {
        super.paintComponent(g);       
		
		for(Node n : _participants)
			drawRange(g, n);

		for(Node n : _participants)
			drawNode(g, n);
	}

	void drawRange(Graphics g, Node n) {
		if(!n.isOnline()) return;

		OrientedPosition pos = n.getPosition();
		Descriptor desc = n.getDescriptor();

		int r = (int)desc.getEmissionRange();
		int x = pos.getX();
		int y = pos.getY();

		double t = pos.getOrientation();
		double a = desc.getEmissionWindow();
		int a1 = (int)Math.round(Math.toDegrees(t - a / 2));
		
		if(currentSending.contains(n.getAddress().asInt())) // FIXME must deal with range
			((Graphics2D)g).setPaint(new GradientPaint(new Point2D.Double(x, y), TRANSMISSION_COLOR_START, new Point2D.Double(x + r * Math.sin(t), y + r * Math.cos(t)),  TRANSMISSION_COLOR_END));
		else
			((Graphics2D)g).setPaint(new GradientPaint(new Point2D.Double(x, y), RANGE_COLOR_START, new Point2D.Double(x + r * Math.sin(t), y + r * Math.cos(t)),  RANGE_COLOR_END));
		g.fillArc(x - r, y - r, 2*r, 2*r, a1, (int)Math.round(Math.toDegrees(a)));
	}
	
	void drawNode(Graphics g, Node n) {
		OrientedPosition pos = n.getPosition();
		int s = NODE_SIZE / 2;
		
		g.setColor(currentReceived.contains(n.getAddress().asInt()) ? TRANSMISSION_COLOR_END : (n.isOnline() ? NODE_COLOR: OFFLINE_NODE_COLOR));
		g.fillOval(pos.getX() - s, pos.getY() - s, NODE_SIZE, NODE_SIZE);
	}


	public void newtick(long time) {
		if(time % REFRESH_RATE > 0) return;
		flip();
		repaint();
	}
	
	void flip() {
		currentReceived = received;
		received = Collections.synchronizedSet(new IntBitSet());
		currentSending = sending;
		sending = Collections.synchronizedSet(new IntBitSet());
	}

	public void simulationStateChanged(State oldstate, State newState) {}


	public void messageReceived(INode receiver) {
		received.add(receiver.getAddress().asInt());
	}


	public void messageSent(INode sender, double range) {
		sending.add(sender.getAddress().asInt());
	}


	public void initWorld(World _world) {
		_world.addObserver(this);
	}
}
