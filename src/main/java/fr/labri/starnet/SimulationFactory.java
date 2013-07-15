package fr.labri.starnet;

import java.util.ArrayList;

import fr.labri.starnet.INode.Descriptor;
import fr.labri.starnet.INode.EnergyModel;
import fr.labri.starnet.SpreadModel.SpreadModelFactory;

public class SimulationFactory {
	public static final boolean PARALLEL = Boolean.parseBoolean(System.getProperty("starnet.parallel", "true"));

	public final double ENERGY_EXPONENT = Double.parseDouble(System.getProperty("starnet.energy.exoponent", "4"));
	public final double ENERGY_BASICCOST = Double.parseDouble(System.getProperty("starnet.energy.basiccost", "1e8"));

	
	public final double NODE_POWERLEVEL = Double.parseDouble(System.getProperty("starnet.node.powerlevel", "1e12"));
	public final double NODE_RANGEMAX = Double.parseDouble(System.getProperty("starnet.node.basiccost", "250"));
	public final double NODE_EMISSION_WINDOW = Double.parseDouble(System.getProperty("starnet.node.window", Double.toString(Math.PI / 3)));

	Position _worldDimensions = new Position(1024, 800);
	int _nbNodes = 100;
	
	final ArrayList<StimuliModel> _externalStimuli = new ArrayList<>();

	StimuliModel getStimuliModel() {
		return new StimuliModel() {
			@Override
			public void nextRound(World world, long time) {
				for(StimuliModel model: _externalStimuli)
					model.nextRound(world, time);
			}
			
			@Override
			public void initWorld(World world) {
				for(StimuliModel model: _externalStimuli)
					model.initWorld(world);
			}
		};		
	}
	
	SimulationFactory add(StimuliModel model) {
		_externalStimuli.add(model);
		return this;
	}
	
	Simulation createSimulation(int w, int h, int nbNodes) {
		_worldDimensions = new Position(w, h);
		_nbNodes = nbNodes;
		
		return createSimulation();
	}
	
	Simulation createSimulation() {
		World world = PARALLEL
				? World.newParallelWorld(_worldDimensions.getX(), _worldDimensions.getY())
				: World.newSimpleWorld(_worldDimensions.getX(), _worldDimensions.getY());
				
		createNodes(world, getNodeCount());
		getSpreadModel().spread(world);
		StimuliModel externalStimuli = getStimuliModel();
		externalStimuli.initWorld(world);
		
		return new Simulation(world, externalStimuli);
	}
	
	private int getNodeCount() {
		return _nbNodes;
	}

	SimulationFactory createNodes(World world, int nbNodes) {
		for(int i = 0; i< nbNodes; i ++)
			new Node(world, getDesciptor(i));
		return this;
	}
	
	SpreadModel getSpreadModel() {
		return SpreadModelFactory.getRandomModel();
	}

	EnergyModel getEnergyModel(int nodeId){
		return Models.getPowerEnergyModel(ENERGY_EXPONENT, ENERGY_BASICCOST);
	}
	
	Descriptor getDesciptor(final int nodeId) {
		return new Descriptor() {
			public double getMaxPower() {
				return NODE_POWERLEVEL;
			}
			
			public double getEmissionWindow() {
				return NODE_EMISSION_WINDOW;
			}
			
			public double getEmissionRange() {
				return NODE_RANGEMAX;
			}

			@Override
			public EnergyModel getEneryModel() {
				return getEnergyModel(nodeId);
			}
		};
	}
}
