package es.csc.pklb.buider;

import es.csc.pklb.grid.HexagonalWeightedGrid;

class PairGridDistance implements java.util.Comparator< PairGridDistance > {
	HexagonalWeightedGrid grid;
	double distance;
	
	public PairGridDistance(HexagonalWeightedGrid grid, double distance) {
		this.grid = grid;
		this.distance = distance;
	}
	
	public int compare(PairGridDistance pair1, PairGridDistance pair2) {
		return (int) (10 * (pair1.distance - pair2.distance));
	}
}
