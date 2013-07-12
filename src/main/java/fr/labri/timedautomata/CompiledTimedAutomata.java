package fr.labri.timedautomata;

import fr.labri.Utils;

public class CompiledTimedAutomata<C> implements ITimedAutomata<C> {
	final State<C>[] _states;
	final Predicate<C>[] _predicates;
	
	int _initial;
	
	final int[][] _transitionsPredicates;
	final int[][] _transitionsTarget;
	final int[] _timeouts;
	final int[] _timeoutsTarget;
	
	public CompiledTimedAutomata(State<C>[] states, Predicate<C>[] predicates, int initial, int[][] transitionsPredicates, int[] timeouts, int[][] transitionsTarget, int[] timeoutsTarget) {
		_states = states;
		_predicates = predicates;
		_transitionsPredicates = transitionsPredicates;
		_timeouts = timeouts;
		_transitionsTarget = transitionsTarget;
		_timeoutsTarget = timeoutsTarget;
		
		_initial = initial;
		
		int l = states.length ;
		if(l != transitionsPredicates.length || l != timeouts.length || l != transitionsTarget.length || l != timeoutsTarget.length)
			throw new RuntimeException("Automata is not well formed !");
	}
	
	public CompiledTimedAutomata(State<C>[] states, Predicate<C>[] predicates, Action<C> initial, int[][] transitionsPredicates, int[] timeouts, int[][] transitionsTarget, int[] timeoutsTarget) {
		this(states, predicates, Utils.indexOf(initial, states), transitionsPredicates, timeouts, transitionsTarget, timeoutsTarget);
	}
	
	@Override
	public Cursor<C> start(final ContextProvider<C> context, final String key) {
		return new Cursor<C>() {
			int _current;
			int _currentTimeout;

			@Override
			final public boolean next(Executor<C> executor) {
				boolean urgent = false, terminal = false; 
				do {
					int current = _current;
					int target = current;
					C ctx = context.getContext();

					if(_currentTimeout > 0 && _currentTimeout -- == 0)
						target = _timeoutsTarget[_current];
					else {
						int[] trans = _transitionsPredicates[_current];
						int len = trans.length;
						for(int i = 0; i < len; i ++)
							if(_predicates[trans[i]].isValid(ctx)) {
								target = _transitionsTarget[_current][i];
								break;
							}
					}
					if(target == current) {
						_states[target].eachAction(ctx, executor, key);
					} else {
						State<C> state = setState(target, executor, ctx);
						urgent = (state.getModifier() & URGENT) > 0;
						terminal = (state.getModifier() & TERMINATE) > 0;
					}
				} while(urgent);
				return terminal;
			}

			final private State<C> setState(int target, Executor<C> executor, C context) {
				_states[_current].postAction(context, executor, key);
				_current = target;
				_currentTimeout = _timeouts[target];
				State<C> newState = _states[target];
				newState.preAction(context, executor, key);
				return newState;
			}

			@Override
			public ITimedAutomata<C> getAutomata() {
				return CompiledTimedAutomata.this;
			}

			@Override
			public String getKey() {
				return key;
			}
		};
	}
	
	@Override
	final public State<C> getInitialState() {
		return _states[_initial];
	}
	
	@Override
	final public void setInitialState(State<C> initial) {
		_initial = Utils.indexOf(initial, _states);
	}

	@Override
	final public State<C>[] getStates() {
		return _states;
	}
	
	@Override
	final public Predicate<C>[] getPredicates() {
		return _predicates;
	}
	
	public String toString() {
		return toDot("G");
	}
	
	@Override
	public String toDot(String name) {
		StringBuilder b = new StringBuilder("digraph ").append(name).append(" {\n");
		int slen = _states.length;
		
		for(int i = 0; i < slen; i++) {
			b.append("node").append(i).append(" [label=\"").append(_states[i].getName()).append("\"");
			if(i == _initial)
				b.append(", shape=\"doubleoctagon\"");
//			if(i == _current) // TODO delegate this to a cursor
//				b.append(",style=filled, color=\"red\"");
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
