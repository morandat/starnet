package fr.labri.starnet.policies;

import fr.labri.timedautomata.TimedAutomata.StateAdapter;

public class SendHello extends StateAdapter<MyBot> {

	@Override
	public void postAction(MyBot context) {
		context.sendMessage();
	}
}
