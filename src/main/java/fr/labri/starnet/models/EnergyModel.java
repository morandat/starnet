package fr.labri.starnet.models;

public interface EnergyModel {
	double energy(double range);
	
	static class EnergyModelFactory {
		public static EnergyModel getLinearEnergyModel() {
			return new EnergyModel() {
				@Override
				public double energy(double range) {
					return range;
				}
			};
		}

		public static EnergyModel getPowerEnergyModel(final double exponent,
				final double basicCost) {
			return new EnergyModel() {
				@Override
				public double energy(double range) {
					return Math.pow(range, exponent) + basicCost;
				}
			};
		}
	}
}