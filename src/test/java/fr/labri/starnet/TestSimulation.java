package fr.labri.starnet;

import java.io.IOException;

import org.jdom2.JDOMException;

import fr.labri.starnet.Simulation.Factory;
import fr.labri.starnet.models.SpreadModel;
import fr.labri.starnet.ui.SimpleUI;

public class TestSimulation extends SimulationFactory {
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
					node.setPosition(new OrientedPosition(x, y, a * Math.PI / 2));
					x += 50;
					a = (a + 2) % 4;
					System.out.println(node.getPosition());
				}
			}
		};
	}

	public static void main(String[] args) throws JDOMException, IOException {
		System.setProperty("starnet.parallel", "false");
		Factory factory = new TestSimulation().setNodeCount(2).setWorldDimensions(1024, 768).setPolicyAdapterFactory("fr.labri.starnet.policies.commons.Random") ;
		Simulation simu = factory.createSimulation();
		SimpleUI.createDefaultLayout(simu).setVisible(true);
		simu.start();
	}
}