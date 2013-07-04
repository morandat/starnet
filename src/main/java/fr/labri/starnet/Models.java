package fr.labri.starnet;

import fr.labri.starnet.INode.EnergyModel;

public class Models {
	static EnergyModel getLinearEnergyModel() {
		return new EnergyModel() {
			@Override
			public double energy(double range) {
				return range;
			}
		};
	}
	
	static EnergyModel getPowerEnergyModel(final double exponent, final double basicCost) {
		return new EnergyModel() {
			@Override
			public double energy(double range) {
				return Math.pow(range, exponent) + basicCost ;
			}
		};
	}
	
	static MoveModel getRandomWalk() {
		return new MoveModel() {
		};
	}
	
	public interface MoveModel {
	}
}
