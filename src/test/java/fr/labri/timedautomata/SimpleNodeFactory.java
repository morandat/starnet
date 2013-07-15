package fr.labri.timedautomata;

import fr.labri.timedautomata.ITimedAutomata.Action;
import fr.labri.timedautomata.ITimedAutomata.ActionAdapter;
import fr.labri.timedautomata.ITimedAutomata.NodeFactory;
import fr.labri.timedautomata.ITimedAutomata.Predicate;
import fr.labri.timedautomata.ITimedAutomata.PredicateAdapter;
import fr.labri.timedautomata.ITimedAutomata.SpawnAdapter;
import fr.labri.timedautomata.ITimedAutomata.Spawner;

class SimpleNodeFactory<C> implements NodeFactory<C> {

	public Predicate<C> newPredicate(final String name) {
		return new PredicateAdapter<C>() {
			public String getType() {
				return name;
			}
		};
	}

	@Override
	public Action<C> newAction(final String type, final String attr) {
		return new ActionAdapter<C>() {
			public String getType() {
				return type+":"+attr;
			}
		};
	}

	@Override
	public Spawner<C> newSpawner(final String type) {
		return new SpawnAdapter<C>() {
			@Override
			public String getType() {
				return type;
			}
		};
	}
}