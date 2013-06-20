package fr.labri.starnet;

import fr.labri.starnet.Simulation.State;

public interface SimulationObserver {
	void newtick(long time);
	void initWorld(World _world);
	void simulationStateChanged(State oldState, State newState);
}
