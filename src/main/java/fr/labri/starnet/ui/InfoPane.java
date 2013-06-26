package fr.labri.starnet.ui;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import fr.labri.starnet.Position;
import fr.labri.starnet.Simulation;
import fr.labri.starnet.Simulation.State;
import fr.labri.starnet.SimulationObserver;
import fr.labri.starnet.World;

public class InfoPane extends JPanel implements SimulationObserver {

	private static final long serialVersionUID = 1L;
	
	final JLabel nodeCount = new JLabel();
	final JLabel tickCount = new JLabel();
	final JLabel worldSize = new JLabel();
	final JLabel status = new JLabel();
	
	InfoPane(Simulation simulation) {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(nodeCount);
		add(worldSize);
		add(new JSeparator());
		add(status);
		add(tickCount);
	}
	
	void updateNodeCount(int nbNodes) {
		nodeCount.setText(String.format("Nodes: %d", nbNodes));
	}
	
	void updateTickCount(long time) {
		tickCount.setText(String.format("Time: %d", time));
	}
	
	void updateDimensions(Position dim) {
		worldSize.setText(String.format("Size: %d x %d", dim.getX(), dim.getY()));
	}

	void updateStatus(State state) {
		status.setText(String.format("Status: %s", state.toString()));
	}
	
	void updateWorld(World world) {
		updateNodeCount(world.getParticipants().size());
		updateTickCount(world.getTime());
		updateDimensions(world.getDimension());
	}

	public void newtick(long time) {
		updateTickCount(time);
	}

	public void simulationStateChanged(State oldstate, State newState) {
		updateStatus(newState);
	}

	public void initWorld(World _world) {
		updateWorld(_world);
	}
}
