package fr.labri.starnet.policies;

import fr.labri.starnet.INode;
import fr.labri.starnet.Message;
import fr.labri.timedautomata.ITimedAutomata;
import fr.labri.timedautomata.TimedAutomata.StateAdapter;

public class SendHello extends StateAdapter<INode> {

	@Override
	public void postAction(INode context, ITimedAutomata<INode> auto) {
		context.send(context.createMessage(Message.Type.HELLO));
	}
}
