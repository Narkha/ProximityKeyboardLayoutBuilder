package es.csc.pklb.buider;

import java.util.Stack;

import es.csc.pklb.frecuency.KeyFrecuencyGraph;
import es.csc.pklb.grid.HexagonalWeightedGrid;

public class GridCache {
	private int maxRows;
	private int radius;
	private KeyFrecuencyGraph weights;
	
	private Stack<HexagonalWeightedGrid> stack = new Stack<HexagonalWeightedGrid>();
	
	public GridCache(int maxRows, int radius, KeyFrecuencyGraph weights) {
		this.maxRows = maxRows;
		this.radius = radius;
		this.weights = weights;
	}
	
	public HexagonalWeightedGrid get() {
		if (stack.isEmpty()) {
			return new HexagonalWeightedGrid(maxRows, radius, weights);
		}
		else {
			return stack.pop();
		}
	}
	
	public void release(HexagonalWeightedGrid item) {
		stack.push(item);
	}
	
}
