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

import es.csc.pklb.frecuency.Key;

public class HexagonalGrid implements Cloneable {
	public static final int EDGES = 6;
	
	public static final double INNER_RADIUS = 1.0;
	public static final double OUTER_RADIUS = 2 / Math.sqrt(3);
	public static final double NEIGHBOURS_DIAGONAL_Y = Math.sqrt(3);
	
	public static final double DIRECTION_SHIFT[][] = { 
									{2 * INNER_RADIUS, 0},
									{INNER_RADIUS, NEIGHBOURS_DIAGONAL_Y},
									{-INNER_RADIUS, NEIGHBOURS_DIAGONAL_Y},
									{-2 * INNER_RADIUS, 0},
									{-INNER_RADIUS, -NEIGHBOURS_DIAGONAL_Y},
									{INNER_RADIUS, -NEIGHBOURS_DIAGONAL_Y}
								};

	private int maxRows;
	private int currenRows;
	private ArrayList<Node> nodes;
	private ArrayList<ArrayList<Node>> nodesByRadius;
	
	private Map<Node, Integer> nodesIndex;
	private double[][] distancesCache;
	
	/***
	 *                                              /\
	 * Create a map of vertical regular hexagons ( |  | ) 
	 *                                              \/    
	 *                                              
	 * This constructor it is equivalent to HexagonalGrid(0, radius).
	 * 
	 * @param radius: defines the maximum distance in hexagons to any hexagon to the center.
	 *                radius 0 corresponds to only one hexagon.
	 */	
	public HexagonalGrid(int radius) {
		this(0, radius);		
	}
	
	/***
	 *                                              /\
	 * Create a map of vertical regular hexagons ( |  | ) 
	 *                                              \/    
	 *                                              
	 * @param maxRows: defines the maximun number of rows that a grid can have. This value is
	 *                 preserved after an expansion.
	 *                 If it´s smaller or equal than 0 there is no limit. 
	 * @param radius: defines the maximum distance in hexagons to any hexagon to the center.
	 *                radius 0 corresponds to only one hexagon 
	 */	
	public HexagonalGrid(int maxRows, int radius) {
		this.maxRows = maxRows;
		
		nodes = new ArrayList<Node>();
		nodesByRadius = new ArrayList<ArrayList<Node>>();
		nodesIndex = new HashMap<Node, Integer>();
						
		for (int r = 0; r <= radius; ++r) {
			addRadius(r);
		}
		
	}
	
	private void addRadius(int r) {
		ArrayList<Node> newNodes= createNodesInRadius(r);
						
		nodes.addAll(newNodes);
		nodesByRadius.add(newNodes);
		
		updateCache();
	}

	private ArrayList<Node> createNodesInRadius(int radius) {
		ArrayList<Node> listNodes = new ArrayList<Node>();
		
		if (radius == 0) {
			currenRows = 1;
			Node node = new Node(0, 0);
			listNodes.add(node);			
		}
		else {
			updateCurrentRows(radius);			
			
			int minInNonLimited = minRowInNonLimitedGrid(radius);
			int maxInNonLimited = maxRowInNonLimitedGrid(radius);
			
			int rowInNonLimited = 0;
			
			double x = - INNER_RADIUS * radius;
			double y = - NEIGHBOURS_DIAGONAL_Y * radius;
			
			for(int i = 0; i < DIRECTION_SHIFT.length; ++i) {
				double xShift = DIRECTION_SHIFT[i][0];
				double yShift = DIRECTION_SHIFT[i][1];
				
				for(int j = 0; j < radius; ++j) {					
					x += xShift;
					y += yShift;
					
					rowInNonLimited += yShift > 0 ? 1 : (yShift < 0 ? -1 : 0);
					
					if ((minInNonLimited <= rowInNonLimited) && (rowInNonLimited <= maxInNonLimited)) {						
						Node node = new Node(x, y);
						listNodes.add(node);
					}
				}
			}
			
		}
		
		return listNodes;
	}

	private void updateCurrentRows(int radius) {
		int rows = rowsInNonLimitedGrid(radius);
		currenRows = (maxRows <= 0 || rows <= maxRows) ? rows : maxRows;
	}

	private int rowsInNonLimitedGrid(int radius) {
		return 2 * radius + 1;
	}

	private int minRowInNonLimitedGrid(int radius) {
		double excess = ((double) rowsInNonLimitedGrid(radius) - currenRows)/2;
		return (int) Math.floor(excess);
	}

	private int maxRowInNonLimitedGrid(int radius) {
		double excess = ((double) rowsInNonLimitedGrid(radius) - currenRows)/2;
		return (int) (rowsInNonLimitedGrid(radius) - 1 - Math.ceil(excess));
	}
	
	private void updateCache() {
		updateIndexesCache();		
		updateDistancesCache();
	}
	
	private void updateIndexesCache() {
		int indexSize = nodesIndex.size();
		int newNodes = nodes.size() - indexSize;
		
		for (int i = 0; i < newNodes; ++i) {
			nodesIndex.put( nodes.get(indexSize + i), indexSize + i );
		}
	}
	
	private void updateDistancesCache() {
		double[][] update = new double[ size() ][ size() ];
		
		for (int i = 0; i < size(); ++i) {
			for (int j = 0; j < size(); ++j) {
				update[i][j] = nodes.get(i).distance( nodes.get(j) );
			}
		}
		
		distancesCache = update;
	}
	
	@Override
	public Object clone() {
		try {
			HexagonalGrid clone = (HexagonalGrid) super.clone();			
			
			cloneNodes(clone);			
			cloneIndexesCache(clone);
			
			return clone;
			
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}
	
	private void cloneNodes(HexagonalGrid clone) {
		clone.nodes = new ArrayList<Node>();
		clone.nodesByRadius = new ArrayList<ArrayList<Node>>();
		for(List<Node> list: this.nodesByRadius) {
			ArrayList<Node> listClone = deepCopy(list);
			clone.nodes.addAll( listClone );
			clone.nodesByRadius.add( listClone );
		}
	}
	
	private ArrayList<Node> deepCopy(List<Node> list) {
		ArrayList<Node> copy = new ArrayList<Node>();
		
		for(Node node : list) {
			copy.add( (Node) node.clone() );
		}
		
		return copy;
	}
	
	private void cloneIndexesCache(HexagonalGrid clone) {
		clone.nodesIndex = new HashMap<Node, Integer>();
		for(int i = 0, n = clone.size(); i < n; ++i) {
			clone.nodesIndex.put( clone.nodes.get(i), i );
		}
	}
	
	public void expand() {
		addRadius(radius() + 1);
	}

	public int radius() {
		return nodesByRadius.size() - 1;
	}
	
	public int maxRows() {
		return maxRows;
	}
	
	public int size() {
		return nodes.size();
	}
	
	@SuppressWarnings("unchecked")
	public List<Node> nodesInRadius(int r) {
		if (r > radius() || r < 0) {
			throw new IndexOutOfBoundsException("Graph of radius " + radius());
		}
		
		return (List<Node>) nodesByRadius.get(r).clone();
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
			for(Node other: nodes) {
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
		Node node1, node2;
		double totalDistance = 0;
		for (int i = 0, n = size(); i < n; ++i) {
			node1 = nodes.get(i);
			if (!node1.isEmpty()) {
				for (int j = i + 1; j < n; ++j) {
					node2 = nodes.get(j);
					if (!node2.isEmpty()) {
						totalDistance += distance( nodes.get(i), nodes.get(j));
					}
				}
			}
		}
		
		return totalDistance;
	}

	protected double distance(Node node1, Node node2) {
		return distancesCache[ nodesIndex.get(node1) ][ nodesIndex.get(node2) ];
	}

	@SuppressWarnings("unchecked")
	public List<Node> nodes() {
		return (List<Node>) nodes.clone();
	}
	
	public void copyContent(HexagonalGrid other) throws ArrayIndexOutOfBoundsException {
		if (this.size() != other.size()) {
			throw new ArrayIndexOutOfBoundsException("The grids have different size");
		}
		
		for (int i = 0, n = this.size(); i < n; ++i) {
			Key content = other.nodes().get(i).getContent(); 
			nodes().get(i).setContent(content);
		}
	}
	
	@Override
	public String toString() {
		List<List<Node>> printGrid = toRows();		
		return toString(printGrid);
	}
	

	public List<List<Node>> toRows() {
		List<List<Node>> result = initializeResult();
		
		for (int radius = 0, R = radius(); radius <= R; ++radius) {
			Iterator<Node> nodeIt = nodesInRadius(radius).iterator();			
			
			if (radius == 0) {
				AddNodesRadiusZero(result, radius, nodeIt);
			}
			else {
				AddNodes(result, nodeIt, radius);
			}
		}
		
		return result;
	}
	
	private List<List<Node>> initializeResult() {		
		List<List<Node>> result = new ArrayList<List<Node>>(currenRows);
		for (int i = 0; i < currenRows; ++i) {
			result.add( new ArrayList<Node>() ); 
		}
		return result;
	}
	
	private void AddNodesRadiusZero(List<List<Node>> result, int radius,
			Iterator<Node> nodeIt) {
		int row = currenRows / 2;
		result.get(row).add( nodeIt.next() );
	}
	

	private void AddNodes(List<List<Node>> result, Iterator<Node> nodeIt, int radius) {
		int minInNonLimited = minRowInNonLimitedGrid( radius() );
		int maxRowInNonLimited = maxRowInNonLimitedGrid( radius() );
		
		int rowInNonLimited = radius() - radius;
		int resultRow = rowInNonLimited - minInNonLimited;
		
		for(int i = 0; i < DIRECTION_SHIFT.length; ++i) {
			double xShift = DIRECTION_SHIFT[i][0];
			double yShift = DIRECTION_SHIFT[i][1];
			
			for(int j = 0; j < radius; ++j) {
				int increment = (yShift > 0) ? 1 : ((yShift < 0)? -1 : 0);
				rowInNonLimited += increment; 
				resultRow += increment;
				
				if ((minInNonLimited <= rowInNonLimited) && (rowInNonLimited <= maxRowInNonLimited)) {		
					if (isAdd2End(xShift, yShift)) {
						result.get(resultRow).add(nodeIt.next());
					}
					else {
						result.get(resultRow).add(0, nodeIt.next());
					}
				}
			}
		}
	}
	
	private boolean isAdd2End(double xShift, double yShift) {
		return yShift > 0 || (yShift == 0 && xShift > 0);
	}
	
	private String toString(List<List<Node>> printGrid) {
		StringBuilder result = new StringBuilder( size() * 3);
		
		for(List<Node> row: printGrid) {
			for(Node node: row) {
				result.append( node.getContent() + " ");
			}	
			result.append("\r\n");
		}
		
		return result.toString();
	}
	
	/**
	 * Rotate the the content of the grid in clockwise
	 */
	public void rotate() {
		for(int r = 1, R = radius(); r <= R; ++r) {
			rotateRadius(r);			
		}
	}

	private void rotateRadius(int r) {
		List<Node> nodes = nodesInRadius(r);
		int size = nodes.size();
		
		for(int j = size - 1; j >= r; --j) {		
			swapContent(nodes, j, j - r);					
		}
	}

	private void swapContent(List<Node> nodes, int index1, int index2) {
		Node node1 = nodes.get(index1);
		Node node2 = nodes.get(index2);
		
		Key tmp = node1.getContent();
		
		node1.setContent( node2.getContent() );
		node2.setContent(tmp);
	}
}
