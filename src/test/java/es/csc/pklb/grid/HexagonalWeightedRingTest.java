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
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import es.csc.pklb.frecuency.KeyFrecuencyGraph;

public class HexagonalWeightedRingTest {
	KeyFrecuencyGraph frecuencies;
	
	@Before
	public void setUp() throws IOException {
		String configFile = "data/test/HexagonalWeightedGridTest/weightedDistances.config",
				dataFile = "data/test/HexagonalWeightedGridTest/weightedDistances.in";
		
		frecuencies = new KeyFrecuencyGraph(configFile, dataFile);		
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void constructorEvenRows() {
		new HexagonalWeightedRing(2, 5, true, frecuencies);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void constructorEvenNodesInMiddleRow() {
		new HexagonalWeightedRing(5, 5, true, frecuencies);
	}
	
	@Test
	public void numberNodesRadiusZero() {
		HexagonalWeightedRing grid = new HexagonalWeightedRing(1, 1, true, frecuencies);
		
		assertEquals(0, grid.radius());
		
		assertEquals(1, grid.rows());
		assertEquals(1, grid.columns());
		
		assertEquals(1, grid.nodesInRadius(0).size());
	}
	
	@Test
	public void numberNodesRadiusOne() {
		HexagonalWeightedRing grid = new HexagonalWeightedRing(3, 3, true, frecuencies);
		
		assertEquals(1, grid.radius());
		
		assertEquals(3, grid.rows());
		assertEquals(3, grid.columns());
		
		assertEquals(1, grid.nodesInRadius(0).size());
		assertEquals(6, grid.nodesInRadius(1).size());
	}

	@Test
	public void numberNodesRadiusTwo() {
		HexagonalWeightedRing grid = new HexagonalWeightedRing(5, 5, false, frecuencies);
		
		assertEquals(2, grid.radius());
		
		assertEquals(5, grid.rows());
		assertEquals(5, grid.columns());
		
		assertEquals(1, grid.nodesInRadius(0).size());
		assertEquals(6, grid.nodesInRadius(1).size());
		assertEquals(12, grid.nodesInRadius(2).size());
	}

	@Test
	public void numberNodesRadiusThree() {
		HexagonalWeightedRing grid = new HexagonalWeightedRing(7, 7, true, frecuencies);
		
		assertEquals(3, grid.radius());
		
		assertEquals(7, grid.rows());
		assertEquals(7, grid.columns());
		
		assertEquals(1, grid.nodesInRadius(0).size());
		assertEquals(6, grid.nodesInRadius(1).size());
		assertEquals(12, grid.nodesInRadius(2).size());
		assertEquals(18, grid.nodesInRadius(3).size());
	}	

	@Test
	public void numberNodesRadiusThreeRowsThree() {
		HexagonalWeightedRing grid = new HexagonalWeightedRing(3, 7, true, frecuencies);
		
		assertEquals(3, grid.radius());
		
		assertEquals(3, grid.rows());
		assertEquals(7, grid.columns());
		
		assertEquals(1, grid.nodesInRadius(0).size());
		assertEquals(6, grid.nodesInRadius(1).size());
		assertEquals(6, grid.nodesInRadius(2).size());
		assertEquals(6, grid.nodesInRadius(3).size());
	}

	@Test
	public void numberNodesRadiusThreeRowsFive() {
		HexagonalWeightedRing grid = new HexagonalWeightedRing(5, 7, false, frecuencies);
		
		assertEquals(3, grid.radius());
		
		assertEquals(5, grid.rows());
		assertEquals(7, grid.columns());
		
		assertEquals(1, grid.nodesInRadius(0).size());
		assertEquals(6, grid.nodesInRadius(1).size());
		assertEquals(12, grid.nodesInRadius(2).size());
		assertEquals(10, grid.nodesInRadius(3).size());
	}
	
	@Test
	public void nodesInRadius() {
		HexagonalWeightedRing grid = new HexagonalWeightedRing(3, 7, true, frecuencies);
		
		Node center = grid.get(1, 3);
		
		for (int i = 0, n = grid.radius(); i < n; ++i) {
			List<Node> listInRadius = grid.nodesInRadius(i),
						listOfNeighbours = grid.neighboursAtSteps(center, i);
			
			assertEquals(listOfNeighbours.size(), listInRadius.size());
			for(Node node : listInRadius) {
				assertTrue("Radius " + i, listOfNeighbours.contains(node));
			}
		}
	}


	@Test
	public void cloneTest() {
		HexagonalWeightedRing grid = new HexagonalWeightedRing(3, 7, true, frecuencies);
		HexagonalWeightedRing clone = (HexagonalWeightedRing) grid.clone();
		
		assertEquals(grid.radius(), clone.radius());
		assertEquals(grid.size(), clone.size());
		
		List<Node> allNodes = new ArrayList<Node>();
		for(Node node : clone) {
			allNodes.add(node);
		}
		
		for(int i = 0, n = clone.radius(); i < n; ++i) {
			List<Node> nodes = clone.nodesInRadius(i);
			for(Node node : nodes) {
				assertTrue("Radius " + i, allNodes.contains(node));
			}
		}
	}

	@Test
	public void rotate() {
		HexagonalWeightedRing grid = new HexagonalWeightedRing(7, 7, true, frecuencies);
		
		HexagonalGridTest.fillGraph(grid);
		
		HexagonalWeightedRing rotationGrid = (HexagonalWeightedRing) grid.clone();
		
		for (int i = 1; i <= HexagonalGrid.EDGES; ++i) {
			rotationGrid.rotate();
			
			assertEquals(grid.nodesInRadius(0).get(0).getContent(), 
							grid.nodesInRadius(0).get(0).getContent());
						
			for(int r = 1, R = grid.radius(), size; r <= R; ++r) {
				size = HexagonalGrid.EDGES * r;
				List<Node> gridNodes = grid.nodesInRadius(r);
				List<Node> rotationNodes = rotationGrid.nodesInRadius(r);
				
				for(int j = 0, index2; j < size; j += r) {
					index2 = (j + i * r) % size;
					
					assertEquals(gridNodes.get(j).getContent(), rotationNodes.get(index2).getContent());
				}
			}
		}

	}
}
