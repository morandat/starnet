package fr.labri.timedautomata;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.jdom2.input.sax.XMLReaderXSDFactory;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import fr.labri.Utils;
import fr.labri.timedautomata.TimedAutomata.Action;
import fr.labri.timedautomata.TimedAutomata.ContextProvider;
import fr.labri.timedautomata.TimedAutomata.DelegatedTimedAutomata;
import fr.labri.timedautomata.TimedAutomata.Predicate;

public abstract class TimedAutomataBuilder<C> {
	public static final String ROOT_TAG = "timedautomata";

	public static final String STATE_TAG = "state";
	public static final String TRANSITION_TAG = "path";
	public static final String TIMEOUT_TAG = "timeout";
	
	public static final String STATE_ACTION_TAG = "action";
	public static final String STATE_NAME_TAG = "name";
	public static final String STATE_INITIAL_TAG = "initial";
	
	public static final String TRANSITION_TARGET_TAG = "to";
	public static final String TRANSITION_PREDICATE_TAG = "guard";
	public static final String TRANSITION_TIMEOUT_TAG = "timeout";
	
//	public static final String XMLNS="http://www.w3.org/namespace/";
	public static final String XMLNS_XSI="http://www.w3.org/2001/XMLSchema-instance";
	public static final String XSI_LOCATION="http://www.labri.fr/~fmoranda/xsd/ta.xsd";
	
	Action<C> _initial;
	Map<Action<C>, List<Transition>> _transitions = new HashMap<Action<C>, List<Transition>>();
	Set<Action<C>> _states = new HashSet<Action<C>>();
	
	public void setInitial(Action<C> state) {
		_initial = state;
	}

	public void addDefaultTransition(Action<C> from, Action<C> to) {
		addTransition(from, 0, null, to);
	}
	
	public void addTransition(Action<C> from, int timeout, Predicate<C> trans, Action<C> to) {
		_states.add(from);
		_states.add(to);
		List<Transition> t = _transitions.get(from);
		if(t == null) {
			t = new ArrayList<Transition>();
			_transitions.put(from, t);
		}
		t.add(new Transition(timeout, trans, to));
	}
	
	public TimedAutomata<C> build(Action<C> init) {
		_initial = init;
		return build();
	}

	public TimedAutomata<C> build() {
		if(_initial == null)
			throw new RuntimeException("Initial state not set");
		
		int nb = _states.size();
		Map<Action<C>, List<Next>> newNodes = new HashMap<Action<C>, List<Next>>(nb);
		
		for(Action<C> node: _transitions.keySet()) {
			List<Next> n = nextStates(node);
			nb += Math.max(n.size() - 2, 0);
			newNodes.put(node, n);
		}
		
		Map<Action<C>, Integer> nodeIndex = new HashMap<Action<C>, Integer>();
		Map<Predicate<C>, Integer> transIndex = new HashMap<Predicate<C>, Integer>();
		
		int[] timeouts = new int[nb];
		int[] timeoutTargets = new int[nb];
		int[][] transitionPredicates = new int[nb][];
		int[][] transitionTarget = new int[nb][];
		
		for(Entry<Action<C>, List<Next>> e: newNodes.entrySet()) {
			Action<C> state = e.getKey();
			List<Next> lst = e.getValue();
			int node = getIndex(state, nodeIndex);
			int size = lst.size() - 1;
			Next nn = null;
			System.out.println(lst);
			for(int i = 0; i < size; i ++) {
				nn = lst.get(i);
				int ln = nn.states.size();
				int[] pred = new int[ln];
				transitionPredicates[node] = pred;
				int[] target = new int[ln];
				transitionTarget[node] = target;

				for(int j = 0; j < ln ; j++) {
					int dest = getIndex(nn.states.get(j), nodeIndex);
					int trans = getIndex(nn.trans.get(j), transIndex);
					pred[j] = trans;
					target[j] = dest;
				}
				if(i < (size-1)) {
					state = new VirtualState<C>(e.getKey());
					timeouts[node] = nn.t;
					node = timeoutTargets[node] = getIndex(state, nodeIndex);
				}
			}
			Next n = lst.get(size);
			if(n.t != 0 || n.states.size() > 1 || n.trans.get(0) != null)
				throw new RuntimeException("Something went wrong, timeout is not last");
			timeoutTargets[node] = getIndex(n.states.get(0), nodeIndex);
			timeouts[node] = nn == null ? 0 : nn.t;
		}
		
		
		return newAutomata(mapToActions(nodeIndex), mapToPredicates(transIndex), nodeIndex.get(_initial), transitionPredicates, timeouts, transitionTarget, timeoutTargets);
	}
	
	final public Document parseXML(InputStream stream, boolean validate) throws JDOMException, IOException {
		// FIXME if validate == true, it does not work :)
		SAXBuilder sxb = new SAXBuilder(validate ? new XMLReaderXSDFactory(TimedAutomataBuilder.class.getResource("ta.xsd")) : null);

		Document document = sxb.build(stream);
		return document;
	}
	
	final public void loadXML(InputStream stream) throws JDOMException, IOException {
		loadXML(parseXML(stream, false));
	}
	
	final public void loadXML(InputStream stream, boolean validate) throws JDOMException, IOException {
		loadXML(parseXML(stream, validate));
	}
	
	final public void loadXML(Document root) throws JDOMException, IOException {
		Map<String, Action<C>> names = new HashMap<>();
	
		loadXMLStates(root, names);
		
		for(Element state: root.getRootElement().getChildren(STATE_TAG)){
			Action<C> src = names.get(state.getAttributeValue(STATE_NAME_TAG));
			for(Element trans: state.getChildren(TRANSITION_TAG)) {
				String pred = trans.getAttributeValue(TRANSITION_PREDICATE_TAG);
				Action<C> dest = names.get(trans.getAttributeValue(TRANSITION_TARGET_TAG));
					addTransition(src, Integer.parseInt(trans.getAttributeValue(TRANSITION_TIMEOUT_TAG)), getPredicate(pred), dest);
			}
			Element timeout = state.getChild(TIMEOUT_TAG);
			if(timeout != null) {
				Action<C> dest = names.get(timeout.getAttributeValue(TRANSITION_TARGET_TAG));
				addDefaultTransition(src, dest);
			} 
		}
	}
	
	private void loadXMLStates(Document root, Map<String, Action<C>> names) throws JDOMException {
		for(Element state: root.getRootElement().getChildren(STATE_TAG)){
			String name = state.getAttributeValue(STATE_NAME_TAG);
			if(names.containsKey(name))
				throw new JDOMException("Node name is not unique: "+ name);
			Action<C> st = getState(name, state.getAttributeValue(STATE_ACTION_TAG));
			names.put(name, st);
			if("true".equalsIgnoreCase(state.getAttributeValue(STATE_INITIAL_TAG))) {
				if(_initial != null)
					throw new RuntimeException("More than one initial state: '"+_initial+"', '"+st+"'");
				_initial = st;
			}
		}
	}
	
	abstract protected Action<C> getState(String name, String type);
	abstract protected Predicate<C> getPredicate(String type);
	abstract protected TimedAutomata<C> newAutomata(Action<C>[] states, Predicate<C>[] predicates, int initial, int[][] transitionsPredicates, int[] timeouts, int[][] transitionsTarget, int[] timeoutsTarget);
	
	interface NodeBuilder<C> {
		Action<C> getState(final String name, final String type);
		Predicate<C> getPredicate(final String type);
	}
	
	public static <C> TimedAutomataBuilder<C> getTimedAutomaBuilder(final ContextProvider<C> context, final NodeBuilder<C> factory) {
		return new TimedAutomataBuilder<C>(){
			@Override
			protected TimedAutomata<C> newAutomata(Action<C>[] states,
					Predicate<C>[] predicates, int initial,
					int[][] transitionsPredicates, int[] timeouts,
					int[][] transitionsTarget, int[] timeoutsTarget) {

				return new DelegatedTimedAutomata<C>(context, states, predicates, initial, transitionsPredicates, timeouts, transitionsTarget, timeoutsTarget);
			}

			@Override
			protected Action<C> getState(String name, String type) {
				return factory.getState(name, type);
			}

			@Override
			protected Predicate<C> getPredicate(String type) {
				return factory.getPredicate(type);
			}
		};
	}
	
	@SuppressWarnings("unchecked")
	private Action<C>[] mapToActions(Map<Action<C>, Integer> map) {
		return mapTo(map, new Action[map.size()]);
	}
	
	@SuppressWarnings("unchecked")
	private Predicate<C>[] mapToPredicates(Map<Predicate<C>, Integer> map) {
		return mapTo(map, new Predicate[map.size()]);
	}
	
	private <T> T[] mapTo(Map<T, Integer> map, T[] array) {
		for(Entry<T, Integer> e: map.entrySet())
			array[e.getValue()] = e.getKey();
		return array;
	}
	
	private <T> int getIndex(T item, Map<T, Integer> index) {
		if(index.containsKey(item))
			return index.get(item);
		int idx = index.size();
		index.put(item, idx);
		return idx;
	}
	
	private List<Next> nextStates (Action<C> state) { // FIXME rewrite to support unlimited
		int m;
		List<Transition> nexts =  new ArrayList<Transition>(_transitions.get(state));
		List<Next> result = new ArrayList<Next>();
		Next c;
		int offset = 0;
		do {
			m = min(nexts, offset);
			if(m == Integer.MAX_VALUE) {
				result.add(c = new Next(0));
				if(nexts.isEmpty())
					throw new RuntimeException("Automata has no default transition for node: "+ state);
				if(nexts.size() > 1) // TODO deal with unlimited data (i.e. -1)
					throw new RuntimeException("Automata is not deterministic for node:"+ state+ ", nodes left: "+ nexts);
				c.add(nexts.get(0).state, null);
				break;
			} else {
				result.add(c = new Next(m - offset));
				offset = m;
				Iterator<Transition> it = nexts.iterator();
				while(it.hasNext()) {
					Transition t = it.next();
					if(t.predicate != null) {
						c.add(t.state, t.predicate);
						if(t.timeout == offset)
							it.remove();
					}
				}
			}
		}while(true);
		
		return result;
	}
	
	private int min(List<Transition> targets, int min) {
		int m = Integer.MAX_VALUE;
		for(Transition t: targets) {
			if(t.timeout > min)
				m = Math.min(m, t.timeout);
		}
		return m;
	}
	
	final private class Transition {
		final Action<C> state;
		final Predicate<C> predicate;
		final int timeout;
		
		Transition(int time, Predicate<C> t, Action<C> s) {
			timeout = time;
			state = s;
			predicate = t;
		}
		@Override
		public String toString() {
			return new StringBuilder("{").append(predicate).append("/").append(timeout).append("->").append(state).append("}").toString();
		}
	}
	
	class Next {
		final int t;
		ArrayList<Action<C>> states = new ArrayList<Action<C>>();
		ArrayList<Predicate<C>> trans = new ArrayList<Predicate<C>>();
		
		Next(int t) {
			this.t = t;
		}
		
		void add(Action<C> s, Predicate<C> t) {
			states.add(s);
			trans.add(t);
		}
		
		@Override
		public String toString() {
			return new StringBuilder("<").append(t).append("::").append(states).append(trans).append(">").toString();
		}
	}
	
	static private class VirtualState<C> extends NamedAction<C> {
		public VirtualState(Action<C> state) {
			super("Virtual"+state.getName(), state);
		}
	}
	
	static public class NamedAction<C> implements Action<C> {
		final String _name;
		final Action<C> _orig;

		public NamedAction(String name, Action<C> state) {
			_name = name;
			_orig = state;
		}
		@Override
		public void preAction(C context) {
			_orig.preAction(context);
		}
		@Override
		public void eachAction(C context) {
			_orig.eachAction(context);
		}
		@Override
		public void postAction(C context) {
			_orig.postAction(context);
		}
		@Override
		public String toString() {
			return _name;
		}

		@Override
		public String getType() {
			return _orig.getType();
		}
		@Override
		public String getName() {
			return _name;
		}
	}
	
	public static class StateAdapter<C> implements Action<C> {
		@Override
		public void preAction(C context) {
		}
		@Override
		public void eachAction(C context) {
		}
		@Override
		public void postAction(C context) {
		}
		@Override
		public String getName() {
			return null;
		}
		@Override
		public String getType() {
			return getClass().getName();
		}
	}
	
	public static class TransitionAdapter<C> implements Predicate<C> {
		public boolean isValid(C context) {
			return false;
		}

		@Override
		public String getType() {
			return getClass().getName();
		}
	}
	
	public Document toXML() {
		Element root = new Element(ROOT_TAG);
		int slen = _states.size();
		@SuppressWarnings("unchecked")
		Action<C>[] states = new Action[slen];
		_states.toArray(states);
		
		Namespace ns = Namespace.getNamespace("xsi", XMLNS_XSI);
		root.addNamespaceDeclaration(ns);
		root.setAttribute(new Attribute("noNamespaceSchemaLocation", XSI_LOCATION, ns));
		
		for(Entry<Action<C>, List<Transition>> e : _transitions.entrySet()) {
			Element state = xmlState(e.getKey(), states);
			root.addContent(state);
			
			for(Transition t: e.getValue()) {
				
				state.addContent(xmlTransition(t, states));
			}
		}
		
		for(Action<C> src: _states) {
			if(_transitions.containsKey(src)) continue;
			root.addContent(xmlState(src, states));

		}
		
		return new Document(root);
	}
	
	private Element xmlState(Action<C> src, Action<C>[] states) {
		Element state = new Element(STATE_TAG);

		state.setAttribute(new Attribute(STATE_NAME_TAG, getNodeName(src, states)));
		if(src == _initial)
			state.setAttribute(new Attribute(STATE_INITIAL_TAG, "true"));

		String type = src.getType();
		if(type != null)
			state.setAttribute(new Attribute(STATE_ACTION_TAG, type));
		return state;
	}
	
	private Element xmlTransition(Transition t, Action<C>[] states) {
		Element path = new Element(t.predicate == null ? TIMEOUT_TAG : TRANSITION_TAG );
		path.setAttribute(new Attribute(TRANSITION_TARGET_TAG, getNodeName(t.state, states)));
		if(t.predicate != null) {
			if(t.timeout > 0)
				path.setAttribute(new Attribute(TRANSITION_TIMEOUT_TAG, Integer.toString(t.timeout)));
			path.setAttribute(new Attribute(TRANSITION_PREDICATE_TAG, t.predicate.getType()));
		}
		return path;
	}
	
	private String getNodeName(Action<C> state, Action<C>[] states) {
		String name = state.getName();
		return (name == null) ? "node" + Integer.toString(Utils.indexOf(state, states)) : name;
	}
	
	public final <S extends OutputStream> S xmlToStream(S stream) throws IOException {
		new XMLOutputter(Format.getPrettyFormat()).output(toXML(), stream);
		return stream;
	}

	public final String toString() {
		try {
			return xmlToStream(new ByteArrayOutputStream()).toString();
		} catch (IOException e) {
			return new StringBuilder("<error>").append(e.getMessage()).append("</error>").toString();
		}
	}

	public String toDot(String name) {
		StringBuilder b = new StringBuilder("digraph ").append(name).append(" {\n");
		int slen = _states.size();
		@SuppressWarnings("unchecked")
		Action<C>[] states = new Action[slen];
		_states.toArray(states);
		
		for(int i = 0; i < slen; i++) {
			b.append(getNodeName(states[i], states)).append(" [label=\"").append(states[i].getType()).append("\"");
			if(states[i] == _initial)
				b.append(", shape=\"doubleoctagon\"");
			b.append("];\n");
		}
		
		for(Entry<Action<C>, List<Transition>> e: _transitions.entrySet()) {
			Action<C> src = e.getKey();
			for(Transition t: e.getValue())
				if(t.predicate == null)
					b.append(getNodeName(src, states)). append(" -> ").append(getNodeName(t.state, states)). append(" [style=dashed];\n");
				else
					b.append(getNodeName(src, states)).append(" -> ").append(getNodeName(t.state, states)).append(" [label=\"").append(t.predicate.getType()).append("[< ").append(t.timeout).append("]\"];\n");
		}
		return b.append("};").toString();
	}
}
