package fr.labri.starnet.models;

public interface MoveModel {
	
	class MoveModelFactoy {
		public static MoveModel getRandomWalk() {
			return new MoveModel() {
			};
		}
	}
}