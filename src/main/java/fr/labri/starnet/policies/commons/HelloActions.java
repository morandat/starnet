package fr.labri.starnet.policies.commons;

import java.util.Map;

import fr.labri.starnet.INode;
import fr.labri.starnet.Message;
import fr.labri.tima.ITimedAutomata.ActionAdapter;

public class HelloActions {

	public static class AddToHelloSet extends ActionAdapter<INode> {
		@Override
		public void postAction(INode context, String key) {
			Utils.addToSet(context.getStorage(), CommonVar.HELLO_SET, CommonVar.CURRENT_MESSAGE);
		}
	}
	
	public static class CleanHelloSet extends ActionAdapter<INode> {
		Map<String, Object> storage;
		HelloSet hello_set;
		
		@Override
		public void postAction(INode context, String key) {
			storage = context.getStorage();
			hello_set = (HelloSet) storage.get(CommonVar.HELLO_SET);
			hello_set.clean(CommonVar.TIMEOUT, context.getTime());
		}
	}
	
	public static class SendHello extends ActionAdapter<INode> {
		@Override
		public void postAction(INode context, String key) {
			context.send(context.createMessage(Message.Type.HELLO));
		}
	}
}
