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
import java.util.Collections;
import java.util.List;

import es.csc.pklb.frecuency.Key;
import es.csc.pklb.frecuency.KeyFrecuencyGraph;

/***
 * 
 * HexagonalWeightedRing is a facility of HexagonalGrid for RingProximityBuider,
 * with the restraint that the central row must have an odd number of elements.
 * 
 */
public class HexagonalWeightedRing extends HexagonalWeightedGrid
									implements Cloneable {
	
	private int radius;
	private List<List<Node>> nodesByRadius;	
	
	public HexagonalWeightedRing(int rows, int columns, boolean shiftEvenRows, 
									KeyFrecuencyGraph weights) {
		super(rows, columns, shiftEvenRows, weights);
		
		if (rows % 2 == 0) {
			throw new IllegalArgumentException("The number of rows must be odd.");
		}		
		else if (grid().get( rows() / 2 ).size() % 2 == 0) {
			throw new IllegalArgumentException("The middle row must contain an odd amount of nodes.");
		}
		
		int columnsInMiddleRow = columns % 2 == 1 ? columns : columns - 1;		
		radius = columnsInMiddleRow / 2;
		
		createRings();
	}

	private void createRings() {
		class AngleComparator implements java.util.Comparator<Node> {
			Node center;
			public AngleComparator(Node center) {
				super();
				this.center = center;
			}
			
			public int compare(Node node1, Node node2) {
				double diff = center.vector(node1).angle()  - center.vector(node2).angle();
				
				return Math.abs(diff) < 0.000001 ? 0 : (diff > 0 ? 1 : -1);
			}			
		}
		
		nodesByRadius = new ArrayList<List<Node>>(radius + 1);
		
		Node center = getCenter();	
		
		for(int i = 0; i <= radius; ++i) {
			List<Node> nodes = neighboursAtSteps(center, i);
			Collections.sort(nodes, new AngleComparator(center));
			
			nodesByRadius.add(nodes);
		}
	}

	/***
	 * 
	 * @return the central node of the central row
	 */
	private Node getCenter() {
		int row = rows() / 2;
		int column = columns() / 2;
		return get(row, column);
	}

	/***
	 * The radius of an HexagonalWeightedGrid is defined as the number
	 * of steps from the center of the grid to the extreme of the central 
	 * row
	 *  
	 * @return the radius of the grid.
	 */
	public int radius() {
		return radius;
	}
	
	/**
	 * Equivalent to neighboursAtSteps(getCenter(), r) with the nodes
	 * oriented in clockwise, starting from three o'clock
	 *  
	 * @param r
	 * @return
	 */
	public List<Node> nodesInRadius(int r) {
		if (r > radius() || r < 0) {
			throw new IndexOutOfBoundsException("Graph of radius " + radius());
		}
		
		return new ArrayList<Node>( nodesByRadius.get(r) );
	}
	
	@Override
	public Object clone() {
		HexagonalWeightedRing clone = (HexagonalWeightedRing) super.clone();						
		clone.createRings();
		
		return clone;
	}

	/**
	 * Rotate the the content of the grid in clockwise
	 */
	public void rotate() {
		for(int r = 1, R = radius(); r <= R; ++r) {
			rotateRadius(r);			
		}
	}

	/***
	 * rotate the node in radius r in clockwise
	 * 
	 * @param r
	 */
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
