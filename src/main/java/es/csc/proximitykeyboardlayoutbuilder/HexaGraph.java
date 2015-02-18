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

public class HexaGraph {
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

	
	private List<HexaNode> nodes;
	private List<List<HexaNode>> nodesByRadius;
	
	/***
	 *                                              /\
	 * Create a map of vertical regular hexagons ( |  | ) 
	 *                                              \/                                      
	 * 
	 * @param radius: defines the maximum distance in hexagons to any hexagon to the center.
	 *                radius 0 corresponds to only one hexagon 
	 */
	public HexaGraph(int radius) {
		nodes = new ArrayList<HexaNode>();
		nodesByRadius = new ArrayList<List<HexaNode>>();
						
		for (int r = 0; r <= radius; ++r) {
			List<HexaNode> newNodes= createNodesInRadius(r);
			nodes.addAll(newNodes);
			nodesByRadius.add(newNodes);
		}
		
	}

	private List<HexaNode> createNodesInRadius(int radius) {
		List<HexaNode> listNodes = new ArrayList<HexaNode>();
		
		if (radius == 0) {
			HexaNode node = new HexaNode(0, 0);
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
					
					HexaNode node = new HexaNode(x, y);
					listNodes.add(node);
				}
			}
			
		}
		
		return listNodes;
	}
	
	
	public int getRadius() {
		return nodesByRadius.size() - 1;
	}
	
	public int getNumberNodes() {
		return nodes.size();
	}
	
	public List<HexaNode> getNodesInRadius(int r) {
		if (r > getRadius() || r < 0) {
			throw new IndexOutOfBoundsException("Graph of radius " + getRadius());
		}
		
		return nodesByRadius.get(r);
	}
	
	/***
	 * 
	 */
	public double distanceToOtherNodes(HexaNode node, boolean onlyWithContent) {
		double totalDistance = 0;
		for(HexaNode other: nodes) {
			if (!onlyWithContent || other.isEmpty()) { 
				totalDistance += node.distance(other);
			}
		}
		return totalDistance;
	}

	public List<HexaNode> getNodes() {
		return nodes;
	}
}
