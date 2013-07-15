package fr.labri.starnet.policies.flssk;

import fr.labri.starnet.INode;
import fr.labri.starnet.Message;
import fr.labri.tima.ITimedAutomata;

public class HelloReceived extends ITimedAutomata.PredicateAdapter<INode> {

	@Override
	 public boolean isValid(INode context, String key) {
		Message[] r = context.receive();
		for(Message m: r)
			if(m.getType() == Message.Type.HELLO)
				//Ajouter les voisins dans un graph
				return true;
		return false;
	}

}
