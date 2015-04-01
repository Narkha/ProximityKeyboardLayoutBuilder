package es.csc.pklb.buider;

import es.csc.pklb.grid.HexagonalWeightedGrid;

class PairGridDistance {
	HexagonalWeightedGrid grid;
	double distance;
	
	public PairGridDistance(HexagonalWeightedGrid grid, double distance) {
		this.grid = grid;
		this.distance = distance;
	}
}
