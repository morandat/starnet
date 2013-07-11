package fr.labri.starnet.policies.pull;

import java.io.IOException;

import org.jdom2.JDOMException;

import fr.labri.starnet.INode;
import fr.labri.timedautomata.AutomataViewer;
import fr.labri.timedautomata.TimedAutomata;

public class GossipPull {
	public static void main(String[] args) throws JDOMException, IOException {
		
		TimedAutomata<INode> b = TimedAutomata.getTimedAutoma(null, TimedAutomata.getReflectNodeBuilder("fr.labri.starnet.policies.pull", INode.class));

		b.loadXML(GossipPull.class.getResourceAsStream("GossipPull.xml"), false);
		System.out.println(b.toString());
		AutomataViewer.viewAsFrame(b);
		
//		INode n = null;
//		n.move(n.getPosition().rotate(Math.PI / 2));
	}
}
