package fr.labri.starnet.policies;

import java.io.IOException;

import org.jdom2.JDOMException;

import fr.labri.timedautomata.AutomataViewer;
import fr.labri.timedautomata.TestTA;
import fr.labri.timedautomata.TimedAutomata;
import fr.labri.timedautomata.ITimedAutomata.Action;
import fr.labri.timedautomata.ITimedAutomata.NodeFactory;
import fr.labri.timedautomata.ITimedAutomata.Predicate;
import fr.labri.timedautomata.TimedAutomata.NamedAction;
import fr.labri.timedautomata.TimedAutomata.TransitionAdapter;

public class LMST {
	public static void main(String[] args) throws JDOMException, IOException {
		TimedAutomata<MyBot> b = TimedAutomata.getTimedAutoma(null, getSimpleNodeBuilder());
		b.loadXML(LMST.class.getResourceAsStream("ex1.xml"), false);
		System.out.println(b.toString());
		AutomataViewer.viewAsFrame(b);
	}
	
	static NodeFactory<MyBot> getSimpleNodeBuilder() {
		return new NodeFactory<MyBot>() {
			public Action<MyBot> newState(final String name, final String type) {
				return new NamedAction<MyBot>(name, null) {
					public String getType() {
						return name;
					}
				};
			}
			
			public Predicate<MyBot> newPredicate(final String name) {
				return new TransitionAdapter<MyBot>() {
					public String getType() {
						return name;
					}
				};
			}
		};
	}
}
