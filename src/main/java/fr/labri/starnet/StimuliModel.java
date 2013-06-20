package fr.labri.starnet;

public interface StimuliModel {
	void initWorld(World world);
	void nextRound(World world, long time);
}
