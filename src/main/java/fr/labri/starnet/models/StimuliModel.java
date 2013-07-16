package fr.labri.starnet.models;

import fr.labri.starnet.World;

public interface StimuliModel {
	void initWorld(World world);
	void nextRound(World world, long time);
}
