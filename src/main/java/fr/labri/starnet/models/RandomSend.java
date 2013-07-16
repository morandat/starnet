package fr.labri.starnet.models;

import java.util.List;

import fr.labri.starnet.INode;
import fr.labri.starnet.Node;
import fr.labri.starnet.World;

public class RandomSend implements StimuliModel {

	@Override
	public void initWorld(World world) {
	}

	@Override
	public void nextRound(World world, long time) {
		boolean ok;
		List<Node> participants = world.getParticipants();
		do {
			INode n = participants.get(world.getRandom().nextInt(participants.size())).asINode();
			if(ok = n.isOnline())
				n.send(n.createMessage(null));
		} while(!ok);
	}
}
