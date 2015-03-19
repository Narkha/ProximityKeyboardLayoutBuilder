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
	private static KeyFrecuencyGraph weights;

	static private GridCache cache;
	
	static public HexagonalWeightedGrid build(KeyFrecuencyGraph weights) {			
		HexagonalProximityBuilder.weights = weights;
		
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
		cache = new GridCache(grid.radius(), weights);
		Map<Key, Double>[] innerDistances = calculateInnerDistances(grid, keys);				
				
		HexagonalWeightedGrid emptyGrid = new HexagonalWeightedGrid(grid.radius(), weights);
		
		placeFirstKeyOuterRadius(emptyGrid, keys);
		
		PairGridDistance winer = minimizeDistance(emptyGrid, innerDistances, keys, 1);
		
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

	private static void placeFirstKeyOuterRadius(HexagonalWeightedGrid emptyGrid,
													List<Key> keys) {
		List<Node> outerNodes = emptyGrid.nodesInRadius( emptyGrid.radius() );
		outerNodes.get(0).setContent( keys.get(0) );
	}

	private static PairGridDistance minimizeDistance(HexagonalWeightedGrid grid,
														Map<Key, Double>[] innerDistances, 
														List<Key> keys,
														int keyIndex) {		
		if (keyIndex == keys.size()) {
			return minDistanceInRotation(grid, innerDistances);
		}
		else {
			return placeNextKey(grid, innerDistances, keys, keyIndex);
		}		
	}

	private static PairGridDistance minDistanceInRotation(HexagonalWeightedGrid grid, 
														Map<Key, Double>[] innerDistances) {
		double outerDistance = grid.totalDistance();
		
		HexagonalWeightedGrid winer = cache.get();
		winer.copyContent(grid);
		double winerDistance = innerDistanceRotation(grid, innerDistances) + outerDistance;
		
		List<Node> nodes = grid.nodesInRadius( grid.radius() );		
		for (int i = 1, n = nodes.size(); i < n; ++i) {			
			rotateContent(nodes);		
			
			double candidateDistance = innerDistanceRotation(grid, innerDistances) + outerDistance;
			if (candidateDistance < winerDistance) {				
				winer.copyContent(grid);
				winerDistance = candidateDistance;
			}
		}
		
		rotateContent(nodes);
		
		return new PairGridDistance(winer, winerDistance);
	}

	private static double innerDistanceRotation(HexagonalWeightedGrid grid, 
													Map<Key, Double>[] innerDistances) {
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

	private static void rotateContent(List<Node> nodes) {
		for(int j = 1, n = nodes.size(); j < n; ++j) {
			Key content1 = nodes.get(j-1).getContent(),
					content2 = nodes.get(j).getContent();	
			
			nodes.get(j - 1).setContent(content2);
			nodes.get(j).setContent(content1);
		}
	}
	
	private static PairGridDistance placeNextKey(HexagonalWeightedGrid grid, 
												Map<Key, Double>[] innerDistances, 
												List<Key> keys, int nextKey) {		
		PairGridDistance winer = null;
		
		List<Node> nodes = grid.nodesInRadius( grid.radius() );
		for(Node node : nodes) {
			if (node.isEmpty()) {
				node.setContent( keys.get(nextKey) );
				
				PairGridDistance candidate = minimizeDistance(grid, innerDistances, keys, nextKey + 1);

				if (winer == null) {
					winer = candidate;
				}
				else if (candidate.distance < winer.distance) {
					cache.release(winer.grid);
					winer = candidate;
				}
				else {
					cache.release(candidate.grid);
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
