/*
 * Copyright (c) 2015
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
*/

package es.csc.pklb.builder;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import es.csc.pklb.buider.RingProximityBuilder;
import es.csc.pklb.frecuency.Key;
import es.csc.pklb.frecuency.KeyFrecuencyGraph;
import es.csc.pklb.grid.HexagonalWeightedGrid;
import es.csc.pklb.grid.Node;

public class RingProximityBuilderTest {

	@Test
	public void ThreeKeys() throws IOException, InterruptedException {
		String keysFile = "data/test/RingProximityBuilderTest/threeKeys.config",
				dataFile = "data/test/RingProximityBuilderTest/threeKeys.in";
		KeyFrecuencyGraph weights = new KeyFrecuencyGraph(keysFile, dataFile);
		
		HexagonalWeightedGrid grid = (new RingProximityBuilder(0, weights)).build();
		
		double expectedDistance = 2 * HexagonalWeightedGrid.INNER_RADIUS * 17; 
		assertEquals(expectedDistance, grid.totalDistance(), 0.000001);
		
		assertEquals("some keys are repeated", weights.size(), countFullNodes(grid));
		
		for(Key key : weights.keys()) {
			assertTrue( contains(grid, key) );
		}
	}

	@Test
	public void sevenKeysSimple() throws IOException, InterruptedException {
		String keysFile = "data/test/RingProximityBuilderTest/sevenKeys.config",
				dataFile = "data/test/RingProximityBuilderTest/sevenKeysSimple.in";
		KeyFrecuencyGraph weights = new KeyFrecuencyGraph(keysFile, dataFile);
		
		HexagonalWeightedGrid grid = (new RingProximityBuilder(0, weights)).build();
		
		HexagonalWeightedGrid expected = sevenKeysSimpleExpected(weights);
		
		assertEquals(expected.totalDistance(), grid.totalDistance(), 0.000001);		
		
		for(Key key : weights.keys()) {
			assertTrue( contains(grid, key) );
		}
	}

	private HexagonalWeightedGrid sevenKeysSimpleExpected(KeyFrecuencyGraph weights) {
		
		HexagonalWeightedGrid expected = new HexagonalWeightedGrid(1, weights);
		
		List<Node> expectedNodes = expected.nodes();		
		expectedNodes.get(0).setContent( weights.keys().get(0) );
		expectedNodes.get(1).setContent( weights.keys().get(1) );
		expectedNodes.get(2).setContent( weights.keys().get(5) );
		expectedNodes.get(3).setContent( weights.keys().get(6) );
		expectedNodes.get(4).setContent( weights.keys().get(2) );
		expectedNodes.get(5).setContent( weights.keys().get(3) );
		expectedNodes.get(6).setContent( weights.keys().get(4) );
		
		return expected;
	}

	@Test
	public void sevenKeysSimple1Row() throws IOException, InterruptedException {
		String keysFile = "data/test/RingProximityBuilderTest/sevenKeys.config",
				dataFile = "data/test/RingProximityBuilderTest/sevenKeysSimple.in";
		KeyFrecuencyGraph weights = new KeyFrecuencyGraph(keysFile, dataFile);
		
		HexagonalWeightedGrid grid = (new RingProximityBuilder(1, weights)).build();
		
		assertEquals(3, grid.radius());
		
		HexagonalWeightedGrid expected = sevenKeysSimple1RowExpected(weights);
		
		assertEquals(expected.totalDistance(), grid.totalDistance(), 0.000001);		
		
		for(Key key : weights.keys()) {
			assertTrue( contains(grid, key) );
		}
	}

	private HexagonalWeightedGrid sevenKeysSimple1RowExpected(KeyFrecuencyGraph weights) {
		
		HexagonalWeightedGrid expected = new HexagonalWeightedGrid(1, 3, weights);
		
		List<Node> expectedNodes = expected.nodes();		
		expectedNodes.get(0).setContent( weights.keys().get(0) );
		expectedNodes.get(1).setContent( weights.keys().get(4) );
		expectedNodes.get(2).setContent( weights.keys().get(1) );
		expectedNodes.get(3).setContent( weights.keys().get(3) );
		expectedNodes.get(4).setContent( weights.keys().get(5) );
		expectedNodes.get(5).setContent( weights.keys().get(2) );
		expectedNodes.get(6).setContent( weights.keys().get(6) );
		
		return expected;
	}
	
	@Test
	public void sevenKeysComplex() throws IOException, InterruptedException {
		String keysFile = "data/test/RingProximityBuilderTest/sevenKeys.config",
				dataFile = "data/test/RingProximityBuilderTest/sevenKeysComplex.in";
		KeyFrecuencyGraph weights = new KeyFrecuencyGraph(keysFile, dataFile);
		
		HexagonalWeightedGrid grid = (new RingProximityBuilder(0, weights)).build();
		
		HexagonalWeightedGrid expected = sevenKeysComplexExptected(weights);
		
		assertEquals(expected.totalDistance(), grid.totalDistance(), 0.000001);
		
		for(Key key : weights.keys()) {
			assertTrue( contains(grid, key) );
		}
	}

	private HexagonalWeightedGrid sevenKeysComplexExptected(KeyFrecuencyGraph weights) {
		
		HexagonalWeightedGrid expected = new HexagonalWeightedGrid(1, weights);
		
		List<Node> expectedNodes = expected.nodes();		
		expectedNodes.get(0).setContent( weights.keys().get(0) );
		expectedNodes.get(1).setContent( weights.keys().get(1) );
		expectedNodes.get(2).setContent( weights.keys().get(3) );
		expectedNodes.get(3).setContent( weights.keys().get(5) );
		expectedNodes.get(4).setContent( weights.keys().get(4) );
		expectedNodes.get(5).setContent( weights.keys().get(6) );
		expectedNodes.get(6).setContent( weights.keys().get(2) );
		
		return expected;
	}

	private Object countFullNodes(HexagonalWeightedGrid grid) {
		int fullNodes = 0;
		
		for(Node node : grid.nodes()) {
			if (!node.isEmpty()) {
				++fullNodes;
			}
		}
		
		return fullNodes;
	}

	private boolean contains(HexagonalWeightedGrid grid, Key key) {
		for(Node node : grid.nodes()) {
			if (node.getContent() == key) {
				return true;				
			}
		}
		
		return false;
	} 
	
	@Test
	public void nineKeysSimple() throws IOException, InterruptedException {
		String keysFile = "data/test/RingProximityBuilderTest/nineKeys.config",
				dataFile = "data/test/RingProximityBuilderTest/nineKeysSimple.in";
		KeyFrecuencyGraph weights = new KeyFrecuencyGraph(keysFile, dataFile);
		
		HexagonalWeightedGrid grid = (new RingProximityBuilder(0, weights)).build();
		
		HexagonalWeightedGrid expected = nineKeysSimpleExpected(weights);
		
		assertEquals(expected.totalDistance(), grid.totalDistance(), 0.000001);		
		
		assertEquals("some keys are repeated", weights.size(), countFullNodes(grid));
		
		for(Key key : weights.keys()) {
			assertTrue( contains(grid, key) );
		}
	}
	
	@Test
	public void nineKeysSimple3Rows() throws IOException, InterruptedException {
		String keysFile = "data/test/RingProximityBuilderTest/nineKeys.config",
				dataFile = "data/test/RingProximityBuilderTest/nineKeysSimple.in";
		KeyFrecuencyGraph weights = new KeyFrecuencyGraph(keysFile, dataFile);
		
		HexagonalWeightedGrid grid = (new RingProximityBuilder(3, weights)).build();
		
		HexagonalWeightedGrid expected = nineKeysSimpleExpected(weights);
		
		assertEquals(expected.totalDistance(), grid.totalDistance(), 0.000001);		
		
		assertEquals("some keys are repeated", weights.size(), countFullNodes(grid));
		
		for(Key key : weights.keys()) {
			assertTrue( contains(grid, key) );
		}
	}

	private HexagonalWeightedGrid nineKeysSimpleExpected(KeyFrecuencyGraph weights) {
		
		HexagonalWeightedGrid expected = new HexagonalWeightedGrid(2, weights);
		
		List<Node> expectedNodes = expected.nodes();		
		expectedNodes.get(0).setContent( weights.keys().get(0) );
		expectedNodes.get(1).setContent( weights.keys().get(3) );
		expectedNodes.get(2).setContent( weights.keys().get(5) );
		expectedNodes.get(3).setContent( weights.keys().get(7) );
		expectedNodes.get(4).setContent( weights.keys().get(1) );
		expectedNodes.get(5).setContent( weights.keys().get(2) );
		expectedNodes.get(6).setContent( weights.keys().get(4) );
		expectedNodes.get(9).setContent( weights.keys().get(6) );
		expectedNodes.get(15).setContent( weights.keys().get(8) );
		
		return expected;
	}
		
	public void validConfiguration3Rows() throws IOException {
		String keysFile = "data/test/RingProximityBuilderTest/thirteenKeys.config",
				dataFile = "data/test/RingProximityBuilderTest/empty.in";
		KeyFrecuencyGraph weights = new KeyFrecuencyGraph(keysFile, dataFile);
		new RingProximityBuilder(3, weights);
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void invalidConfiguration3Rows() throws IOException {
		String keysFile = "data/test/RingProximityBuilderTest/fourteenKeys.config",
				dataFile = "data/test/RingProximityBuilderTest/empty.in";
		KeyFrecuencyGraph weights = new KeyFrecuencyGraph(keysFile, dataFile);
		new RingProximityBuilder(3, weights);
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void invalidConfiguration5Rows() throws IOException {
		String keysFile = "data/test/RingProximityBuilderTest/thirtyKeys.config",
				dataFile = "data/test/RingProximityBuilderTest/empty.in";
		KeyFrecuencyGraph weights = new KeyFrecuencyGraph(keysFile, dataFile);
		new RingProximityBuilder(5, weights);
	}
	
	public void validConfiguration5Rows() throws IOException {
		String keysFile = "data/test/RingProximityBuilderTest/thirteenKeys.config",
				dataFile = "data/test/RingProximityBuilderTest/empty.in";
		KeyFrecuencyGraph weights = new KeyFrecuencyGraph(keysFile, dataFile);
		new RingProximityBuilder(3, weights);
	}
}
