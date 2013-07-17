package fr.labri.starnet;

import java.util.ArrayList;

import fr.labri.Utils;
import fr.labri.starnet.models.FailureModel;
import fr.labri.starnet.models.RandomSend;
import fr.labri.starnet.models.StimuliModel;
import fr.labri.starnet.ui.SimpleUI;

public class Simulation implements Runnable {
	public static final boolean DEBUG = Boolean.parseBoolean(System.getProperty("starnet.debug", "true"));
	
	public interface Observer {
		void newtick(long time);
		void initWorld(World _world);
		void simulationStateChanged(State oldState, State newState);
	}
	
	final World _world;
	
	volatile State _state;
	volatile int _timeout = 10;
	Thread _simulationThread;
	
	final StimuliModel _externalStimuli;
	
	ArrayList<Observer> _observers = new ArrayList<Observer>();
	
	Simulation(World world, StimuliModel stimuli) {
		_world = world;
		_externalStimuli = stimuli;
		_state = State.READY;
	}
	
	public void addObserver(Observer obs) {
		_observers.add(obs);
	}
	
	public void removeObserver(Observer obs) {
		_observers.remove(obs);
	}
	
	
	public World getWorld() {
		return _world;
	}
	
	public static void main(String[] args) {
		SimulationFactory factory = new SimulationFactory().add(new FailureModel()).add(new RandomSend());
		Simulation simu = factory.createSimulation();
		SimpleUI.createDefaultLayout(simu).setVisible(true);
		simu.start();
	}

	public void run() {
		if(DEBUG)
			Utils.debug(this, "Simulation Started");

		for(Observer o: _observers)
			o.initWorld(_world);

		setState(DEBUG ? State.PAUSED : State.RUNNING);
		while(_state != State.ENDING) {
			if(DEBUG)
				Utils.debug(this, "Next tick: ", _world.getTime());
			
			_world.doTick();
			long time = _world.getTime();
			for(Observer o: _observers)
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
	
	private void setState(State nstate){
		if(_state != nstate) {
			State ostate = _state;
			_state = nstate;
			for(Observer o: _observers)
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
