package fr.labri.starnet.models;

import java.util.Random;

import fr.labri.IntBitSet;
import fr.labri.starnet.Node;
import fr.labri.starnet.OrientedPosition;
import fr.labri.starnet.Position;
import fr.labri.starnet.World;

public interface SpreadModel {
	void spread(World world);
	
	 class SpreadModelFactory {
			public final static Long SPREAD_SEED = Long.getLong("starnet.spread.seed", System.nanoTime());
			public static SpreadModel getRandomModel() {
				return new SpreadModel() {
					final Random rnd = new Random(SPREAD_SEED);
					IntBitSet usedPosition = new IntBitSet();
					
					public void spread(World world) {
						Position dim = world.getDimension();
						long max = dim.dimension();
						if(max > Integer.MAX_VALUE)
							throw new RuntimeException("Integer overflow not supported");
						
						
						for(Node node: world.getParticipants()) {
							long pos = nextPos((int)max);
							double angle = rnd.nextDouble() * 2 * Math.PI;
							node.setPosition(OrientedPosition.from(to2DCoord(pos, dim), angle));
						}
					}
					
					private Position to2DCoord(long c, Position dimensions) {
						int dx = dimensions.getX();
						int x = (int) c % dx;
						int y = (int) c / dx;
						if(y%2 == 1)
							x = dx - x;
						return new Position(x, y);
					}
					
					private int nextPos(int max) {
						int pos;
						do pos = rnd.nextInt(max);
						while(!usedPosition.add(pos));
						return pos;
					}
				};
			}
		}
}
