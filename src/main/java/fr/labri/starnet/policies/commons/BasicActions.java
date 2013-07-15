package fr.labri.starnet.policies.commons;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.Map;


import fr.labri.starnet.INode;
import fr.labri.starnet.Message;
import fr.labri.tima.ITimedAutomata.ActionAdapter;

public class BasicActions {
	
	public static class InitEnv extends ActionAdapter<INode> {
		public Map<String, Object> storage;

		@Override
		public void preAction(INode context, String key) {
			storage = context.getStorage();
			storage.put(CommonVar.DATA_SET, new DataSet());
			storage.put(CommonVar.HELLO_SET, new HelloSet());
			storage.put(CommonVar.SAVED_MAILBOX, new ArrayDeque<Message>());
		}
	}

	public static class SaveMailBox extends ActionAdapter<INode> {
		public Map<String, Object> storage;

		@Override
		public void postAction(INode context, String key) {
			storage = context.getStorage();
			Message[] r = context.receive();
			Deque<Message> stack = new ArrayDeque<Message>(Arrays.asList(r));
			storage.put(CommonVar.SAVED_MAILBOX, stack);
		}
	}

	
}
