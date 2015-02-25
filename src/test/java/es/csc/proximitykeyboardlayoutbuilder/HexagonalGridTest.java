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

import java.util.HashSet;
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
		
		Node<Integer> center = grid.getNodesInRadius(0).get(0);
		assertEquals(6 * 2 * HexagonalGrid.INNER_RADIUS, grid.distanceToOtherNodes(center, false), 0.00001);
		
		List<Node<Integer>> listNodes = grid.getNodesInRadius(1);
		for (Node<Integer> node : listNodes) {
			assertEquals( 3 * 2 * HexagonalGrid.INNER_RADIUS
							+ 2 * 3 * HexagonalGrid.OUTER_RADIUS
							+ 4 * HexagonalGrid.INNER_RADIUS, grid.distanceToOtherNodes(node, false), 0.00001);
		}
	}
	
	@Test
	public void testDistancesFullNodesRadiusOne() {
		HexagonalGrid grid = new HexagonalGrid(1);

		Integer[] indexes = generateRandomIndex(3, 0, 6);
		
		Node<Integer> node = grid.getNodes().get( indexes[0] );
		node.setContent(1);
		
		double expectedDistances = 0;
		for (int i = 1; i < indexes.length; ++i) {
			Node<Integer> other = grid.getNodes().get( indexes[i] );
			other.setContent(2);
			
			expectedDistances += node.distance(other);
		}
		
		assertEquals(expectedDistances, grid.distanceToOtherNodes(node, true), 0.00001);
	}

	private Integer[] generateRandomIndex(int length, int floor, int ceiling) {
		Random random = new Random();
		
		Set<Integer> result = new HashSet<Integer>();
		for (int i = 0; i < length; ++i) {
			Integer number;
			do {
				number = random.nextInt(ceiling + 1) + floor;
			} while( result.contains( number) ) ;
				
			result.add(number);
		}
		
		return result.toArray( new Integer[0] );
		
	}
}
