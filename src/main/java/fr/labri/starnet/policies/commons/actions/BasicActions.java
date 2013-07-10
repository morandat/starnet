package fr.labri.starnet.policies.commons.actions;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.Map;

import org.jdom2.JDOMException;

import fr.labri.starnet.INode;
import fr.labri.starnet.Message;
import fr.labri.starnet.policies.commons.DataSet;
import fr.labri.starnet.policies.commons.HelloSet;
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
	
	public static class InitEnv extends StateAdapter<INode> {
		public Map<String, Object> storage;

		@Override
		public void preAction(INode context, ITimedAutomata<INode> auto) {
			storage = context.getStorage();
			storage.put(CommonVar.DATA_SET, new DataSet());
			storage.put(CommonVar.HELLO_SET, new HelloSet());
			storage.put(CommonVar.SAVED_MAILBOX, new ArrayDeque<Message>());
		}
	}

	public static class SaveMailBox extends StateAdapter<INode> {
		public Map<String, Object> storage;

		@Override
		public void postAction(INode context, ITimedAutomata<INode> auto) {
			storage = context.getStorage();
			Message[] r = context.receive();
			Deque<Message> stack = new ArrayDeque<Message>(Arrays.asList(r));
			storage.put(CommonVar.SAVED_MAILBOX, stack);
		}
	}

	
}
