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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.junit.Test;

import es.csc.geometry.Point;
import es.csc.pklb.frecuency.Key;
import es.csc.pklb.grid.HexagonalGrid;
import es.csc.pklb.grid.Node;

public class HexagonalGridTest {

	@Test(expected=IllegalArgumentException.class)
	public void constructorInvalidDimesions() {
		new HexagonalGrid(-1, 0);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void constructorInvalidRows() {
		new HexagonalGrid(0, 3);
	}
		
	@Test(expected=IllegalArgumentException.class)
	public void constructorInvalidColumns() {
		new HexagonalGrid(3, 0);
	}
	
	@Test
	public void numberNodesDim_1_1() {
		HexagonalGrid grid = new HexagonalGrid(1, 1);
		
		assertEquals(1, grid.rows());
		assertEquals(1, grid.columns());
		
		assertEquals(1, grid.size());
	}
	
	@Test
	public void numberNodesOneRow() {
		HexagonalGrid grid = new HexagonalGrid(1, 10);
		
		assertEquals(1, grid.rows());
		assertEquals(10, grid.columns());
		
		assertEquals(10, grid.size());
	}
	
	@Test
	public void numberNodesOneColumn() {
		HexagonalGrid grid = new HexagonalGrid(10, 1);
		
		assertEquals(10, grid.rows());
		assertEquals(1, grid.columns());
		
		assertEquals(10, grid.size());
	}
	
	
	@Test
	public void numberNodesEvenRows() {
		HexagonalGrid grid = new HexagonalGrid(4, 3);
		
		assertEquals(4, grid.rows());
		assertEquals(3, grid.columns());
		
		assertEquals(10, grid.size());
	}	
	
	@Test
	public void numberNodesOddRows() {
		HexagonalGrid grid = new HexagonalGrid(3, 3);
		
		assertEquals(3, grid.rows());
		assertEquals(3, grid.columns());
		
		assertEquals(7, grid.size());
	}
	
	
	@Test
	public void constructorShiftEvenRows() {
		HexagonalGrid grid = new HexagonalGrid(3, 3, true);
		fillGraph(grid);
		
		assertEquals(3, grid.rows());
		assertEquals(3, grid.columns());
		
		assertEquals(7, grid.size());
		
		Node center = grid.get(1, 1);
		assertEquals(6 * 2 * HexagonalGrid.INNER_RADIUS, grid.distanceFrom(center), 0.0001);
	}
	
	@Test
	public void constructorShiftOdd() {
		HexagonalGrid grid = new HexagonalGrid(3, 3, false);
		fillGraph(grid);
		
		assertEquals(3, grid.rows());
		assertEquals(3, grid.columns());
		
		assertEquals(8, grid.size());
				
		Node node = grid.get(1, 0);
		double expectedDistance = 5 * 2 * HexagonalGrid.INNER_RADIUS
									+ 2 * 3 * HexagonalGrid.OUTER_RADIUS;		
		assertEquals(expectedDistance, grid.distanceFrom(node), 0.0001);
	}
	
	@Test
	public void distancesAllNodesOneRow() {
		HexagonalGrid grid = new HexagonalGrid(1, 5);
		
		fillGraph(grid);
		
		Node first = grid.get(0, 0);		
		assertEquals(10 * 2 * HexagonalGrid.INNER_RADIUS, grid.distanceFrom(first), 0.00001);
		
		Node middle = grid.get(0, grid.columns() / 2);		
		assertEquals(6 * 2 * HexagonalGrid.INNER_RADIUS, grid.distanceFrom(middle), 0.00001);
		
		Node last = grid.get(0, grid.columns() -1 );		
		assertEquals(10 * 2 * HexagonalGrid.INNER_RADIUS, grid.distanceFrom(last), 0.00001);
	}
	
	@Test
	public void distancesAllNodesOneColumn() {
		HexagonalGrid grid = new HexagonalGrid(5, 1);
		
		fillGraph(grid);
		
		Node first = grid.grid().get(0).get(0);	
		double expectedDistanceFirst = 2 * HexagonalGrid.INNER_RADIUS
										+ 3 * HexagonalGrid.OUTER_RADIUS
										+ (new Point(0, 0)).distance( new Point(HexagonalGrid.INNER_RADIUS, 3 * HexagonalGrid.ROW_SHIFT) )										
										+ 6 * HexagonalGrid.OUTER_RADIUS;										
		assertEquals(expectedDistanceFirst, grid.distanceFrom(first), 0.00001);
		
		Node middle = grid.grid().get( grid.rows() / 2).get(0);
		double expectedDistanceMiddle = 2 * 2 * HexagonalGrid.INNER_RADIUS
										+ 2 * 3 * HexagonalGrid.OUTER_RADIUS;
		assertEquals(expectedDistanceMiddle, grid.distanceFrom(middle), 0.00001);
		
		Node last = grid.grid().get( grid.rows() -1 ).get(0);		
		assertEquals(expectedDistanceFirst, grid.distanceFrom(last), 0.00001);
	}
	

	@Test
	public void distancesAllNodes3Rows4Column() {
		HexagonalGrid grid = new HexagonalGrid(3, 4);
		
		fillGraph(grid);
		
		assertDistancesExtremesRows1_3(grid);
		assertDistancesCenterRows1_3(grid);
		assertDistancesExtremesRow2(grid);
		assertDistancesCenterRow2(grid);
	}
	
	private void assertDistancesExtremesRows1_3(HexagonalGrid grid) {
		double expectedDistance = 3 * 2 * HexagonalGrid.INNER_RADIUS
									+ 2 * 3 * HexagonalGrid.OUTER_RADIUS
									+ 2 * 4 * HexagonalGrid.INNER_RADIUS
									+ 2 * (new Point(0,0)).distance( new Point(5 * HexagonalGrid.INNER_RADIUS, HexagonalGrid.ROW_SHIFT));
		
		assertEquals(expectedDistance, grid.distanceFrom( grid.get(0, 0) ), 0.00001);
		assertEquals(expectedDistance, grid.distanceFrom( grid.get(0, 2) ), 0.00001);
		assertEquals(expectedDistance, grid.distanceFrom( grid.get(2, 0) ), 0.00001);
		assertEquals(expectedDistance, grid.distanceFrom( grid.get(2, 2) ), 0.00001);
	}
	
	private void assertDistancesCenterRows1_3(HexagonalGrid grid) {
		double expectedDistance = 4 * 2 * HexagonalGrid.INNER_RADIUS
									+ 3 * 3 * HexagonalGrid.OUTER_RADIUS
									+ 2 * 4 * HexagonalGrid.INNER_RADIUS;
		
		assertEquals(expectedDistance, grid.distanceFrom( grid.get(0, 1) ), 0.00001);
		assertEquals(expectedDistance, grid.distanceFrom( grid.get(2, 1) ), 0.00001);
	}
	
	private void assertDistancesExtremesRow2(HexagonalGrid grid) {
		double expectedDistance = 3 * 2 * HexagonalGrid.INNER_RADIUS
									+ 2 * 3 * HexagonalGrid.OUTER_RADIUS
									+ 4 * HexagonalGrid.INNER_RADIUS
									+ 2 * (new Point(0,0)).distance( new Point(5 * HexagonalGrid.INNER_RADIUS, HexagonalGrid.ROW_SHIFT))
									+ 6 * HexagonalGrid.INNER_RADIUS;
		
		assertEquals(expectedDistance, grid.distanceFrom( grid.get(1, 0) ), 0.00001);
		assertEquals(expectedDistance, grid.distanceFrom( grid.get(1, 3) ), 0.00001);
	}
		
	private void assertDistancesCenterRow2(HexagonalGrid grid) {
		double expectedDistance = 6 * 2 * HexagonalGrid.INNER_RADIUS
									+ 2 * 3 * HexagonalGrid.OUTER_RADIUS
									+ 4 * HexagonalGrid.INNER_RADIUS;
		
		assertEquals(expectedDistance, grid.distanceFrom( grid.get(1, 1) ), 0.00001);
		assertEquals(expectedDistance, grid.distanceFrom( grid.get(1, 2) ), 0.00001);
	}	
	
	@Test
	public void distancesSomeNodes3Rows4Column() {
		int gridRows = 3, gridColumns = 4;
		int row, column;
		HexagonalGrid grid = new HexagonalGrid(gridRows, gridColumns);

		List<Integer> indexes = generateRandomIndex(3, 0, grid.size());
		for (Integer index : indexes) {
			row = rowIteratorIndex(index, gridRows, gridColumns);
			column = columnIteratorIndex(index, gridRows, gridColumns);
			grid.get(row, column).setContent( new Key( Integer.toString(index) ) );
		}
		

		row = rowIteratorIndex(indexes.get(0), gridRows, gridColumns);
		column = columnIteratorIndex(indexes.get(0), gridRows, gridColumns);
		Node node = grid.get(row , column);
		
		double expectedDistances = 0;
		for (Integer index : indexes) {
			row = rowIteratorIndex(index, gridRows, gridColumns);
			column = columnIteratorIndex(index, gridRows, gridColumns);
			
			Node other = grid.get(row , column);
			
			expectedDistances += node.distance(other);
		}
		
		
		assertEquals(expectedDistances, grid.distanceFrom(node), 0.00001);
	}

	@Test
	public void testClone() {
		HexagonalGrid grid = generateRandomGrid(4, 3);
		HexagonalGrid clone = (HexagonalGrid) grid.clone();
		
		assertEquals(grid.rows(), clone.rows());
		assertEquals(grid.columns(), clone.columns());
		assertEquals(grid.size(), clone.size());
		
		Iterator<Node> gridIt = grid.iterator(),
						cloneIt = clone.iterator();
		
		while(gridIt.hasNext() && cloneIt.hasNext()) {
			Node node1 = gridIt.next(),
					node2 = cloneIt.next();
			assertNotSame(node1, node2);
			assertEquals(0, node1.distance(node2), 0.0000001);
			assertSame(node1.getContent(), node2.getContent());
		}
		
		assertFalse(gridIt.hasNext() || cloneIt.hasNext());
		
	}
	
	@Test
	public void copyContent() {
		HexagonalGrid original = generateRandomGrid(4, 3),
						copyOriginal = (HexagonalGrid) original.clone(),
						destiny = new HexagonalGrid(4, 3);
		
		destiny.copyContentFrom(original);
		
		Iterator<Node> originalIt = original.iterator(),
						copyIt = copyOriginal.iterator(),
						destinyIt = destiny.iterator();
		
		while(originalIt.hasNext() && copyIt.hasNext() && destinyIt.hasNext()) {
			Node node1 = originalIt.next(),
					node2 = destinyIt.next();
			assertNotSame(node1, node2);
			assertEquals(copyIt.next().getContent(), node1.getContent());
			assertEquals(node1.getContent(), node2.getContent());
		}
		assertFalse(originalIt.hasNext() || copyIt.hasNext() || destinyIt.hasNext());
	}
	
	@Test
	public void toStringNonEmpyNodes() {
		HexagonalGrid grid = new HexagonalGrid(5, 4);
		
		fillGraph(grid);
		
		String expectedResult =   " 0 1 2 \r\n"
								+ "3 4 5 6 \r\n"
				                + " 7 8 9 \r\n"
								+ "10 11 12 13 \r\n"
				                + " 14 15 16 \r\n";
		
		assertEquals(expectedResult, grid.toString());
	}

	@Test
	public void toStringEmpyNodes() {
		HexagonalGrid grid = new HexagonalGrid(6, 2);
		
		int i = 0;
		for (Node node : grid) {
			if ((i & 1) == 0) {
				node.setContent( new Key( ""+ i) );
			}
			++i;
		}
		
		String expectedResult =   " 0 \r\n"
								+ "# 2 \r\n"
								+  " # \r\n"
								+ "4 # \r\n"
								+ " 6 \r\n"
								+ "# 8 \r\n";
		
		assertEquals(expectedResult, grid.toString());
	}

	public static void fillGraph(HexagonalGrid grid) {
		int i = 0;
		for(Node node : grid) {
			node.setContent( new Key(i++ + "") );
		}
	}

	private HexagonalGrid generateRandomGrid(int rows, int columns) {
		HexagonalGrid grid = new HexagonalGrid(rows, columns);
		
		List<Integer> indexes = generateRandomIndex(grid.size(), 0, grid.size());
		
		for (int index : indexes) {
			Key key = new Key( Integer.toString(index) );
			
			int row = rowIteratorIndex(index, grid.rows(), grid.columns());
			int column = columnIteratorIndex(index, grid.rows(), grid.columns());
			
			grid.get(row, column).setContent(key);
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
	
	private int rowIteratorIndex(int index, int rows, int columns) {
		int quotient = index / (2 * columns - 1);
		int substract = index % (2 * columns - 1);
		
		return 2 * quotient + ((substract < columns - 1) ? 0 : 1);
	}

	private int columnIteratorIndex(int index, int rows, int columns) {
		int substract = index % (2 * columns - 1);
		
		return (substract < columns - 1) ? substract : substract - (columns - 1);
	}
	
	@Test
	public void neighbourAt_1_Steps() {		
		HexagonalGrid grid = new HexagonalGrid(3, 4);
		
		// Node and the expected number of neigbours
		Map<Node, Integer> testValues = new HashMap<Node, Integer>();
			testValues.put(grid.get(0, 0), 3);
			testValues.put(grid.get(0, 1), 4);
			testValues.put(grid.get(0, 2), 3);
			testValues.put(grid.get(1, 0), 3);
			testValues.put(grid.get(1, 1), 6);		
		
		for(Node node : testValues.keySet()) {
			int exptectedNeighbours = testValues.get(node);
			
			String errorMessage = node + ": neighbours " + exptectedNeighbours;
			
			List<Node> neighbours = grid.neighboursAtSteps(node, 1);
			
			assertEquals(errorMessage, exptectedNeighbours, neighbours.size());
			
			// all the nodes should be different
			for(int i = 0, n = neighbours.size(); i < n; ++i) {
				for(int j = i + 1; j < n; ++j) {
					assertNotEquals(errorMessage, neighbours.get(i), neighbours.get(j));				
				}	
			}
			
			// and be at the same distance from the node
			for(Node other : neighbours) {
				assertEquals(errorMessage, 2 * HexagonalGrid.INNER_RADIUS, node.distance(other), 0.0001);
			}	
		}
	}
	
	@Test
	public void neighbourAt_2_Steps() {		
		HexagonalGrid grid = new HexagonalGrid(5, 6);
		
		// Node and the expected number of neigbours at distance 4 and number of nodes to distance xxxx
		Map<Node, int[]> testValues = new HashMap<Node, int[]>();
			testValues.put(grid.get(0, 0), new int[]{2, 2});
			testValues.put(grid.get(0, 2), new int[]{4, 3});
			testValues.put(grid.get(1, 2), new int[]{4, 5});
			testValues.put(grid.get(2, 2), new int[]{6, 6});		
		
		for(Node node : testValues.keySet()) {
			int[] exptectedNeighbours = testValues.get(node);			
			
			String errorMessage = node + ": neighbours " + exptectedNeighbours;
			
			List<Node> neighbours = grid.neighboursAtSteps(node, 2);
			
			assertEquals(errorMessage, exptectedNeighbours[0] + exptectedNeighbours[1], neighbours.size());
			
			// all the nodes should be different
			for(int i = 0, n = neighbours.size(); i < n; ++i) {
				for(int j = i + 1; j < n; ++j) {
					assertNotEquals(errorMessage, neighbours.get(i), neighbours.get(j));				
				}	
			}
			
			int[] existingNeigbours = new int[] {0, 0};
			// and be at the same distance from the node
			for(Node other : neighbours) {
				if ( Math.abs(node.distance(other) - 4 * HexagonalGrid.INNER_RADIUS) < 0.0001) {
					++existingNeigbours[0];
				}
				else if (Math.abs(node.distance(other) - 3 * HexagonalGrid.OUTER_RADIUS) < 0.0001) {
					++existingNeigbours[1];
				}
				else {
					assertTrue(node.distance(other) + " en " + errorMessage, false);
				}				
			}	
			
			assertArrayEquals(exptectedNeighbours, existingNeigbours);
		}
	}
	

	
	@Test
	public void neighbourAt_8_Steps() {		
		HexagonalGrid grid = new HexagonalGrid(9, 10);
		
		Node node = grid.get(8, 0);
		
		int exptectedNeighbours = 13;
		
		List<Node> neighbours = grid.neighboursAtSteps(node, 8);
		
		assertEquals(exptectedNeighbours, neighbours.size());
		
		// all the nodes should be different
		for(int i = 0, n = neighbours.size(); i < n; ++i) {
			assertNotEquals(node, neighbours.get(i));
			
			for(int j = i + 1; j < n; ++j) {
				assertNotEquals(neighbours.get(i), neighbours.get(j));				
			}	
		}
		
		assertTrue( neighbours.contains( grid.get(4, 6)) );
	}

	
	@Test
	public void iterator() {
		HexagonalGrid grid = new HexagonalGrid(5, 5);
				
		int i = 0;
		for(Node node : grid) {
			node.setContent( new Key( Integer.toString(i) ) );
			++i;
		}
		
		i = 0;
		Iterator<Node> gridIt = grid.iterator();
		
		List<List<Node>> rows = grid.grid();		
		for (List<Node> row : rows) {
			for(Node node : row) {
			
				assertEquals(Integer.toString(i), node.getContent().toString());
				assertEquals(node, gridIt.next());
				++i;
			}
		}
		
	}
}
