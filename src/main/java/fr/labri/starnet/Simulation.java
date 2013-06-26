package fr.labri.starnet;

import java.util.ArrayList;

import fr.labri.Utils;
import fr.labri.starnet.INode.Descriptor;
import fr.labri.starnet.SpreadModel.SpreadModelFactory;
import fr.labri.starnet.ui.SimpleUI;

public class Simulation implements Runnable {
	public static final boolean DEBUG = Boolean.parseBoolean(System.getProperty("starnet.debug", "true"));
	public static final boolean PARALLEL = Boolean.parseBoolean(System.getProperty("starnet.parallel", "true"));
	final World _world;
	
	volatile State _state;
	volatile int _timeout = 10;
	Thread _simulationThread;
	
	final StimuliModel _externalStimuli;
	
	ArrayList<SimulationObserver> _observers = new ArrayList<SimulationObserver>();
	
	Simulation(World world) {
		_world = world;
		_state = State.READY;
		_externalStimuli = new FailureModel();
	}
	
	static Simulation createSimulation(int w, int h, int nbNodes) {
		Simulation simu = new Simulation(PARALLEL ? World.newParallelWorld(w, h) : World.newSimpleWorld(w, h));
		simu.createNodes(nbNodes);
		simu.init();
		return simu;
	}
	
	public void addObserver(SimulationObserver obs) {
		_observers.add(obs);
	}
	
	public void removeObserver(SimulationObserver obs) {
		_observers.remove(obs);
	}
	
	
	public World getWorld() {
		return _world;
	}
	
	SpreadModel getSpreadModel() {
		return SpreadModelFactory.getRandomModel();
	}
	
	void createNodes(int nbNodes) {
		Descriptor desc = getDesciptor();
		for(int i = 0; i< nbNodes; i ++)
			new Node(_world, desc);
	}
	
	Descriptor getDesciptor() {
		return new Descriptor() {
			public int getMaxPower() {
				return 1000;
			}
			
			public double getEmissionWindow() {
				return Math.PI / 3;
			}
			
			public int getEmissionRange() {
				return 300;
			}
		};
	}
	
	public void run() {
		if(DEBUG)
			Utils.debug(this, "Simulation Started");

		for(SimulationObserver o: _observers)
			o.initWorld(_world);

		setState(DEBUG ? State.PAUSED : State.RUNNING);
		while(_state != State.ENDING) {
			if(DEBUG)
				Utils.debug(this, "Next tick: ", _world.getTime());
			
			_world.doTick();
			long time = _world.getTime();
			for(SimulationObserver o: _observers)
				o.newtick(time);

			waitNextTick();
			
			_externalStimuli.nextRound(_world, time);
		}
		setState(State.ENDED);
		if(DEBUG)
			Utils.debug(this, "Simulation Finished");
	}
	
	void waitNextTick() {
		try {
			do {
				Thread.sleep(_timeout);
			} while(_state == State.PAUSED);
		} catch (InterruptedException e) {}
	}
	
	void init() {
		getSpreadModel().spread(_world);
		_externalStimuli.initWorld(_world);
	}
	
	private void setState(State nstate){
		if(_state != nstate) {
			State ostate = _state;
			_state = nstate;
			for(SimulationObserver o: _observers)
				o.simulationStateChanged(ostate, nstate);
			_simulationThread.interrupt();
		}
	}
	
	public void stop() {
		setState(State.ENDING);
	}
	
	public void start() {
		if(_state != State.READY)
			throw new RuntimeException("Simulation already started");
		(_simulationThread = new Thread(this)).start();
	}
	
	public static void main(String[] args) {
		Simulation simu = createSimulation(1024, 800, 100);
		SimpleUI.createDefaultLayout(simu).setVisible(true);
		simu.start();
	}

	public boolean isRunning() {
		return _state == State.RUNNING;
	}
	
	public void setRunning(boolean newState) {
		State nstate = newState ? State.RUNNING : State.PAUSED;
		if(_state != nstate) {
			setState(nstate);
		}
	}
	
	public void setTimeout(int value) {
		_timeout = value;
	}

	public void interrupt() {
		_simulationThread.interrupt();
	}
	
	public static enum State {READY, RUNNING, PAUSED, ENDING, ENDED};
}
