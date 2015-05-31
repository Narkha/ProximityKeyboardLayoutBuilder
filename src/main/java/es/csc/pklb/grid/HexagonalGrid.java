/*
 * Copyright (c) 2015
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
*/

package es.csc.pklb.grid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import es.csc.geometry.Point;
import es.csc.pklb.frecuency.Key;

public class HexagonalGrid implements Iterable<Node>, Cloneable {
	public static final int EDGES = 6;
	
	public static final double INNER_RADIUS = 1.0;
	public static final double OUTER_RADIUS = 2 / Math.sqrt(3);
	public static final double ROW_SHIFT = Math.sqrt(3);
	
	private static final double TAN_30_DEGREES = Math.tan( Math.PI / 6);
	private static final double _60_DEGREES_IN_RADIANS = Math.PI / 3;

	private int rows;
	private int columns;
	// number of nodes
	private int size;
	private List<List<Node>> grid;
	
	private Map<Node, Integer> nodesIndex;
	private double[][] distancesCache;
	
	/***
	 *                                              /\
	 * Create a grid of vertical regular hexagons ( |  | ) 
	 *                                              \/	                
	 *               
	 * Given that the number of elements in the rows can not be equal,
	 * the even colums will contain @column nodes and the odd will contain
	 * @columns - 1 nodes. 
	 * If @colulmns is equals to one all the rows will contain one node.
	 * If @rows is equals to one this row will contains @columns items.
	 * It is equivalent to HexagonalGrid(@rows, @columns, true) 
	 *  
	 * @param rows 
	 * @param columns
	 * @throws IllegalArgumentException if rows or columns are equal or smaller than 0
	 */
	public HexagonalGrid(int rows, int columns) throws IllegalArgumentException {
		this(rows, columns, true);		
	}
	
	/***
	 *                                               /\
	 * Create a grid of vertical regular hexagons ( |  | ) 
	 *                                               \/	                
	 *               
	 * Given that the number of elements in the rows can not be equal,
	 * some rows will contains @columns - 1 nodes. 
	 *  
	 * @param rows 
	 * @param columns
	 * @param shiftEvenRows define with rows will be shifted and contains @columns - 1 nodes
	 * @throws IllegalArgumentException if rows or columns are equal or smaller than 0
	 */
	public HexagonalGrid(int rows, int columns, boolean shiftEvenRows) throws IllegalArgumentException {
		if (rows > 0 && columns > 0) {
			this.rows = rows;
			this.columns = columns;
			
			createNodes(shiftEvenRows);
			createIndexesCache();		
			createDistancesCache();
		}
		else {
			throw new IllegalArgumentException("Invalid dimensions [" + rows + ", " + "]: both values must be grater than 0.");
		}
		
	}
	
	private void createNodes(boolean shiftEvenRows) {
		grid = new ArrayList<List<Node>>(rows);
						
		if (rows == 1) {
			List<Node> row = createRow(columns, 0, 0);
			
			grid.add(row);
		}
		else {
			double x, y = 0;
			for (int i = 0; i < rows; ++i, y += ROW_SHIFT) {				
				boolean isEven = (i % 2 == 0);
				boolean shiftedRow = !(shiftEvenRows ^ isEven);
				
				int rowColumns = (columns == 1) ? columns : (shiftedRow ? columns - 1 : columns);				
				x = shiftedRow ? INNER_RADIUS : 0; 
				
				List<Node> row = createRow(rowColumns, x, y);
				
				grid.add(row);
			}
		}		

		calculateSize();
	}

	private List<Node> createRow(int columns, double x, double y) {
		List<Node> rowList = new ArrayList<Node>(columns);
					
		for(int i = 0; i < columns; ++i, x += 2 * INNER_RADIUS) {
			Node node = new Node(x, y);
			rowList.add(node);
		}
		
		return rowList;
	}
	
	private void calculateSize() {
		size = 0;
		for(List<Node> row : grid) {
			size += row.size();
		}
	}

	private void createIndexesCache() {
		nodesIndex = new HashMap<Node, Integer>();
		
		int index = 0;
		
		for (Node node : this) {
			nodesIndex.put(node, index);
			++index;
		}
	}
	
	private void createDistancesCache() {
		distancesCache = new double[ size() ][ size() ];
		
		int i = 0, j;
		for (Node node1 : this) {
			j = 0;
			
			for (Node node2 : this) {
				if (i == j) {
					distancesCache[i][j] = 0;
				}
				else if (i < j) {
					distancesCache[i][j] = node1.distance(node2);
				}
				else {
					distancesCache[i][j] = distancesCache[j][i];
				}
				
				++j;
			}
			++i;
		}
	}
	
	@Override
	public Object clone() {
		try {
			HexagonalGrid clone = (HexagonalGrid) super.clone();			
			
			cloneNodes(clone);			
			cloneIndexesCache(clone);
			
			return clone;
			
		} 
		catch (CloneNotSupportedException e) {
			return null;
		}
	}
	
	private void cloneNodes(HexagonalGrid clone) {
		clone.grid = new ArrayList<List<Node>>(this.grid.size());
		for(List<Node> list: this.grid) {
			List<Node> listClone = deepCopy(list);
			clone.grid.add( listClone );
		}
	}
	
	private List<Node> deepCopy(List<Node> list) {
		ArrayList<Node> copy = new ArrayList<Node>();
		
		for(Node node : list) {
			copy.add( (Node) node.clone() );
		}
		
		return copy;
	}
	
	private void cloneIndexesCache(HexagonalGrid clone) {
		clone.nodesIndex = new HashMap<Node, Integer>();
		
		int i = 0;
		for(Node node : clone) {
			clone.nodesIndex.put(node, i);
			++i;
		}
	}
	
	/***
	 * 
	 * @return the number of nodes in the grid
	 */
	public int size() {
		return size;
	}
	
	/***
	 * 
	 * @return the number of rows in the grid
	 */
	public int rows() {
		return rows;
	}
	
	/***
	 * 
	 * @return a copy of the internal grid of the grid
	 */
	public List<List<Node>> grid() {
		ArrayList<List<Node>> copy = new ArrayList<List<Node>>( grid.size() );
		for(List<Node> row : grid) {
			copy.add( new ArrayList<Node>(row) );
		}
		
		return copy;
	}
	
	/***
	 * 
	 * @return the number of columns
	 */
	public int columns() {
		return columns;
	}
	
	/***
	 * 
	 * @param row
	 * @param column
	 * @return the Node in indicated position of the grid
	 * @throws IndexOutOfBoundsException if the position (row, column) is outside of the grid
	 */
	public Node get(int row, int column) throws IndexOutOfBoundsException {
		return grid.get(row).get(column);
	}
	
	/***
	 * Return the distance between this node and other non empty nodes
	 */
	public double distanceFrom(Node node) {
		if (node.isEmpty()) {
			return 0;
		}
		else {
			double totalDistance = 0;
			for(Node other: this) {
				if (!other.isEmpty()) { 
					totalDistance += distance(node, other);
				}
			}
			return totalDistance;
		}
	}
	
	/**
	 * 
	 * @return the distance between all the non empty nodes
	 */
	public double totalDistance() {
		double totalDistance = 0;
		for (Node node: this) {
			totalDistance += distanceFrom(node);
		}
		
		return totalDistance / 2;
	}

	protected double distance(Node node1, Node node2) {
		return distancesCache[ nodesIndex.get(node1) ][ nodesIndex.get(node2) ];
	}
	
	public void copyContentFrom(HexagonalGrid origin) throws ArrayIndexOutOfBoundsException {
		if (this.size() != origin.size()) {
			throw new ArrayIndexOutOfBoundsException("The grids have different size");
		}
		
		Iterator<Node> thisIt = this.iterator(),
						otherIt = origin.iterator();
		while(thisIt.hasNext()) {
			Key content = otherIt.next().getContent(); 
			thisIt.next().setContent(content);
		}
	}
	
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder( size() * 3);
		
		for(List<Node> row : grid) {
			if (row.get(0).getX() > 0) {
				result.append(" ");
			}
			
			for(Node node: row) {
				String value = node.isEmpty() ? "#" :  node.getContent().toString();
				result.append(value).append(" ");
			}
			
			result.append("\r\n");
		}
		
		return result.toString();
	}
	
	/***
	 * Returns all the elements that stay at steps of the node.
	 * An step is defined at going from the center of one hexagon to
	 * the center of and adjacent node.
	 * 
	 * @param node
	 * @param steps
	 * @return
	 */
	public List<Node> neighboursAtSteps(Node node, int steps) {
		int maxNeighbours = steps * EDGES;
		
		List<Node> result = new ArrayList<Node>(maxNeighbours);
		if (steps == 0) {
			result.add(node);
		}
		else {					
			for(Node other : this) {
				if (node == other) {
					continue;
				}
				
				int minSteps = calculateMinSteps(node, other);
				if (minSteps == steps) {
					result.add(other);
					
					if (result.size() == maxNeighbours) {
						break;
					}
				}
			}
		}
		
		return result;
	}

	private int calculateMinSteps(Node node1, Node node2) {
		Point vector = node1.vector(node2);
		
		Point orientedVector = new Point(Math.abs(vector.getX()), Math.abs(vector.getY()));
		double angle = orientedVector.angle();
		
		double maxDistanceInRing = 0.0;
		if (angle <= _60_DEGREES_IN_RADIANS) {
			maxDistanceInRing = orientedVector.getX() 
									+ orientedVector.getY() * TAN_30_DEGREES;
		}
		else {
			double module = orientedVector.module(),
					remainingAngle = angle - _60_DEGREES_IN_RADIANS;
			
			maxDistanceInRing = module * (Math.cos(remainingAngle)
									+ Math.sin(remainingAngle) * TAN_30_DEGREES); 
		}
		
		return  (int) Math.round(maxDistanceInRing) / 2;
	}
	public class GridIterator implements Iterator<Node> {

		int nextRow;
		Iterator<Node> column;
		
		public GridIterator() {
			column = getRowIterator(0);
			nextRow = 1;
		}
		
		@Override
		public boolean hasNext() {
			return column.hasNext() || nextRow < HexagonalGrid.this.rows;
		}

		@Override
		public Node next() {
			if (hasNext()) {
				if (!column.hasNext()){
					column = getRowIterator(nextRow);
					++nextRow;
				}

				return column.next();
			}
			else {
				throw new NoSuchElementException();
			}
		}
		
		private Iterator<Node> getRowIterator(int row) {
			return HexagonalGrid.this.grid.get(row).iterator();
		}
		
	}
	
	@Override
	/***
	 * Iterate through the elements of the first row, continues through the elements of the second
	 *  row, etc.
	 */
	public Iterator<Node> iterator() {
		return new GridIterator();
	}
}
