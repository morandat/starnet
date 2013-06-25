package fr.labri.starnet.policies;

import java.io.IOException;

import org.jdom2.JDOMException;

import fr.labri.timedautomata.AutomataViewer;
import fr.labri.timedautomata.TimedAutomata;

public class LMST {
	public static void main(String[] args) throws JDOMException, IOException {
		TimedAutomata<MyBot> b = TimedAutomata.getTimedAutoma(null, TimedAutomata.getReflectNodeBuilder("fr.labri.starnet.policies", MyBot.class));

		b.loadXML(LMST.class.getResourceAsStream("LMST.xml"), false);
		System.out.println(b.toString());
		AutomataViewer.viewAsFrame(b);
	}
}
