/*
 * Copyright (c) 2015
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
*/

package es.csc.proximitykeyboardlayoutbuilder;

import java.util.ArrayList;
import java.util.List;

public class HexagonalGrid implements Cloneable {
	public static final double INNER_RADIUS = 1.0;
	public static final double OUTER_RADIUS = 2 / Math.sqrt(3);
	public static final double NEIGHBOURS_DIAGONAL_Y = Math.sqrt(3);
	
	public static final double DIRECTION_SHIFT[][] = { 
									{2 * INNER_RADIUS, 0},
									{INNER_RADIUS, -NEIGHBOURS_DIAGONAL_Y},
									{-INNER_RADIUS, -NEIGHBOURS_DIAGONAL_Y},
									{-2 * INNER_RADIUS, 0},
									{-INNER_RADIUS, NEIGHBOURS_DIAGONAL_Y},
									{INNER_RADIUS, NEIGHBOURS_DIAGONAL_Y}
								};

	
	private ArrayList<Node> nodes;
	private ArrayList<ArrayList<Node>> nodesByRadius;
	
	/***
	 *                                              /\
	 * Create a map of vertical regular hexagons ( |  | ) 
	 *                                              \/                                      
	 * 
	 * @param radius: defines the maximum distance in hexagons to any hexagon to the center.
	 *                radius 0 corresponds to only one hexagon 
	 */
	public HexagonalGrid(int radius) {
		nodes = new ArrayList<Node>();
		nodesByRadius = new ArrayList<ArrayList<Node>>();
						
		for (int r = 0; r <= radius; ++r) {
			addRadius(r);
		}
		
	}
	private void addRadius(int r) {
		ArrayList<Node> newNodes= createNodesInRadius(r);
		nodes.addAll(newNodes);
		nodesByRadius.add(newNodes);
	}

	private ArrayList<Node> createNodesInRadius(int radius) {
		ArrayList<Node> listNodes = new ArrayList<Node>();
		
		if (radius == 0) {
			Node node = new Node(0, 0);
			listNodes.add(node);			
		}
		else {
			double x = - INNER_RADIUS * radius;
			double y = NEIGHBOURS_DIAGONAL_Y * radius;
			
			for(int i = 0; i < DIRECTION_SHIFT.length; ++i) {
				double xShift = DIRECTION_SHIFT[i][0];
				double yShift = DIRECTION_SHIFT[i][1];
				for(int j = 0; j < radius; ++j) {
					x += xShift;
					y += yShift;
					
					Node node = new Node(x, y);
					listNodes.add(node);
				}
			}
			
		}
		
		return listNodes;
	}
	
	@Override
	public Object clone() {
		try {
			HexagonalGrid clone = (HexagonalGrid) super.clone();
			
			clone.nodes = new ArrayList<Node>();
			clone.nodesByRadius = new ArrayList<ArrayList<Node>>();
			for(List<Node> list: this.nodesByRadius) {
				ArrayList<Node> listClone = deepCopy(list);
				clone.nodes.addAll( listClone );
				clone.nodesByRadius.add( listClone );
			}
			
			return clone;
			
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}
	
	private ArrayList<Node> deepCopy(List<Node> list) {
		ArrayList<Node> copy = new ArrayList<Node>();
		
		for(Node node : list) {
			copy.add( (Node) node.clone() );
		}
		
		return copy;
	}
	
	public void expand() {
		addRadius(radius() + 1);
	}

	public int radius() {
		return nodesByRadius.size() - 1;
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

	protected double distance(Node node, Node other) {
		return node.distance(other);
	}

	@SuppressWarnings("unchecked")
	public List<Node> nodes() {
		return (List<Node>) nodes.clone();
	}
}
