package fr.labri.starnet.policies;

import fr.labri.starnet.Message;
import fr.labri.timedautomata.TimedAutomata;

public class HelloReceived extends TimedAutomata.TransitionAdapter<MyBot> {

	@Override
	 public boolean isValid(MyBot context) {
		Message[] r = context.getReceivedMessages();
		for(Message m: r)
			if(m.getType() == Message.Type.HELLO)
				return true;
		return false;
	}

}
