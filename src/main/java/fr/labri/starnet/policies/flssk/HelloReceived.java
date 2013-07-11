package fr.labri.starnet.policies.flssk;

import fr.labri.starnet.INode;
import fr.labri.starnet.Message;
import fr.labri.timedautomata.TimedAutomata;

public class HelloReceived extends TimedAutomata.TransitionAdapter<INode> {

	@Override
	 public boolean isValid(INode context) {
		Message[] r = context.receive();
		for(Message m: r)
			if(m.getType() == Message.Type.HELLO)
				//Ajouter les voisins dans un graph
				return true;
		return false;
	}

}
