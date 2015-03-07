/*
 * Copyright (c) 2015
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
*/

package es.csc.proximitykeyboardlayoutbuilder;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class HexagonalProximityBuilder {
	private static class GridDistance {
		HexagonalWeightedGrid grid;
		double distance;
		
		public GridDistance(HexagonalWeightedGrid grid, double distance) {
			this.grid = grid;
			this.distance = distance;
		}
	}
	
	private static KeyFrecuencyGraph weights;
	
	static HexagonalWeightedGrid build(KeyFrecuencyGraph weights) {			
		HexagonalProximityBuilder.weights = weights;
		
		List<Key> keysByWeight = weights.keysSortedByFrecuency();
		
		HexagonalWeightedGrid grid = new HexagonalWeightedGrid(0, weights);
		
		placeMostUsedKey(keysByWeight, grid);		
		
		return placeOtherKeys(grid, keysByWeight);
	}

	private static void placeMostUsedKey(List<Key> keys,
										HexagonalWeightedGrid grid) {
		Key mostUsedKey = keys.get(0);
		Node center = grid.nodesInRadius(0).get(0);
		center.setContent(mostUsedKey );
	}

	private static HexagonalWeightedGrid placeOtherKeys(HexagonalWeightedGrid grid,
														List<Key> keys) {
		int analyzed = 1;				
		while( analyzed < keys.size() ) {						
			grid.expand();
			
			List<Key> keysToPlace = keysToPlace(keys, grid, analyzed);			
			
			grid = minimizeDistance(grid, keysToPlace);
			
			analyzed += keysToPlace.size();
		}
		return grid;
	}

	private static List<Key> keysToPlace(List<Key> keys, 
										HexagonalWeightedGrid grid, 
										int analyzed) {
		
		int remainingKeys = keys.size() - analyzed;
		int keysInRadius = grid.nodesInRadius( grid.radius() ).size();
		int keysToAnalyze = Math.min(keysInRadius, remainingKeys);	
		
		return keys.subList(analyzed, analyzed + keysToAnalyze);
	}

	private static HexagonalWeightedGrid minimizeDistance(HexagonalWeightedGrid grid, 
														List<Key> keys) {
		Map<Key, Double>[] innerDistances = calculateInnerDistances(grid, keys);				
				
		HexagonalWeightedGrid emptyGrid = new HexagonalWeightedGrid(grid.radius(), weights);
		
		placeFirstKeyOuterRadius(keys, emptyGrid);
		
		GridDistance winer = minimieDistance(emptyGrid, innerDistances, keys, 1);
		
		copyOuterNodes(winer.grid, grid);
		
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

	private static void placeFirstKeyOuterRadius(List<Key> keys,
													HexagonalWeightedGrid emptyGrid) {
		List<Node> outerNodes = emptyGrid.nodesInRadius( emptyGrid.radius() );
		outerNodes.get(0).setContent( keys.get(0) );
	}

	private static GridDistance minimieDistance(HexagonalWeightedGrid grid,
														Map<Key, Double>[] innerDistances, 
														List<Key> keys,
														int keyIndex) {		
		if (keyIndex == keys.size()) {
			return minDistanceinRotation((HexagonalWeightedGrid) grid.clone(), innerDistances);
		}
		else {
			return placeNextKey(grid, innerDistances, keys, keyIndex);
		}		
	}

	private static GridDistance minDistanceinRotation(HexagonalWeightedGrid grid, Map<Key, Double>[] innerDistances) {
		double outerDistance = grid.totalDistance();
		
		HexagonalWeightedGrid winer = (HexagonalWeightedGrid) grid.clone();
		double winerDistance = innerDistanceRotation(grid, innerDistances) + outerDistance;
		
		List<Node> nodes = grid.nodesInRadius( grid.radius() );		
		for (int i = 1, n = nodes.size(); i < n; ++i) {			
			ratateContent(nodes);		
			
			double candidateDistance = innerDistanceRotation(grid, innerDistances) + outerDistance;
			if (candidateDistance < winerDistance) {				
				winer = (HexagonalWeightedGrid) grid.clone();
				winerDistance = candidateDistance;
			}
		}
		
		return new GridDistance(winer, winerDistance);
	}

	private static double innerDistanceRotation(HexagonalWeightedGrid grid, Map<Key, Double>[] innerDistances) {
		double distance = 0;
				
		List<Node> outerNodes = grid.nodesInRadius( grid.radius() );
		for(int i = 0, n = outerNodes.size(); i < n; ++i)  {
			Node node = outerNodes.get(i);
			
			if (!node.isEmpty()) {
				distance += innerDistances[i].get( node.getContent() );
			}
		}
		
		return distance;		
	}

	private static void ratateContent(List<Node> nodes) {
		for(int j = 1, n = nodes.size(); j < n; ++j) {
			Key content1 = nodes.get(j-1).getContent(),
					content2 = nodes.get(j).getContent();	
			
			nodes.get(j - 1).setContent(content2);
			nodes.get(j).setContent(content1);
		}
	}
	
	private static GridDistance placeNextKey(
			HexagonalWeightedGrid grid, Map<Key, Double>[] innerDistances, List<Key> keys, int nextKey) {		
		GridDistance winer = null;
		
		List<Node> nodes = grid.nodesInRadius( grid.radius() );
		for(Node node : nodes) {
			if (node.isEmpty()) {
				node.setContent( keys.get(nextKey) );
				
				GridDistance candidate = minimieDistance(grid, innerDistances, keys, nextKey + 1);

				if (winer == null || candidate.distance < winer.distance) {
					winer = candidate;
				}
				
				node.resetContent();
			}
		}
		
		return winer;
	}
	
	private static void copyOuterNodes(HexagonalWeightedGrid origin,
			HexagonalWeightedGrid destiny) {

		Iterator<Node> oIt = origin.nodesInRadius( origin.radius() ).iterator();
		Iterator<Node> dIt = destiny.nodesInRadius( destiny.radius() ).iterator();
		
		while(oIt.hasNext()) {
			dIt.next().setContent( oIt.next().getContent() );
		}
	}
}
