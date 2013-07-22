package fr.labri.starnet;

import java.io.IOException;
import java.util.ArrayList;

import org.jdom2.JDOMException;

import fr.labri.AutoQualifiedClassLoader;
import fr.labri.starnet.INode.Descriptor;
import fr.labri.starnet.Node.PolicyAdapter;
import fr.labri.starnet.Node.PolicyAdapterFactory;
import fr.labri.starnet.Simulation.Factory;
import fr.labri.starnet.models.EnergyModel;
import fr.labri.starnet.models.EnergyModel.EnergyModelFactory;
import fr.labri.starnet.models.SpreadModel;
import fr.labri.starnet.models.SpreadModel.SpreadModelFactory;
import fr.labri.starnet.models.StimuliModel;
import fr.labri.starnet.policies.RandomPolicyAdapter;
import fr.labri.starnet.policies.SimpleRouting;
import fr.labri.starnet.policies.TimedAutomataPolicy;
import fr.labri.tima.TimedAutomataFactory;
import fr.labri.tima.ITimedAutomata.ContextProvider;

public class SimulationFactory extends Factory {
	private static final String DEFAULT_NAMESPACE = "fr.labri.starnet.policies.commons";

	public static final boolean PARALLEL = Boolean.parseBoolean(System.getProperty("starnet.parallel", "true"));

	public final double ENERGY_EXPONENT = Double.parseDouble(System.getProperty("starnet.energy.exoponent", "4"));
	public final double ENERGY_BASICCOST = Double.parseDouble(System.getProperty("starnet.energy.basiccost", "1e8"));
	
	public final double NODE_POWERLEVEL = Double.parseDouble(System.getProperty("starnet.node.powerlevel", "1e12"));
	public final double NODE_RANGEMAX = Double.parseDouble(System.getProperty("starnet.node.basiccost", "250"));
	public final double NODE_EMISSION_WINDOW = Double.parseDouble(System.getProperty("starnet.node.window", "360"));//Double.toString(Math.PI / 3)));


	Position _worldDimensions = new Position(1024, 800);
	int _nbNodes = 100;
	
	final ArrayList<StimuliModel> _externalStimuli = new ArrayList<>();
	PolicyAdapterFactory _policyAdapterFactory = new PolicyAdapterFactory() {
		@Override
		public PolicyAdapter getPolicyAdapter(final INode node) {
			return new RandomPolicyAdapter(new RoutingPolicy[]{
					new SimpleRouting()
				});
		}
	};

	@Override
	public StimuliModel getStimuliModel() {
		if(_externalStimuli.size() == 1)
			return _externalStimuli.get(0);
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
	
	public Simulation createSimulation(int w, int h, int nbNodes) {
		_worldDimensions = new Position(w, h);
		setNodeCount(nbNodes);
		
		return createSimulation();
	}
	
	@Override
	public World createWorld(Position worldDimensions) {
		return PARALLEL
				? World.newParallelWorld(_worldDimensions.getX(), _worldDimensions.getY())
				: World.newSimpleWorld(_worldDimensions.getX(), _worldDimensions.getY());
	}
	
	@Override
	public int getNodeCount() {
		return _nbNodes;
	}
	
	public SimulationFactory setNodeCount(int nbNodes) {
		_nbNodes = nbNodes;
		return this;
	}

	@Override
	public Factory createNodes(World world, int nbNodes) {
		for(int i = 0; i< nbNodes; i ++)
			new Node(world, getPolicyAdapterFactory(), getDesciptor(i));
		return this;
	}
	
	@Override
	public SpreadModel getSpreadModel() {
		return SpreadModelFactory.getRandomModel();
	}

	@Override
	public EnergyModel getEnergyModel(int nodeId){
		return EnergyModelFactory.getPowerEnergyModel(ENERGY_EXPONENT, ENERGY_BASICCOST);
	}
	
	@Override
	public PolicyAdapterFactory getPolicyAdapterFactory() {
		return _policyAdapterFactory;
	}
	
	public SimulationFactory setPolicyAdapterFactory(PolicyAdapterFactory policyAdapterFactory) {
		_policyAdapterFactory = policyAdapterFactory;
		return this;
	}
	
	public SimulationFactory setPolicyAdapterFromXML(String path) throws JDOMException, IOException {
		String fname = "/" + path.replaceAll("\\.", "/") + ".xml";
		String namespace = path.substring(0, path.lastIndexOf("."));
		return setPolicyAdapterFromXML(fname, namespace);
	}	
	
	public SimulationFactory setPolicyAdapterFromXML(String path, String namespace) throws JDOMException, IOException {
		ClassLoader cl = new AutoQualifiedClassLoader(DEFAULT_NAMESPACE);
		if(!DEFAULT_NAMESPACE.equals(namespace))
			cl = new AutoQualifiedClassLoader(namespace, cl);
		
		final TimedAutomataFactory<INode> timaFactory = new TimedAutomataFactory<>(TimedAutomataFactory.getReflectNodeBuilder(cl, INode.class));
		timaFactory.loadXML(getClass().getResourceAsStream(path)).get(0).compile();

		_policyAdapterFactory = new PolicyAdapterFactory() {
				@Override
				public PolicyAdapter getPolicyAdapter(final INode node) {
					
					return new RandomPolicyAdapter(new RoutingPolicy[]{
							new TimedAutomataPolicy(timaFactory.getExecutor(new ContextProvider<INode>() {
								
								@Override
								public INode getContext() {
									return node;
								}
							}))
						});
				}
		};
		
		return this;
	}
	
	@Override
	public Descriptor getDesciptor(final int nodeId) {
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
	
	public SimulationFactory setWorldDimensions(Position worldDimensions) {
		_worldDimensions = worldDimensions;
		return this;
	}

	@Override
	public Position getWorldDimensions() {
		return _worldDimensions;
	}

	public SimulationFactory setWorldDimensions(int x, int y) {
		return setWorldDimensions(new Position(x, y));
	}	
}
