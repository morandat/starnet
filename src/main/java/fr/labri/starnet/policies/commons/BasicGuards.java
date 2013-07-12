package fr.labri.starnet.policies.commons;

import fr.labri.timedautomata.ITimedAutomata.PredicateAdapter;

public class BasicGuards {
	public static class True<C> extends PredicateAdapter<C> {
		public True() {}
		@Override
		public boolean isValid(C context) {
			return true;
		}
	}
	public static class False<C> extends PredicateAdapter<C> {
		public False() {}
		@Override
		public boolean isValid(C context) {
			return false;
		}
	}
	public static class Random<C> extends PredicateAdapter<C> {
		public Random() {}

		java.util.Random rnd;
		@Override
		public boolean isValid(C context) {
			return rnd.nextBoolean();
		}
	}
}
