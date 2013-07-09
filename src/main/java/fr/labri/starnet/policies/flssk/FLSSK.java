package fr.labri.starnet.policies.flssk;

import java.io.IOException;

import org.jdom2.JDOMException;

import fr.labri.starnet.INode;
import fr.labri.timedautomata.AutomataViewer;
import fr.labri.timedautomata.TimedAutomata;

public class FLSSK {
	public static void main(String[] args) throws JDOMException, IOException {
		
		TimedAutomata<INode> b = TimedAutomata.getTimedAutoma(null, TimedAutomata.getReflectNodeBuilder("fr.labri.starnet.policies.flssk", INode.class));

		b.loadXML(FLSSK.class.getResourceAsStream("FLSSk.xml"), false);
		System.out.println(b.toString());
		AutomataViewer.viewAsFrame(b);
		
//		INode n = null;
//		n.move(n.getPosition().rotate(Math.PI / 2));
	}
}
