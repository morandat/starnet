package fr.labri.timedautomata;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import fr.labri.timedautomata.ITimedAutomata.ContextProvider;
import fr.labri.timedautomata.ITimedAutomata.Cursor;

public class Executor<C> implements ITimedAutomata.Executor<C> {
	List<Cursor<C>> _cursors = new LinkedList<>();
	
	ContextProvider<C> _context;
	
	Executor(ContextProvider<C> context) {
		_context = context;
	}
	
	@Override
	public void start(ITimedAutomata<C> auto, String key) {
		_cursors.add(auto.start(_context, key));
	}

	@Override
	public void next() {
		for(Iterator<Cursor<C>> it = _cursors.iterator(); it.hasNext() ;) {
			Cursor<C> c = it.next();
			if(c.next(this))
				it.remove();
		}
	}

	@Override
	public Collection<Cursor<C>> getCursors() {
		return Collections.unmodifiableCollection(_cursors);
	}
}
