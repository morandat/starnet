package fr.labri.starnet.policies.commons;

import java.util.Map;

import fr.labri.starnet.INode;
import fr.labri.starnet.Message;
import fr.labri.timedautomata.ITimedAutomata;
import fr.labri.timedautomata.TimedAutomata.StateAdapter;

public class HelloActions {

	public static class AddToHelloSet extends StateAdapter<INode> {
		@Override
		public void postAction(INode context, ITimedAutomata<INode> auto) {
			Utils.addToSet(context.getStorage(), CommonVar.HELLO_SET, CommonVar.CURRENT_MESSAGE);
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
