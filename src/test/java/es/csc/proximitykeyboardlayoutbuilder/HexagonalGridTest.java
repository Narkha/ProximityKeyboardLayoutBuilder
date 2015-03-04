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

import es.csc.geometry.Point;

public class HexagonalGridTest {

	@Test
	public void numberNodesRadiusZero() {
		HexagonalGrid grid = new HexagonalGrid(0);
		
		assertEquals(0, grid.radius());
		
		assertEquals(1, grid.size());
		assertEquals(1, grid.nodesInRadius(0).size());
	}

	@Test
	public void numberNodesRadiusOne() {
		HexagonalGrid grid = new HexagonalGrid(1);
		
		assertEquals(1, grid.radius());
		
		assertEquals(7, grid.size());
		assertEquals(1, grid.nodesInRadius(0).size());
		assertEquals(6, grid.nodesInRadius(1).size());
	}
	

	@Test
	public void numberNodesRadius2() {
		HexagonalGrid grid = new HexagonalGrid(2);
		
		assertEquals(2, grid.radius());
		
		assertEquals(19, grid.size());
		assertEquals(1, grid.nodesInRadius(0).size());
		assertEquals(6, grid.nodesInRadius(1).size());
		assertEquals(12, grid.nodesInRadius(2).size());
	}	
	
	@Test
	public void distancesAllNodesRadiusOne() {
		HexagonalGrid grid = new HexagonalGrid(1);
		
		fillGraph(grid);
		
		Node center = grid.nodesInRadius(0).get(0);
		assertEquals(6 * 2 * HexagonalGrid.INNER_RADIUS, grid.distanceFrom(center), 0.00001);
		
		List<Node> listNodes = grid.nodesInRadius(1);
		double expectedDistance =  3 * 2 * HexagonalGrid.INNER_RADIUS
									+ 2 * 3 * HexagonalGrid.OUTER_RADIUS
									+ 1 * 4 * HexagonalGrid.INNER_RADIUS;
		for (Node node : listNodes) {
			assertEquals(expectedDistance, grid.distanceFrom(node), 0.00001);
		}
	}
	
	@Test
	public void distancesSomeNodesRadiusOne() {
		HexagonalGrid grid = new HexagonalGrid(1);

		List<Integer> indexes = generateRandomIndex(3, 0, grid.size());
		for (Integer index : indexes){
			grid.nodes().get(index).setContent( new Key( Integer.toString(index) ) );
		}
				
		Node node = grid.nodes().get( indexes.get(0) );
		
		double expectedDistances = 0;
		for (int i = 1; i < indexes.size(); ++i) {
			Node other = grid.nodes().get( indexes.get(i) );		
			expectedDistances += node.distance(other);
		}
		
		assertEquals(expectedDistances, grid.distanceFrom(node), 0.00001);
	}
	
	@Test
	public void totalDistance() {
		HexagonalGrid grid = generateRandomGrid(2);

		double expectedDistance = 0;
		for (Node node : grid.nodes()) {
			expectedDistance += grid.distanceFrom(node);
		}
		expectedDistance /= 2;
		
		assertEquals(expectedDistance, grid.totalDistance(), 0.000001);
	}
		
	@Test
	public void expandGraph() {
		HexagonalGrid grid = new HexagonalGrid(1);
		
		grid.expand();
		assertEquals(2, grid.radius());

		fillGraph(grid);
		
		double expectedDistance;
		
		Node center = grid.nodesInRadius(0).get(0);
		expectedDistance = 	6 * 2 * HexagonalGrid.INNER_RADIUS
				
							+ 6 * 4 * HexagonalGrid.INNER_RADIUS
							
							+ 6 * 3 * HexagonalGrid.OUTER_RADIUS;							
		assertEquals(expectedDistance, grid.distanceFrom(center), 0.00001);
		
		Node nodeRadius1 = grid.nodesInRadius(1).get(0);
		expectedDistance = 	6 * 2 * HexagonalGrid.INNER_RADIUS
				
							+ 3 * 4 * HexagonalGrid.INNER_RADIUS							
							+ 4 * 3 * HexagonalGrid.OUTER_RADIUS
							
							+ 1 * 6 * HexagonalGrid.INNER_RADIUS							
							+ 4 * (new Point(0,0)).distance( new Point(5 * HexagonalGrid.INNER_RADIUS, 1.5 * HexagonalGrid.OUTER_RADIUS));
		assertEquals(expectedDistance, grid.distanceFrom(nodeRadius1), 0.00001);
		
		
		Node nodeRadius2 = grid.nodesInRadius(2).get(0);
		expectedDistance = 	4 * 2 * HexagonalGrid.INNER_RADIUS
				
							+ 2 * 4 * HexagonalGrid.INNER_RADIUS
							+ 3 * 3 * HexagonalGrid.OUTER_RADIUS
							
							+ 2 * 6 * HexagonalGrid.INNER_RADIUS							
							+ 4 * (new Point(0,0)).distance( new Point(HexagonalGrid.INNER_RADIUS, 4.5 * HexagonalGrid.OUTER_RADIUS))
							
							+ 1 * 6 * HexagonalGrid.OUTER_RADIUS
							+ 2 * (new Point(0,0)).distance( new Point(2 * HexagonalGrid.INNER_RADIUS, 6 * HexagonalGrid.OUTER_RADIUS));
		assertEquals(expectedDistance, grid.distanceFrom(nodeRadius2), 0.00001);
	}

	private void fillGraph(HexagonalGrid grid) {
		List<Node> nodes = grid.nodes();
		for(Node node : nodes) {
			node.setContent( new Key("0") );
		}
	}
	
	@Test
	public void testClone() {
		HexagonalGrid grid = generateRandomGrid(2);
		HexagonalGrid clone = (HexagonalGrid) grid.clone();
		
		assertEquals(grid.radius(), clone.radius());
		assertEquals(grid.size(), clone.size());
		
		Iterator<Node> gridNodes = grid.nodes().iterator(),
						cloneNodes = clone.nodes().iterator();
		
		while(gridNodes.hasNext() && cloneNodes.hasNext()) {
			Node node1 = gridNodes.next(),
					node2 = cloneNodes.next();
			assertNotSame(node1, node2);
			assertEquals(0, node1.distance(node2), 0.0000001);
			assertSame(node1.getContent(), node2.getContent());
		}
		
	}

	private HexagonalGrid generateRandomGrid(int radius) {
		HexagonalGrid grid = new HexagonalGrid(radius);
		List<Node> nodes = grid.nodes();
		
		List<Integer> indexes = generateRandomIndex(grid.size() / 2, 0, grid.size());
		
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
				number = random.nextInt(ceiling) + floor;
			} while( numbers.contains(number) ) ;
				
			numbers.add(number);
		}
		
		return new ArrayList<Integer>(numbers);		
	}
}
