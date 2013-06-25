package fr.labri.starnet.policies;

import fr.labri.timedautomata.TimedAutomata.TransitionAdapter;

public class BasicGuards {
	public static class True<C> extends TransitionAdapter<C> {
		public True() {}
		@Override
		public boolean isValid(C context) {
			return true;
		}
	}
	public static class False<C> extends TransitionAdapter<C> {
		public False() {}
		@Override
		public boolean isValid(C context) {
			return false;
		}
	}
	public static class Random<C> extends TransitionAdapter<C> {
		public Random() {}

		java.util.Random rnd;
		@Override
		public boolean isValid(C context) {
			return rnd.nextBoolean();
		}
	}
}
