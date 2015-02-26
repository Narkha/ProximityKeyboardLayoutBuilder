/*
 * Copyright (c) 2015
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
*/

package es.csc.proximitykeyboardlayoutbuilder;

/***
 * 
 * HexagonalGrid where the distance between two nodes is the 
 * geometrical distance multiplied by the frecuency between both nodes
 *
 */
public class HexagonalWeightedGrid extends HexagonalGrid 
{

	private KeyFrecuencyGraph weights;

	public HexagonalWeightedGrid(int radius, KeyFrecuencyGraph weights) {
		super(radius);
		
		this.weights = weights;
	}
	
	@Override
	protected double getDistance(Node<Integer> node1, Node<Integer> node2) {
		return node1.distance(node2) 
					* weights.getWeight(node1.getContent(), node2.getContent());
	}

}
