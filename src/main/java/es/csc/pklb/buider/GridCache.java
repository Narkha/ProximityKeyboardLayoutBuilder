package es.csc.pklb.buider;

import java.util.Stack;

import es.csc.pklb.frecuency.KeyFrecuencyGraph;
import es.csc.pklb.grid.HexagonalWeightedRing;

public class GridCache {
	private int maxRows;
	private int radius;
	private KeyFrecuencyGraph weights;
	
	private Stack<HexagonalWeightedRing> stack = new Stack<HexagonalWeightedRing>();
	
	public GridCache(int maxRows, int radius, KeyFrecuencyGraph weights) {
		this.maxRows = maxRows;
		this.radius = radius;
		this.weights = weights;
	}
	
	public HexagonalWeightedRing get() {
		if (stack.isEmpty()) {
			return new HexagonalWeightedRing(maxRows, radius, weights);
		}
		else {
			return stack.pop();
		}
	}
	
	public void release(HexagonalWeightedRing item) {
		stack.push(item);
	}
	
}
