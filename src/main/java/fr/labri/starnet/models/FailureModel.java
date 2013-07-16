package fr.labri.starnet.models;

import java.util.ArrayList;

import fr.labri.starnet.Node;
import fr.labri.starnet.World;

public class FailureModel implements StimuliModel {

	final int nbTurn = 10;
	final int failureRate = 6;
	final int restoreRate = 3;
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
			chooseN(world, toenable, true, disabledList, enabledList);
		else 
			todisable = failureRate  * nb / 100;

		chooseN(world, todisable, false, enabledList, disabledList);
	}

	private void chooseN(World world, int count, boolean state, ArrayList<Node> from, ArrayList<Node> to) {
		while (count-- > 0 && !from.isEmpty()) {
			Node n = pickOne(world, from);
			n.setOnline(state);
			to.add(n);
		}
	}
	
	private Node pickOne(World world, ArrayList<Node> list) {
		int s = list.size();
		int r = world.getRandom().nextInt(s);
		Node n = list.get(r);
		if(--s == r)
			list.remove(s);
		else
			list.set(r, list.remove(s));
		return n;
	}
}
