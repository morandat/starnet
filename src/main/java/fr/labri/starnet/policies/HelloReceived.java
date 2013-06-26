package fr.labri.starnet.policies;

import fr.labri.starnet.INode;
import fr.labri.starnet.Message;
import fr.labri.timedautomata.TimedAutomata;

public class HelloReceived extends TimedAutomata.TransitionAdapter<INode> {

	@Override
	 public boolean isValid(INode context) {
		Message[] r = context.receive();
		for(Message m: r)
			if(m.getType() == Message.Type.HELLO)
				return true;
		return false;
	}

}
