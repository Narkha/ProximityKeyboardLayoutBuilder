/*
 * Copyright (c) 2015
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
*/

package es.csc.pklb.grid;

import es.csc.pklb.frecuency.KeyFrecuencyGraph;

/***
 * 
 * HexagonalGrid where the distance between two nodes is the 
 * geometrical distance multiplied by the frecuency between both nodes
 *
 */
public class HexagonalWeightedGrid extends HexagonalGrid
									implements Cloneable {
	private KeyFrecuencyGraph weights;

	public HexagonalWeightedGrid(int radius, KeyFrecuencyGraph weights) {
		this(0, radius, weights);
	}
	
	public HexagonalWeightedGrid(int maxRows, int radius, KeyFrecuencyGraph weights) {
		super(radius);
		
		this.weights = weights;
	}
	
	@Override
	protected double distance(Node node1, Node node2) {
		return node1.distance(node2) 
					* weights.getFrecuency(node1.getContent(), node2.getContent());
	}

	public KeyFrecuencyGraph getWeights() {
		return weights;
	}
}
