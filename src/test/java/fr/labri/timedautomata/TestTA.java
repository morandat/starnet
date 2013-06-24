package fr.labri.timedautomata;

import java.awt.Dimension;
import java.io.IOException;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.JFrame;

import org.jdom2.JDOMException;

import fr.labri.timedautomata.TimedAutomata;
import fr.labri.timedautomata.ITimedAutomata.NodeFactory;
import fr.labri.timedautomata.ITimedAutomata.Action;
import fr.labri.timedautomata.ITimedAutomata.Predicate;
import fr.labri.timedautomata.TimedAutomata.NamedAction;
import fr.labri.timedautomata.TimedAutomata.TransitionAdapter;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;

public class TestTA {
	public static void main(String[] args) throws JDOMException, IOException {
		
		TimedAutomata<Object> b = TimedAutomata.getTimedAutoma(null, getSimpleNodeBuilder());
		b.loadXML(TestTA.class.getResourceAsStream("ex1.xml"), false);
		//DotViewer.view(b.toDot("ex1"));
		System.out.println(b.toString());
		//DotViewer.view(b.compile().toDot("ex1"));
		
		b = TimedAutomata.getTimedAutoma(null, getSimpleNodeBuilder());
		b.loadXML(TestTA.class.getResourceAsStream("ex2.xml"), false);
		//DotViewer.view(b.toDot("ex2"));
		System.out.println(b.toString());
		//DotViewer.view(b.compile().toDot("ex2"));
		
		// The Layout<V, E> is parameterized by the vertex and edge types
		DirectedGraph<Action<Object>, Predicate<Object>> g = automataToGraph(b);
		Layout<Action<Object>, Predicate<Object>> layout = new FRLayout<>(g);
		layout.setSize(new Dimension(300,300)); // sets the initial size of the space
		// The BasicVisualizationServer<V,E> is parameterized by the edge types
		BasicVisualizationServer<Action<Object>, Predicate<Object>> vv = 
				new BasicVisualizationServer<Action<Object>, Predicate<Object>>(layout);
		vv.setPreferredSize(new Dimension(350,350)); //Sets the viewing area size
		vv.getRenderContext().getPickedVertexState().pick(b._initial, true);
		for(Predicate<Object> e: g.getEdges())
			if(e instanceof DefaultTransition)
				vv.getRenderContext().getPickedEdgeState().pick(e, true);
		JFrame frame = new JFrame("Simple Graph View");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(vv); 
		frame.pack();
		frame.setVisible(true); 
	}
	
	static DirectedGraph<Action<Object>, Predicate<Object>> automataToGraph(TimedAutomata<Object> automata) {
		DirectedGraph<Action<Object>, Predicate<Object>> sgv = new DirectedSparseGraph<Action<Object>, Predicate<Object>>();
		for(Action<Object> state: automata._stateMap.values())
			sgv.addVertex(state);
		for(Entry<Action<Object>, List<TimedAutomata<Object>.Transition>> edge: automata._transitions.entrySet()) {
			Action<Object> src = edge.getKey();
			for(TimedAutomata<Object>.Transition dst: edge.getValue())
				if(dst.predicate != null)
					sgv.addEdge(dst.predicate, src, dst.state);
				else
					sgv.addEdge(new DefaultTransition(), src, dst.state);

		}
		return sgv;
	}
	
	static class DefaultTransition extends TransitionAdapter<Object> {
		
	}
	
	static <C> NodeFactory<C> getSimpleNodeBuilder() {
		return new NodeFactory<C>() {
			public Action<C> newState(final String name, final String type) {
				return new NamedAction<C>(name, null) {
					public String getType() {
						return name;
					}
				};
			}
			
			public Predicate<C> newPredicate(final String name) {
				return new TransitionAdapter<C>() {
					public String getType() {
						return name;
					}
				};
			}
		};
	}
}
