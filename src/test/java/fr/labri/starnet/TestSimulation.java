package fr.labri.starnet;

import java.io.IOException;

import org.jdom2.JDOMException;

import fr.labri.starnet.models.FailureModel;
import fr.labri.starnet.models.SpreadModel;
import fr.labri.starnet.ui.SimpleUI;

public class TestSimulation extends SimulationFactory {
	static boolean ok = true;
	static boolean above = true;
	
	@Override
	public SpreadModel getSpreadModel() {
		return new SpreadModel() {
			@Override
			public void spread(World world) {
				Position dim = world.getDimension();
				int x = dim.getX() / 2;
				int y = dim.getY() / 2;
				int a = 2;
				for(Node node : world.getParticipants()) {
					node.setPosition(new OrientedPosition(x, y, (above ? Math.PI/2 :0) + (ok ? (Math.PI - a * Math.PI / 2) : a * Math.PI / 2)));
					if(above)
						y += 50;
					else
						x += 50;
					a = (a + 2) % 4;
					System.out.println(node.getPosition());
				}
			}
		};
	}

	public static void main(String[] args) throws JDOMException, IOException {
		System.setProperty("starnet.parallel", "false");
		SimulationFactory factory = new TestSimulation();
		factory.setNodeCount(2).setWorldDimensions(1024, 768);
		factory.add(new FailureModel());
		factory.setPolicyAdapterFactory("fr.labri.starnet.policies.commons.Random") ;
		Simulation simu = factory.createSimulation();
		SimpleUI.createDefaultLayout(simu).setVisible(true);
		simu.start();
	}
}