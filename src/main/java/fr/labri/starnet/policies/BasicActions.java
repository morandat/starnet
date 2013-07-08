package fr.labri.starnet.policies;

import java.io.IOException;

import org.jdom2.JDOMException;

import fr.labri.timedautomata.CompositeAutomata;
import fr.labri.timedautomata.ITimedAutomata;
import fr.labri.timedautomata.TimedAutomata;
import fr.labri.timedautomata.TimedAutomata.StateAdapter;

public class BasicActions {
	static class Spawn<C> extends StateAdapter<C> {
		ITimedAutomata<C> _auto;
		final String _name;
		
		Spawn(String name) {
			_name = name;
		}
		
		@Override
		public void preAction(C context, ITimedAutomata<C> auto) {
			try {
				if(_auto == null) { // FIXME hack the node factory to have a descend terminate action
					CompositeAutomata<C> ca = ((CompositeAutomata<C>)auto);
					TimedAutomata<C> a = TimedAutomata.getTimedAutoma(ca.getContextProvider(), ca.getNodeFactory());
					a.loadXML(Spawn.class.getResourceAsStream(_name));
					_auto = a.compile();
				}
					
				((CompositeAutomata<C>)auto).add(_auto);
			} catch (JDOMException | IOException e) {
				e.printStackTrace();
			}
		}
	}
	static class Terminate<C> extends StateAdapter<C> {
		public void preAction(C context, ITimedAutomata<C> auto) {
			((CompositeAutomata<C>.NestedAutomata)auto).detach();
		}
	}
}
