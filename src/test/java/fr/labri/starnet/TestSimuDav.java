package fr.labri.starnet;

import java.io.IOException;

import org.jdom2.JDOMException;

import fr.labri.starnet.models.FailureModel;
import fr.labri.starnet.models.RandomSend;
import fr.labri.starnet.ui.SimpleUI;

public class TestSimuDav {

	public static void main(String[] args) throws JDOMException, IOException {
		SimulationFactory factory = new SimulationFactory().add(new FailureModel()).add(new RandomSend());
		factory.setPolicyAdapterFactory("fr.labri.starnet.policies.push.GossipPush");
		Simulation simu = factory.createSimulation();
		SimpleUI.createDefaultLayout(simu).setVisible(true);
		simu.start();
	}

}
