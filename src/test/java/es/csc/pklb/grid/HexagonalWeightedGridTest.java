/*
 * Copyright (c) 2015
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
*/

package es.csc.pklb.grid;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import es.csc.pklb.frecuency.Key;
import es.csc.pklb.frecuency.KeyFrecuencyGraph;
import es.csc.pklb.grid.HexagonalWeightedGrid;
import es.csc.pklb.grid.Node;

public class HexagonalWeightedGridTest {

	@Test
	public void weightedDistances() throws IOException {
		String configFile = "data/test/HexagonalWeightedGridTest/weightedDistances.config",
				dataFile = "data/test/HexagonalWeightedGridTest/weightedDistances.in";
		KeyFrecuencyGraph frecuencies = new KeyFrecuencyGraph(configFile, dataFile);
		
		HexagonalWeightedGrid grid = new HexagonalWeightedGrid(3, 3, frecuencies);
		
		List<Key> keys = frecuencies.keys();
		
		grid.get(1, 1).setContent( keys.get(0) );
		grid.get(0, 0).setContent( keys.get(1) );
		grid.get(0, 1).setContent( keys.get(2) );
		grid.get(1, 2).setContent( keys.get(3) );
		
		double expetedDistanceFromNode_1_1 = 
				frecuencies.getFrecuency(keys.get(0), keys.get(1)) * (2 * HexagonalWeightedGrid.INNER_RADIUS)
				+ frecuencies.getFrecuency(keys.get(0), keys.get(2)) * (2 * HexagonalWeightedGrid.INNER_RADIUS)
				+ frecuencies.getFrecuency(keys.get(0), keys.get(3)) * (2 * HexagonalWeightedGrid.INNER_RADIUS);
		Node node_1_1 = grid.get(1, 1);
		assertEquals(expetedDistanceFromNode_1_1, grid.distanceFrom(node_1_1), 0.0001);
		
		double expetedDistanceFrom_0_0 = 
				frecuencies.getFrecuency(keys.get(1), keys.get(0)) * (2 * HexagonalWeightedGrid.INNER_RADIUS)
				+ frecuencies.getFrecuency(keys.get(1), keys.get(2)) * (2 * HexagonalWeightedGrid.INNER_RADIUS)
				+ frecuencies.getFrecuency(keys.get(1), keys.get(3)) * (3 * HexagonalWeightedGrid.OUTER_RADIUS);
		Node node_0_0 = grid.get(0, 0);
		assertEquals(expetedDistanceFrom_0_0, grid.distanceFrom(node_0_0), 0.0001);
	}

}
