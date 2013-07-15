package fr.labri.starnet;

import fr.labri.starnet.ui.SimpleUI;

public class TestSimulation extends SimulationFactory {
	@Override
	SpreadModel getSpreadModel() {
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

	public static void main(String[] args) {
		System.setProperty("starnet.parallel", "false");
		SimulationFactory factory = new TestSimulation();
		Simulation simu = factory.createSimulation(1024, 768, 2);
		SimpleUI.createDefaultLayout(simu).setVisible(true);
		simu.start();
	}
}