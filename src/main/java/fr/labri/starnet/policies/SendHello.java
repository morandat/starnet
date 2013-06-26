package fr.labri.starnet.policies;

import fr.labri.starnet.INode;
import fr.labri.starnet.Message;
import fr.labri.timedautomata.TimedAutomata.StateAdapter;

public class SendHello extends StateAdapter<INode> {

	@Override
	public void postAction(INode context) {
		context.send(context.createMessage(Message.Type.HELLO));
	}
}
