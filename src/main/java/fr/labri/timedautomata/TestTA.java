package fr.labri.timedautomata;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.jdom2.JDOMException;

import fr.labri.DotViewer;
import fr.labri.timedautomata.TimedAutomataBuilder.NodeBuilder;
import fr.labri.timedautomata.TimedAutomata.Action;
import fr.labri.timedautomata.TimedAutomata.Predicate;
import fr.labri.timedautomata.TimedAutomataBuilder.NamedAction;
import fr.labri.timedautomata.TimedAutomataBuilder.TransitionAdapter;

public class TestTA {
	public static void main(String[] args) throws JDOMException, IOException {
		
		TimedAutomataBuilder<Object> b = TimedAutomataBuilder.getTimedAutomaBuilder(null, getSimpleNodeBuilder());
		
		b.loadXML(TestTA.class.getResourceAsStream("ex2.xml"), false);
		
		b._transitions.remove(b.getState("timeout", null));
		b.addDefaultTransition(b.getState("timeout", null), b.getState("timeout", null));
		
		DotViewer.view(b.toDot("G"));
		System.out.println(b.toString());
		DotViewer.view(b.build().toDot("G"));
		
//		Action<Void> s0 = getState("s0");
//		Action<Void> s1 = getState("s1");
//		Action<Void> s2 = getState("s2");
//		Action<Void> s3 = getState("s3");
//		Action<Void> t = getState("timeout");
//		b.addTransition(s0, 10, getTransition("t1"), s1);
//		b.addTransition(s0, 15, getTransition("t2"), s2);
//		b.addDefaultTransition(s0, t);
//		
//		b.addTransition(s2, 10, getTransition("t3"), s3);
//
//		b.addTransition(s3, 5, getTransition("t5"), s1);
//		b.addTransition(s1, 12, getTransition("t4"), s2);
//		
//		b.addDefaultTransition(s1, t);
//		b.addDefaultTransition(s2, t);
//		b.addDefaultTransition(s3, t);
//		
//		b.setInitial(s0);
//		System.out.println(b.toString());
//		System.out.println(DotViewer.view(b.toDot("Original")));
//		TimedAutomata<Void> ta;
//		ta = b.build();
//		System.out.println(DotViewer.view(ta.toDot("Compiled")));
	}
	
	static <C> NodeBuilder<C> getSimpleNodeBuilder() {
		return new NodeBuilder<C>() {
			Map<String, Action<C>> stateMap = new HashMap<String, Action<C>>();
			public Action<C> getState(final String name, final String type) {
				if(stateMap.containsKey(name))
					return stateMap.get(name);
				Action<C> s = new NamedAction<C>(name, null) {
					public String getType() {
						return name;
					}
				};
				stateMap.put(name, s);
				return s;
			}
			
			Map<String, Predicate<C>> transMap = new HashMap<String, Predicate<C>>();
			public Predicate<C> getPredicate(final String name) {
				if(stateMap.containsKey(name))
					return transMap.get(name);
				Predicate<C> t = new TransitionAdapter<C>() {
					public String getType() {
						return name;
					}
				};
				transMap.put(name, t);
				return t;
			}
		};
	}
}
