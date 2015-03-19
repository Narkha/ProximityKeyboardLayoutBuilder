package es.csc.proximitykeyboardlayoutbuilder.proximitybuilder;

import es.csc.proximitykeyboardlayoutbuilder.HexagonalWeightedGrid;

class PairGridDistance {
	HexagonalWeightedGrid grid;
	double distance;
	
	public PairGridDistance(HexagonalWeightedGrid grid, double distance) {
		this.grid = grid;
		this.distance = distance;
	}
}
