package fr.labri.timedautomata;

public interface ITimedAutomata<C> {
	public abstract void nextState();
	
	public abstract Action<C> getInitialState();
	public abstract void setInitialState(Action<C> initial);
	public abstract Action<C> getCurrentState();
	public abstract Action<C>[] getStates();
	public abstract Predicate<C>[] getPredicates();
	public abstract void reset();
	public abstract void start();
	public abstract void restart();
	public abstract void setState(Action<C> target);
	public abstract C getContext();
	public abstract String toDot(String name);

	public interface NodeFactory<C> {
		Action<C> newState(final String name, final String type, String attr);
		Predicate<C> newPredicate(final String type);
	}

	public interface Predicate<C> {
		boolean isValid(C context);
		String getType();
	}
	
	public interface Action<C> {
		void preAction(C context, ITimedAutomata<C> auto);
		void eachAction(C context, ITimedAutomata<C> auto);
		void postAction(C context, ITimedAutomata<C> auto);
		
		String getName();
		String getType();
	}
	
	public interface ContextProvider<C> {
		C getContext();
	}
}

