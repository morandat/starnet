package fr.labri.starnet;

import java.util.Random;

import fr.labri.IntBitSet;

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
					long max = dim.dotProduct();
					if(max > Integer.MAX_VALUE)
						throw new RuntimeException("Integer overflow not supported");
					
					
					for(Node node: world.getParticipants()) {
						long pos = nextPos((int)max);
						double angle = rnd.nextDouble() * 2 * Math.PI;
						node.setPosition(OrientedPosition.from(to2DCoord(pos, dim), angle));
					}
				}
				
				private Position to2DCoord(long c, Position dimensions) {
					int x = (int) c % dimensions._x;
					int y = (int) c / dimensions._x;
					if(y%2 == 1)
						x = dimensions._x - x;
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
