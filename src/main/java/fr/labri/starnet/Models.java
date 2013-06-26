package fr.labri.starnet;

import fr.labri.starnet.INode.EnergyModel;

public class Models {
	static EnergyModel getLinearEnergyModel() {
		return new EnergyModel() {
			@Override
			public double energy(double rangeMax, double range) {
				return range / rangeMax;
			}
			
			@Override
			public double distance(double rangeMax, double power) {
				return rangeMax * power;
			}
		};
	}
	
	static EnergyModel getPowerEnergyModel(final double exponent, final double basicCost) {
		return new EnergyModel() {
			@Override
			public double energy(double rangeMax, double range) {
				return Math.pow(range, exponent) + basicCost ;
			}
			
			@Override
			public double distance(double rangeMax, double power) {
				return Math.pow(power - basicCost, 1 / exponent);
			}
		};
	}
}
