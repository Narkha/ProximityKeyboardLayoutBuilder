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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.junit.Test;

public class HexagonalGridTest {

	@Test
	public void testNumberNodesRadiusZero() {
		HexagonalGrid grid = new HexagonalGrid(0);
		assertEquals(1, grid.getNumberNodes());
		assertEquals(1, grid.getNodesInRadius(0).size());
	}

	@Test
	public void testNumberNodesRadiusOne() {
		HexagonalGrid grid = new HexagonalGrid(1);
		assertEquals(7, grid.getNumberNodes());
		assertEquals(1, grid.getNodesInRadius(0).size());
		assertEquals(6, grid.getNodesInRadius(1).size());
	}
	

	@Test
	public void testNumberNodesRadius2() {
		HexagonalGrid grid = new HexagonalGrid(2);
		assertEquals(19, grid.getNumberNodes());
		assertEquals(1, grid.getNodesInRadius(0).size());
		assertEquals(6, grid.getNodesInRadius(1).size());
		assertEquals(12, grid.getNodesInRadius(2).size());
	}
	
	@Test
	public void testRadiusGrap() {
		for (int r = 0; r < 3; ++r) {
			HexagonalGrid grid = new HexagonalGrid(r);
			assertEquals(r, grid.getRadius());
		}
	}
	
	@Test
	public void testDistancesAllNodesRadiusOne() {
		HexagonalGrid grid = new HexagonalGrid(1);
		
		List<Node> nodes = grid.getNodes();
		for(Node node : nodes) {
			node.setContent( new Key("0") );
		}
		
		Node center = grid.getNodesInRadius(0).get(0);
		assertEquals(6 * 2 * HexagonalGrid.INNER_RADIUS, grid.distanceToOtherNodes(center), 0.00001);
		
		List<Node> listNodes = grid.getNodesInRadius(1);
		for (Node node : listNodes) {
			assertEquals( 3 * 2 * HexagonalGrid.INNER_RADIUS
							+ 2 * 3 * HexagonalGrid.OUTER_RADIUS
							+ 4 * HexagonalGrid.INNER_RADIUS, grid.distanceToOtherNodes(node), 0.00001);
		}
	}
	
	@Test
	public void testDistancesFullNodesRadiusOne() {
		HexagonalGrid grid = new HexagonalGrid(1);

		List<Integer> indexes = generateRandomIndex(3, 0, 6);
		
		Node node = grid.getNodes().get( indexes.get(0) );
		node.setContent( new Key("1") );
		
		double expectedDistances = 0;
		for (int i = 1; i < indexes.size(); ++i) {
			Node other = grid.getNodes().get( indexes.get(i) );
			other.setContent( new Key("2") );
			
			expectedDistances += node.distance(other);
		}
		
		assertEquals(expectedDistances, grid.distanceToOtherNodes(node), 0.00001);
	}
	
	@Test
	public void cloneTest() {
		HexagonalGrid grid = generateRandomGrid();
		HexagonalGrid clone = (HexagonalGrid) grid.clone();
		
		assertEquals(grid.getRadius(), clone.getRadius());
		assertEquals(grid.getNumberNodes(), clone.getNumberNodes());
		
		Iterator<Node> gridNodes = grid.getNodes().iterator(),
						cloneNodes = clone.getNodes().iterator();
		
		while(gridNodes.hasNext() && cloneNodes.hasNext()) {
			Node node1 = gridNodes.next(),
					node2 = cloneNodes.next();
			assertNotSame(node1, node2);
			assertEquals(0, node1.distance(node2), 0.0000001);
			assertSame(node1.getContent(), node2.getContent());
		}
		
	}

	private HexagonalGrid generateRandomGrid() {
		HexagonalGrid grid = new HexagonalGrid(2);
		List<Node> nodes = grid.getNodesInRadius(2);
		
		List<Integer> indexes = generateRandomIndex(8, 0, 18);
		
		for (int i = 0, n = indexes.size(); i < n; ++i) {
			Key key = new Key( Integer.toString(i) );
			nodes.get(i).setContent(key);
		}
		
		return grid;
	}

	private List<Integer> generateRandomIndex(int length, int floor, int ceiling) {
		Random random = new Random();
		
		Set<Integer> numbers = new HashSet<Integer>();
		for (int i = 0; i < length; ++i) {
			Integer number;
			do {
				number = random.nextInt(ceiling + 1) + floor;
			} while( numbers.contains(number) ) ;
				
			numbers.add(number);
		}
		
		return new ArrayList<Integer>(numbers);		
	}
}
