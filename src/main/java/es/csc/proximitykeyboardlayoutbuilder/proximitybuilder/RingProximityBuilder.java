/*
 * Copyright (c) 2015
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
*/

package es.csc.proximitykeyboardlayoutbuilder.proximitybuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import es.csc.proximitykeyboardlayoutbuilder.HexagonalWeightedGrid;
import es.csc.proximitykeyboardlayoutbuilder.Key;
import es.csc.proximitykeyboardlayoutbuilder.KeyFrecuencyGraph;
import es.csc.proximitykeyboardlayoutbuilder.Node;

public class RingProximityBuilder {	
	private KeyFrecuencyGraph weights;
	
	public RingProximityBuilder(KeyFrecuencyGraph weights) {			
		this.weights = weights;
	}
	
	public HexagonalWeightedGrid build() throws InterruptedException {			
		List<Key> keysByWeight = weights.keysSortedByFrecuency();
		
		HexagonalWeightedGrid grid = new HexagonalWeightedGrid(0, weights);
		
		placeMostUsedKey(grid, keysByWeight);		
		
		return placeOtherKeys(grid, keysByWeight);
	}

	private void placeMostUsedKey(HexagonalWeightedGrid grid,
										List<Key> keys) {
		Key mostUsedKey = keys.get(0);
		Node center = grid.nodesInRadius(0).get(0);
		center.setContent(mostUsedKey);
	}

	private HexagonalWeightedGrid placeOtherKeys(HexagonalWeightedGrid grid,
														List<Key> keys) throws InterruptedException {
		int analyzed = 1;				
		while( analyzed < keys.size() ) {						
			grid.expand();
			
			List<Key> keysToPlace = keysToPlace(grid, keys, analyzed);			
			
			grid = minimizeDistance(grid, keysToPlace);
			
			analyzed += keysToPlace.size();
		}
		return grid;
	}

	private List<Key> keysToPlace(HexagonalWeightedGrid grid, 
										List<Key> keys, 
										int analyzed) {
		
		int remainingKeys = keys.size() - analyzed;
		int keysInRadius = grid.nodesInRadius( grid.radius() ).size();
		int keysToAnalyze = Math.min(keysInRadius, remainingKeys);	
		
		return keys.subList(analyzed, analyzed + keysToAnalyze);
	}

	private HexagonalWeightedGrid minimizeDistance(HexagonalWeightedGrid grid, 
														List<Key> keys) throws InterruptedException {		
		Map<Key, Double>[] innerDistances = calculateInnerDistances(grid, keys);				
				
		HexagonalWeightedGrid outerKeysGrid = new HexagonalWeightedGrid(grid.radius(), 
																	grid.getWeights());
								
		placeFirstKeyOuterRadius(outerKeysGrid, keys);
		
		PairGridDistance winner = null;	

		if (keys.size() == 1) {
			RingProximityBuilderTask task = new RingProximityBuilderTask(outerKeysGrid, innerDistances, 
																	keys, 1);
			winner = task.call();
		}
		else {
			winner = parallelizePlaceKeysTasks(outerKeysGrid, innerDistances, keys);
		}
	
		copyOuterKeys(winner.grid, grid);
		
		return grid;
	}

	@SuppressWarnings("unchecked")
	private Map<Key, Double>[] calculateInnerDistances(
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
	
	private void placeFirstKeyOuterRadius(HexagonalWeightedGrid emptyGrid,
													List<Key> keys) {
		List<Node> outerNodes = emptyGrid.nodesInRadius( emptyGrid.radius() );
		outerNodes.get(0).setContent( keys.get(0) );
	}
	

	private PairGridDistance parallelizePlaceKeysTasks(
											HexagonalWeightedGrid grid,
											Map<Key, Double>[] innerDistances, 
											List<Key> keys) throws InterruptedException {	
		
		
		
		ExecutorService executor = Executors.newFixedThreadPool( Runtime.getRuntime().availableProcessors() );
				
		List<Future<PairGridDistance>> results = submitTasks(executor, grid,
																innerDistances, keys);
		
		executor.shutdown();
		executor.awaitTermination(10, TimeUnit.DAYS);				
		
		return findMin(results);
		
	}

	private List<Future<PairGridDistance>> submitTasks(
			ExecutorService executor, HexagonalWeightedGrid grid,
			Map<Key, Double>[] innerDistances, List<Key> keys) {
		List<Node> nodes = grid.nodesInRadius( grid.radius() );
		List< Future<PairGridDistance> > results = new ArrayList< Future<PairGridDistance> >();
		
		for (int i = 1, n = nodes.size(); i < n; ++i) {
			Node node = nodes.get(i);
			
			node.setContent( keys.get(1) );
			
			RingProximityBuilderTask task = new RingProximityBuilderTask((HexagonalWeightedGrid) grid.clone(), 
																innerDistances, keys, 2);
			Future<PairGridDistance> future = executor.submit(task);
			results.add(future);
			
			node.resetContent();
		}
		return results;
	}

	private PairGridDistance findMin(List<Future<PairGridDistance>> results) {
		class FutureComparator implements java.util.Comparator< Future<PairGridDistance >> {

			public int compare(Future<PairGridDistance> future1,
								Future<PairGridDistance> future2) {
				try {
					return (int) (10 * (future1.get().distance - future2.get().distance));
				} 
				catch (InterruptedException e) {
					return 0;
				} 
				catch (ExecutionException e) {
					return 0;
				}
			}
			
		}
		
		int index = results.indexOf( Collections.min(results, new FutureComparator()) );
		
		try {
			return results.get(index).get();
		} 
		catch (InterruptedException e) {
			return null;
		} 
		catch (ExecutionException e) {
			return null;
		}
	}
	
	private void copyOuterKeys(HexagonalWeightedGrid origin,
			HexagonalWeightedGrid destiny) {

		Iterator<Node> oIt = origin.nodesInRadius( origin.radius() ).iterator();
		Iterator<Node> dIt = destiny.nodesInRadius( destiny.radius() ).iterator();
		
		while(oIt.hasNext()) {
			dIt.next().setContent( oIt.next().getContent() );
		}
	}
}
