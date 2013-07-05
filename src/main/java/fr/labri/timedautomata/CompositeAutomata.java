package fr.labri.timedautomata;

import java.util.ArrayList;
import java.util.Collection;

public class CompositeAutomata<C> implements ITimedAutomata<C> {
	final ITimedAutomata<C> _main;
	Collection<ITimedAutomata<C>> automatas = new ArrayList<>();
	final Action<C> _initial = new TimedAutomata.StateAdapter<C>() {
		public String getName() {return "CompositeStart"; };
	};
	
	CompositeAutomata(ITimedAutomata<C> main) {
		_main = main;
		add(main);
	}
	
	CompositeAutomata<C> add(ITimedAutomata<C> auto) {
		automatas.add(auto);
		return this;
	}
	
	boolean remove(ITimedAutomata<C> auto) {
		if(auto == _main)
			return false;
		return automatas.remove(auto);
	}
	
	@Override
	public void nextState() {
		for(ITimedAutomata<C> a: automatas)
			a.nextState();
	}

	@Override
	public Action<C> getInitialState() {
		return _initial;
	}

	@Override
	public void setInitialState(Action<C> initial) {
		throw new RuntimeException("Cannot change initial state of composite automata");
	}

	@Override
	public Action<C> getCurrentState() {
		return _initial; // FIXME create a fake handler ... maybe
	}

	@SuppressWarnings("unchecked")
	@Override
	public Action<C>[] getStates() {
		Collection<Action<C>> states = new ArrayList<>();
		for(ITimedAutomata<C> a: automatas)
			for(Action<C> s: a.getStates())
				states.add(s);
		Action<C>[] s = (Action<C>[])new Action<?>[states.size()];
		states.toArray(s);
		return s;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Predicate<C>[] getPredicates() {
		Collection<Predicate<C>> preds = new ArrayList<>();
		for(ITimedAutomata<C> a: automatas)
			for(Predicate<C> p: a.getPredicates())
				preds.add(p);
		Predicate<C>[] p = (Predicate<C>[])new Predicate<?>[preds.size()];
		preds.toArray(p);
		return p;
	}

	@Override
	public void reset() {
	}

	@Override
	public void start() {
		for(ITimedAutomata<C> a: automatas)
			a.start();
	}

	@Override
	public void restart() {
		for(ITimedAutomata<C> a: automatas)
			a.restart();
	}

	@Override
	public void setState(Action<C> target) {
		throw new RuntimeException("Cannot change state of composite automata");
	}

	@Override
	public C getContext() {
		return _main.getContext();
	}

	@Override
	public String toDot(String name) { // TODO FIXME TESTME another day
		StringBuilder b = new StringBuilder("digraph ").append(name).append(" {\n");
		
		b.append("initial [label=\"").append(_initial.getName()).append("\"]; ");
		int id = 0;
		for(ITimedAutomata<C> a: automatas) {
			String sname = "cluster_"+ id++;
			String ssname = sname+"_s$1";
			String dot = a.toDot(sname).replace("digraph", "subgraph").replaceAll("s(\\d)", ssname);
			b.append(dot);
			b.append("initial -> ").append(sname+"_s0").append(";");
		}
		
		return b.append("};").toString();
	}

}
