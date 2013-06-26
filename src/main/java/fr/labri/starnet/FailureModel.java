package fr.labri.starnet;

import java.util.ArrayList;
import java.util.Random;

public class FailureModel implements StimuliModel {
	public final static Long FAILURE_SEED = Long.getLong("starnet.failuremodel.seed", System.nanoTime());

	final int nbTurn = 10;
	final int failureRate = 6;
	final int restoreRate = 3;
	final Random rnd = new Random(FAILURE_SEED);
	final ArrayList<Node> enabledList = new ArrayList<Node>();
	final ArrayList<Node> disabledList = new ArrayList<Node>();

	public void initWorld(final World world) {
		enabledList.addAll(world.getParticipants());
	}

	public void nextRound(final World world, final long time) {
		if (time % nbTurn > 0)
			return;

		int nb = enabledList.size() + disabledList.size();

		int toenable = restoreRate * nb / 100;
		int todisable = (failureRate - restoreRate) * nb / 100;

		if (!disabledList.isEmpty())
			chooseN(toenable, true, disabledList, enabledList);
		else 
			todisable = failureRate  * nb / 100;

		chooseN(todisable, false, enabledList, disabledList);
		
		INode n = enabledList.get(rnd.nextInt(enabledList.size())).asINode();

		n.send(n.createMessage(null));
	}

	private void chooseN(int count, final boolean state, final ArrayList<Node> from, final ArrayList<Node> to) {
		while (count-- > 0 && !from.isEmpty()) {
			Node n = pickOne(from);
			n.setOnline(state);
			to.add(n);
		}
	}
	
	private Node pickOne(final ArrayList<Node> list) {
		int s = list.size();
		int r = rnd.nextInt(s);
		Node n = list.get(r);
		if(--s == r)
			list.remove(s);
		else
			list.set(r, list.remove(s));
		return n;
	}
}
