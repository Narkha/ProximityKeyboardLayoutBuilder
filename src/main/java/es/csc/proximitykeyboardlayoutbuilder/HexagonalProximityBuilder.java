/*
 * Copyright (c) 2015
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
*/

package es.csc.proximitykeyboardlayoutbuilder;

import java.util.List;

public class HexagonalProximityBuilder {
	static HexagonalWeightedGrid build(KeyFrecuencyGraph weights) {				
		HexagonalWeightedGrid grid = new HexagonalWeightedGrid(0, weights);
		
		List<Key> keysByWeight = weights.keysSortedByFrecuency();
		
		placeMostUsedKey(keysByWeight, grid);		
		
		return placeOtherKeys(keysByWeight, grid);
	}

	private static void placeMostUsedKey(List<Key> keysByWeight,
										HexagonalWeightedGrid grid) {
		grid.nodesInRadius(0).get(0).setContent(keysByWeight.get(0));
	}
	

	private static HexagonalWeightedGrid placeOtherKeys(List<Key> keysByWeight,
														HexagonalWeightedGrid grid) {
		int analyzed = 1;				
		while( analyzed < keysByWeight.size() ) {						
			grid.expand();
			
			List<Key> keysToAnalyze = keysToPlace(keysByWeight, grid, analyzed);			
			
			grid = minimizeWeight(grid, keysToAnalyze);
			
			analyzed += keysToAnalyze.size();
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

	

	private static HexagonalWeightedGrid minimizeWeight(HexagonalWeightedGrid grid, 
														List<Key> keys) {
		List<Node> nodes = grid.nodesInRadius( grid.radius() );
		
		nodes.get(0).setContent( keys.get(0) );
		
		return minimizeWeight(grid, keys, 1);
	}

	private static HexagonalWeightedGrid minimizeWeight(HexagonalWeightedGrid grid,
														List<Key> keys,
														int nextKey) {		
		if (nextKey == keys.size()) {
			return minWeightInRotation((HexagonalWeightedGrid) grid.clone());
		}
		else {
			return placeNextKey(grid, keys, nextKey);
		}
		
	}

	private static HexagonalWeightedGrid minWeightInRotation(HexagonalWeightedGrid grid) {
		HexagonalWeightedGrid winer = (HexagonalWeightedGrid) grid.clone();
		
		List<Node> nodes = grid.nodesInRadius( grid.radius() );
		
		for (int i = 1, n = nodes.size(); i < n; ++i) {			
			ratateContent(nodes);		
			
			if (grid.totalDistance() < winer.totalDistance()) {
				winer = (HexagonalWeightedGrid) grid.clone();
			}
		}
		
		return winer;
	}

	private static void ratateContent(List<Node> nodes) {
		for(int j = 1, n = nodes.size(); j < n; ++j) {
			Key content1 = nodes.get(j-1).getContent(),
					content2 = nodes.get(j).getContent();	
			
			nodes.get(j - 1).setContent(content2);
			nodes.get(j).setContent(content1);
		}
	}
	
	private static HexagonalWeightedGrid placeNextKey(
			HexagonalWeightedGrid grid, List<Key> keys, int nextKey) {		
		HexagonalWeightedGrid winer = null;
		
		List<Node> nodes = grid.nodesInRadius( grid.radius() );
		for(Node node : nodes) {
			if (node.isEmpty()) {
				node.setContent( keys.get(nextKey) );
				
				HexagonalWeightedGrid candidate = minimizeWeight(grid, keys, nextKey + 1);

				if (winer == null || candidate.totalDistance() < winer.totalDistance()) {
					winer = candidate;
				}
				
				node.resetContent();
			}
		}
		
		return winer;
	}
}
