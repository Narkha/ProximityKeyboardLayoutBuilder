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

	
	private List<Node> nodes;
	private List<List<Node>> nodesByRadius;
	
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
		nodesByRadius = new ArrayList<List<Node>>();
						
		for (int r = 0; r <= radius; ++r) {
			List<Node> newNodes= createNodesInRadius(r);
			nodes.addAll(newNodes);
			nodesByRadius.add(newNodes);
		}
		
	}

	private List<Node> createNodesInRadius(int radius) {
		List<Node> listNodes = new ArrayList<Node>();
		
		if (radius == 0) {
			Node node = new Node(0, 0);
			listNodes.add(node);			
		}
		else {
			Double x = - INNER_RADIUS * radius;
			Double y = NEIGHBOURS_DIAGONAL_Y * radius;
			
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
			clone.nodesByRadius = new ArrayList<List<Node>>();
			for(List<Node> list: this.nodesByRadius) {
				List<Node> listClone = deepCopy(list);
				clone.nodes.addAll( listClone );
				clone.nodesByRadius.add( listClone );
			}
			
			return clone;
			
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}
	
	private List<Node> deepCopy(List<Node> list) {
		List<Node> copy = new ArrayList<Node>();
		
		for(Node node : list) {
			copy.add( (Node) node.clone() );
		}
		
		return copy;
	}

	public int getRadius() {
		return nodesByRadius.size() - 1;
	}
	
	public int getNumberNodes() {
		return nodes.size();
	}
	
	public List<Node> getNodesInRadius(int r) {
		if (r > getRadius() || r < 0) {
			throw new IndexOutOfBoundsException("Graph of radius " + getRadius());
		}
		
		return nodesByRadius.get(r);
	}
	
	/***
	 * Return the distance between this node and other non empty nodes
	 */
	public double distanceToOtherNodes(Node node) {
		if (node.isEmpty()) {
			return 0;
		}
		else {
			double totalDistance = 0;
			for(Node other: nodes) {
				if (!other.isEmpty()) { 
					totalDistance += getDistance(node, other);
				}
			}
			return totalDistance;
		}
	}

	protected double getDistance(Node node, Node other) {
		return node.distance(other);
	}

	public List<Node> getNodes() {
		return nodes;
	}
}
