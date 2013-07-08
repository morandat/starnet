package fr.labri.timedautomata;

import fr.labri.Utils;


public abstract class CompiledTimedAutomata<C> implements ITimedAutomata<C> {
	int _current;
	int _currentTimeout;

	final Action<C>[] _states;
	final Predicate<C>[] _predicates;
	
	int _initial;
	
	final int[][] _transitionsPredicates;
	final int[][] _transitionsTarget;
	final int[] _timeouts;
	final int[] _timeoutsTarget;
	
	public CompiledTimedAutomata(Action<C>[] states, Predicate<C>[] predicates, int initial, int[][] transitionsPredicates, int[] timeouts, int[][] transitionsTarget, int[] timeoutsTarget) {
		_states = states;
		_predicates = predicates;
		_transitionsPredicates = transitionsPredicates;
		_timeouts = timeouts;
		_transitionsTarget = transitionsTarget;
		_timeoutsTarget = timeoutsTarget;
		
		_initial = initial;
		_current = -1;
		
		int l = states.length ;
		if(l != transitionsPredicates.length || l != timeouts.length || l != transitionsTarget.length || l != timeoutsTarget.length)
			throw new RuntimeException("Automata is not well formed !");
		// TODO check everything is valid
	}
	
	public CompiledTimedAutomata(Action<C>[] states, Predicate<C>[] predicates, Action<C> initial, int[][] transitionsPredicates, int[] timeouts, int[][] transitionsTarget, int[] timeoutsTarget) {
		this(states, predicates, Utils.indexOf(initial, states), transitionsPredicates, timeouts, transitionsTarget, timeoutsTarget);
	}
	
	@Override
	final public void nextState() {
		int target = _current;
		C context = getContext();

		if(_currentTimeout > 0 && _currentTimeout -- == 0)
			target = _timeoutsTarget[_current];
		else {
			int[] trans = _transitionsPredicates[_current];
			int len = trans.length;
			for(int i = 0; i < len; i ++)
				if(_predicates[trans[i]].isValid(context)) {
					target = _transitionsTarget[_current][i];
					break;
				}
		}
		setState(target, context);
	}
	
	@Override
	final public Action<C> getInitialState() {
		return _states[_initial];
	}
	
	@Override
	final public void setInitialState(Action<C> initial) {
		_initial = Utils.indexOf(initial, _states);
	}


	@Override
	final public Action<C> getCurrentState() {
		return _states[_current];
	}
	
	@Override
	final public Action<C>[] getStates() {
		return _states;
	}
	
	@Override
	final public Predicate<C>[] getPredicates() {
		return _predicates;
	}
	
	@Override
	final public void reset() {
		_currentTimeout = _timeouts[_current];
	}

	@Override
	final public void start() {
		assert _current == -1;
		setState(_initial, getContext());
	}

	@Override
	final public void restart() {
		setState(_initial, getContext());
	}
	
	@Override
	final public void setState(Action<C> target) {
		setState(Utils.indexOf(target, _states), getContext());
	}
	
	final private void setState(int target, C context) {
		if(_current == target) {
			_states[target].eachAction(context, this);
		} else {
			_states[_current].postAction(context, this);
			_current = target;
			_currentTimeout = _timeouts[_current];
			_states[_current].preAction(context, this);
		}
	}
	
	@Override
	abstract public C getContext();
	
	public static class DelegatedTimedAutomata<C> extends CompiledTimedAutomata<C> {
		final ContextProvider<C> _context;
		public DelegatedTimedAutomata(ContextProvider<C> context,
				Action<C>[] states,
				Predicate<C>[] predicates,
				int initial,
				int[][] transitionsPredicates, int[] timeouts,
				int[][] transitionsTarget, int[] timeoutsTarget) {
			super(states, predicates, initial, transitionsPredicates, timeouts,
					transitionsTarget, timeoutsTarget);
			
			_context = context;
		}

		@Override
		public C getContext() {
			return _context == null ? null : _context.getContext();
		}

		public ContextProvider<C> getContextProvider() {
			return _context;
		}
	}
	
	public String toString() {
		return toDot("G");
	}
	
	@Override
	public String toDot(String name) {
		StringBuilder b = new StringBuilder("digraph ").append(name).append(" {\n");
		int slen = _states.length;
		
		for(int i = 0; i < slen; i++) {
			b.append("node").append(i).append(" [label=\"").append(_states[i]).append("\"");
			if(i == _initial)
				b.append(", shape=\"doubleoctagon\"");
			if(i == _current)
				b.append(",style=filled, color=\"red\"");
			b.append("];\n");
		}
		
		for(int i = 0; i < slen; i++) {
			int[] targets = _transitionsTarget[i];
			int[] preds = _transitionsPredicates[i];
			int tlen = preds == null ? 0 : preds.length;
			
			for(int j = 0; j < tlen; j ++)
				b.append("node").append(i).append(" -> node").append(targets[j]).append(" [label=\"").append(_predicates[preds[j]].getType()).append("\"];\n");
			if(_timeouts[i] > 0)
				b.append("node").append(i).append(" -> node").append(_timeoutsTarget[i]).append(" [style=dashed, label=\"").append(_timeouts[i]).append("\"];\n");
		}
		return b.append("};").toString();
	}
}
