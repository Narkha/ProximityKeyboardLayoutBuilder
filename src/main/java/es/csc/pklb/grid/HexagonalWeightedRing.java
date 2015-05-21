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
 * A Facility for the builder RingProximityBuilder
 *
 */
public class HexagonalWeightedRing extends HexagonalWeightedGrid
									implements Cloneable {
	
	public HexagonalWeightedRing(int radius, KeyFrecuencyGraph weights) {
		this(0, radius, weights);
	}
	
	public HexagonalWeightedRing(int maxRows, int radius, KeyFrecuencyGraph weights) {
		super(maxRows, radius, weights);
	}
}
