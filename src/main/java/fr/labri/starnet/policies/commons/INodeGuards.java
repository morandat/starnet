package fr.labri.starnet.policies.commons;

import fr.labri.starnet.INode;
import fr.labri.tima.ITimedAutomata.PredicateAdapter;

public class INodeGuards {
	public static class Random extends PredicateAdapter<INode> {
		@Override
		public boolean isValid(INode context, String key) {
			return context.getRandom().nextBoolean();
		}
	}
}
