package es.csc.pklb.buider;

import java.util.Stack;

import es.csc.pklb.grid.HexagonalWeightedRing;
import es.csc.pklb.grid.Node;

public class GridCache {
	private HexagonalWeightedRing model;
	
	private Stack<HexagonalWeightedRing> stack = new Stack<HexagonalWeightedRing>();
	
	public GridCache(HexagonalWeightedRing grid) {
		this.model = (HexagonalWeightedRing) grid.clone();
		clear(this.model);
	}
	
	public HexagonalWeightedRing get() {
		if (stack.isEmpty()) {
			return (HexagonalWeightedRing) model.clone();
		}
		else {			
			HexagonalWeightedRing grid = stack.pop();
			clear(grid);
			return grid;
		}
	}
	
	public void release(HexagonalWeightedRing item) {
		stack.push(item);
	}
	
	private void clear(HexagonalWeightedRing grid) {
		for(Node node : grid) {
			node.setContent(null);
		}
	}
}
