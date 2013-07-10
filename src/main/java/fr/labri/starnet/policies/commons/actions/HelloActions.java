package fr.labri.starnet.policies.commons.actions;

import java.util.Map;

import fr.labri.starnet.INode;
import fr.labri.starnet.Message;
import fr.labri.starnet.policies.commons.HelloSet;
import fr.labri.timedautomata.ITimedAutomata;
import fr.labri.timedautomata.TimedAutomata.StateAdapter;

public class HelloActions {

	public static class AddToHelloSet extends StateAdapter<INode> {
		Map<String, Object> storage;
		HelloSet hello_set;

		@Override
		public void postAction(INode context, ITimedAutomata<INode> auto) {
			storage = context.getStorage();
			Message msg = (Message) storage
					.get(CommonVar.CURRENT_MESSAGE);
			hello_set = (HelloSet) storage.get(CommonVar.HELLO_SET);
			hello_set.add(msg);
		}
	}
	
	public static class CleanHelloSet extends StateAdapter<INode> {
		Map<String, Object> storage;
		HelloSet hello_set;
		
		@Override
		public void postAction(INode context, ITimedAutomata<INode> auto) {
			storage = context.getStorage();
			hello_set = (HelloSet) storage.get(CommonVar.HELLO_SET);
			hello_set.clean(CommonVar.TIMEOUT, context.getTime());
		}
	}
	
	public static class SendHello extends StateAdapter<INode> {
		@Override
		public void postAction(INode context, ITimedAutomata<INode> auto) {
			context.send(context.createMessage(Message.Type.HELLO));
		}
	}
}
