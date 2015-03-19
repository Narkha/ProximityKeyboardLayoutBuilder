/*
 * Copyright (c) 2015
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
*/

package es.csc.proximitykeyboardlayoutbuilder.proximitybuilder;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import es.csc.proximitykeyboardlayoutbuilder.HexagonalWeightedGrid;
import es.csc.proximitykeyboardlayoutbuilder.Key;
import es.csc.proximitykeyboardlayoutbuilder.KeyFrecuencyGraph;
import es.csc.proximitykeyboardlayoutbuilder.Node;

public class HexagonalProximityBuilder {	
	static public HexagonalWeightedGrid build(KeyFrecuencyGraph weights) {			
		List<Key> keysByWeight = weights.keysSortedByFrecuency();
		
		HexagonalWeightedGrid grid = new HexagonalWeightedGrid(0, weights);
		
		placeMostUsedKey(grid, keysByWeight);		
		
		return placeOtherKeys(grid, keysByWeight);
	}

	private static void placeMostUsedKey(HexagonalWeightedGrid grid,
										List<Key> keys) {
		Key mostUsedKey = keys.get(0);
		Node center = grid.nodesInRadius(0).get(0);
		center.setContent(mostUsedKey );
	}

	private static HexagonalWeightedGrid placeOtherKeys(HexagonalWeightedGrid grid,
														List<Key> keys) {
		int analyzed = 1;				
		while( analyzed < keys.size() ) {						
			grid.expand();
			
			List<Key> keysToPlace = keysToPlace(grid, keys, analyzed);			
			
			grid = minimizeDistance(grid, keysToPlace);
			
			analyzed += keysToPlace.size();
		}
		return grid;
	}

	private static List<Key> keysToPlace(HexagonalWeightedGrid grid, 
										List<Key> keys, 
										int analyzed) {
		
		int remainingKeys = keys.size() - analyzed;
		int keysInRadius = grid.nodesInRadius( grid.radius() ).size();
		int keysToAnalyze = Math.min(keysInRadius, remainingKeys);	
		
		return keys.subList(analyzed, analyzed + keysToAnalyze);
	}

	private static HexagonalWeightedGrid minimizeDistance(HexagonalWeightedGrid grid, 
														List<Key> keys) {		
		Map<Key, Double>[] innerDistances = calculateInnerDistances(grid, keys);				
				
		HexagonalWeightedGrid outerKeysGrid = new HexagonalWeightedGrid(grid.radius(), 
																	grid.getWeights());
								
		placeFirstKeyOuterRadius(outerKeysGrid, keys);
		
		PairGridDistance winer = null;	
		
		try {
			PlaceOuterKeysTask task = new PlaceOuterKeysTask(outerKeysGrid, innerDistances, 
																keys, 1);
			winer = task.call();				
		} 
		catch (Exception e) {
			return null;
		}	
				
		copyOuterKeys(winer.grid, grid);
		
		return grid;
	}

	@SuppressWarnings("unchecked")
	private static Map<Key, Double>[] calculateInnerDistances(
														HexagonalWeightedGrid grid, 
														List<Key> keys) {
		List<Node> outerNodes = grid.nodesInRadius( grid.radius() ); 
		
		int outerSize = outerNodes.size();
		Map<Key, Double>[] innerDistances = (Map<Key, Double>[]) new HashMap[outerSize];
		
		for (int i = 0; i < outerSize; ++i) {
			Node node = outerNodes.get(i);
			
			innerDistances[i] = new HashMap<Key, Double>();
			for (Key key : keys) {				
				node.setContent(key);
				innerDistances[i].put( key, grid.distanceFrom(node) );
			}
			
			node.resetContent();
		}
		
		return innerDistances;
	}
	
	private static void placeFirstKeyOuterRadius(HexagonalWeightedGrid emptyGrid,
													List<Key> keys) {
		List<Node> outerNodes = emptyGrid.nodesInRadius( emptyGrid.radius() );
		outerNodes.get(0).setContent( keys.get(0) );
	}
	
	private static void copyOuterKeys(HexagonalWeightedGrid origin,
			HexagonalWeightedGrid destiny) {

		Iterator<Node> oIt = origin.nodesInRadius( origin.radius() ).iterator();
		Iterator<Node> dIt = destiny.nodesInRadius( destiny.radius() ).iterator();
		
		while(oIt.hasNext()) {
			dIt.next().setContent( oIt.next().getContent() );
		}
	}
}
