package es.csc.proximitykeyboardlayoutbuilder.proximitybuilder;

import java.util.Stack;

import es.csc.proximitykeyboardlayoutbuilder.HexagonalWeightedGrid;
import es.csc.proximitykeyboardlayoutbuilder.KeyFrecuencyGraph;

public class GridCache {	
	private int radius;
	private KeyFrecuencyGraph weights;
	
	private Stack<HexagonalWeightedGrid> stack = new Stack<HexagonalWeightedGrid>();
	
	public GridCache(int radius, KeyFrecuencyGraph weights) {
		this.radius = radius;
		this.weights = weights;
	}
	
	public HexagonalWeightedGrid get() {
		if (stack.isEmpty()) {
			return new HexagonalWeightedGrid(radius, weights);
		}
		else {
			return stack.pop();
		}
	}
	
	public void release(HexagonalWeightedGrid item) {
		stack.push(item);
	}
	
}
