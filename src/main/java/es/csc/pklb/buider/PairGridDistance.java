package es.csc.pklb.buider;

import es.csc.pklb.grid.HexagonalWeightedRing;

class PairGridDistance implements java.util.Comparator< PairGridDistance > {
	HexagonalWeightedRing grid;
	double distance;
	
	public PairGridDistance(HexagonalWeightedRing grid, double distance) {
		this.grid = grid;
		this.distance = distance;
	}
	
	public int compare(PairGridDistance pair1, PairGridDistance pair2) {
		return (int) (10 * (pair1.distance - pair2.distance));
	}
}
