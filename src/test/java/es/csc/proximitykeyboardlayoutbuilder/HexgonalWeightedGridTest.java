/*
 * Copyright (c) 2015
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
*/

package es.csc.proximitykeyboardlayoutbuilder;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

public class HexgonalWeightedGridTest {

	@Test
	public void weightedDistances() throws IOException {
		String configFile = "data/test/weightedDistances.config",
				dataFile = "data/test/weightedDistances.in";
		KeyFrecuencyGraph frecuencies = new KeyFrecuencyGraph(configFile, dataFile);
		
		HexagonalWeightedGrid grid = new HexagonalWeightedGrid(1, frecuencies);
		
		List<Node<Integer>> nodes = grid.getNodes();
		for(int i = 0, n = frecuencies.getNodesSize(); i < n; ++i) {
			nodes.get(i).setContent(i);
		}
		
		double expetedDistanceFromZero = 
				frecuencies.getWeight(0, 1) * (2 * HexagonalWeightedGrid.INNER_RADIUS)
				+ frecuencies.getWeight(0, 2) * (2 * HexagonalWeightedGrid.INNER_RADIUS)
				+ frecuencies.getWeight(0, 3) * (2 * HexagonalWeightedGrid.INNER_RADIUS);				
		assertEquals(expetedDistanceFromZero, grid.distanceToOtherNodes(nodes.get(0)), 0.01);
		
		double expetedDistanceFromOne = 
				frecuencies.getWeight(1, 0) * (2 * HexagonalWeightedGrid.INNER_RADIUS)
				+ frecuencies.getWeight(1, 2) * (2 * HexagonalWeightedGrid.INNER_RADIUS)
				+ frecuencies.getWeight(1, 3) * (3 * HexagonalWeightedGrid.OUTER_RADIUS);				
		assertEquals(expetedDistanceFromOne, grid.distanceToOtherNodes(nodes.get(1)), 0.01);
	}

}
