/*
 * Copyright (c) 2015
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
*/

package es.csc.pklb.buider;

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

import es.csc.pklb.frecuency.Key;
import es.csc.pklb.frecuency.KeyFrecuencyGraph;
import es.csc.pklb.grid.HexagonalWeightedRing;
import es.csc.pklb.grid.Node;

/***
 * Given that minimize the distance between N keys is a NP problem of order N!, the algorithm
 * implement in this class makes the assumption that the most used the most used keys should 
 * be placed closer to the center of the grid. 
 *     1) Create a HexagonalRingWeightedGrid of radius 0 and place the most used key
 *     2) Expand the grid and find the next most frequent keys
 *     3) repeat 2 until all the keys are placed.  
 * 
 * Given this, if the configuration of the algorithm (the number of keys to place and maximum 
 * of rows) implies the creation of a cut grid (a grid with radius 3 and 3 rows), 
 * it will no work because the best distribution of the keys would probably be different 
 * if the inside keys were rotated).
 * The exception to this problem is when it is possible rotate the inner rings:
 *    if there are 29 keys and 3 rows possible it will be possible rotate the inner keys
 *    when the last 10 keys are being placed.
 *    
 *    if there are 39 keys and 3 rows possible it wont be possible rotate the inner keys
 *    when the last 10 keys are being placed.
 */
public class RingProximityBuilder {
	private boolean shiftEvenRows;
	private int rows;
	private int columns;
	private KeyFrecuencyGraph weights;
	
	/**
	 * 
	 * @throws UnsupportedOperationException
	 * 			If this configuration (weights.size() keys in N nodes) is not supported by
	 *          the algorithm
	 */
	public RingProximityBuilder(int maxRows, KeyFrecuencyGraph weights) throws UnsupportedOperationException {
		int idealRadius = idealRadius( weights.size() );
		int radius = (maxRows == 0) ?  idealRadius : cuttedRadius(maxRows, weights.size());
		int notCuttedRadius = (maxRows == 0) ? radius : maxRows / 2; 

		if (maxRows != 1 && notCuttedRadius != radius && notCuttedRadius + 1 < radius) {
			throw new UnsupportedOperationException("This configuration is not allowed.");
		}
		
		this.rows = notCuttedRadius == radius ? radius * 2 + 1 : maxRows;
		this.columns = radius * 2 + 1;		
		this.shiftEvenRows = (this.rows / 2) % 2 == 1;
		
		this.weights = weights;
	}

	private int cuttedRadius(int maxRows, int size) {
		int notCuttedRadius = maxRows / 2 ;
		int sizeNotCutted = 6 * notCuttedRadius * (notCuttedRadius + 1) / 2 + 1;
		
		int cuttedRadius;
		if (sizeNotCutted >= size) {
			cuttedRadius = notCuttedRadius;
		}
		else {
			cuttedRadius = notCuttedRadius 
						+ (int) Math.ceil( ( (double) size - sizeNotCutted) / (2 * maxRows) );
		}
		
		return cuttedRadius;
	}

	private int idealRadius(int size) {
		int radius = 0;
		
		--size;
		while(size > 0) {
			++radius;
			size -= radius * HexagonalWeightedRing.EDGES;
		}
		
		return radius;
	}

	public HexagonalWeightedRing build() throws InterruptedException {			
		List<Key> keysByWeight = weights.keysSortedByFrecuency();
		
		HexagonalWeightedRing grid = new HexagonalWeightedRing(rows, columns, shiftEvenRows, weights);
		
		placeMostUsedKey(grid, keysByWeight);		
		
		return placeOtherKeys(grid, keysByWeight);
	}

	private void placeMostUsedKey(HexagonalWeightedRing grid, List<Key> keys) {
		Key mostUsedKey = keys.get(0);
		Node center = grid.nodesInRadius(0).get(0);
		center.setContent(mostUsedKey);
	}

	private HexagonalWeightedRing placeOtherKeys(HexagonalWeightedRing grid, List<Key> keys) 
													throws InterruptedException {
		int radius = 0, analyzed = 1;				
		while( analyzed < keys.size() ) {	
			++radius;
			List<Key> keysToPlace = keysToPlace(grid, radius, keys, analyzed);
			
			if (rows <= (radius * 2 + 1)) {			
				grid = minimizeDistance(grid, radius, keysToPlace);
			}
			else {
				grid = minimizeDistanceCutGrid(grid, radius, keysToPlace);
			}
			
			analyzed += keysToPlace.size();
		}
		return grid;
	}

	private List<Key> keysToPlace(HexagonalWeightedRing grid, int radius, 
									List<Key> keys, int analyzed) {
		
		int remainingKeys = keys.size() - analyzed;
		int keysInRadius = grid.nodesInRadius(radius).size();
		int keysToAnalyze = Math.min(keysInRadius, remainingKeys);	
		
		return keys.subList(analyzed, analyzed + keysToAnalyze);
	}

	private HexagonalWeightedRing minimizeDistanceCutGrid(HexagonalWeightedRing grid, int radius, List<Key> keys) throws InterruptedException {
		grid = (HexagonalWeightedRing) grid.clone();
		
		List<PairGridDistance> results = new ArrayList<PairGridDistance>();
		for (int i = 0; i < 3; ++i) {			
			HexagonalWeightedRing result = minimizeDistance(grid, radius, keys);
			results.add( new PairGridDistance(result, result.totalDistance()) );
			grid.rotate();
		}
		
		PairGridDistance min = findMin(results);
		
		return min.grid;
	}

	private HexagonalWeightedRing minimizeDistance(HexagonalWeightedRing grid, int radius,
													List<Key> keys) 
													throws InterruptedException {				
		grid = (HexagonalWeightedRing) grid.clone();
		
		Map<Key, Double>[] innerDistances = calculateInnerDistances(grid, radius, keys);				
				
		HexagonalWeightedRing outerKeysGrid = new HexagonalWeightedRing(rows, columns, shiftEvenRows, 
																	grid.getWeights());
								
		placeFirstKeyOuterRadius(outerKeysGrid, radius, keys);
		
		PairGridDistance winner = null;	

		if (keys.size() == 1) {
			winner = (new RingProximityBuilderTask(outerKeysGrid, radius, innerDistances, keys, 1)).call();
		}
		else {
			winner = parallelizePlaceKeysTasks(outerKeysGrid, radius, innerDistances, keys);
		}
	
		copyOuterKeys(winner.grid, grid, radius);
		
		return grid;
	}
	
	@SuppressWarnings("unchecked")
	private Map<Key, Double>[] calculateInnerDistances(HexagonalWeightedRing grid, int radius,
														List<Key> keys) {
		List<Node> outerNodes = grid.nodesInRadius(radius); 
		
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
	
	private void placeFirstKeyOuterRadius(HexagonalWeightedRing emptyGrid, int radius,
													List<Key> keys) {
		List<Node> outerNodes = emptyGrid.nodesInRadius(radius);
		outerNodes.get(0).setContent( keys.get(0) );
	}
	

	private PairGridDistance parallelizePlaceKeysTasks(HexagonalWeightedRing grid, int radius,
														Map<Key, Double>[] innerDistances, 
														List<Key> keys) 
														throws InterruptedException {	
		
		ExecutorService executor = Executors.newFixedThreadPool( Runtime.getRuntime().availableProcessors() );
				
		List<Future<PairGridDistance>> results = submitTasks(executor, grid, radius,
																innerDistances, keys);
		
		executor.shutdown();
		executor.awaitTermination(10, TimeUnit.DAYS);				
		
		return findMinFutures(results);
		
	}

	private List<Future<PairGridDistance>> submitTasks(ExecutorService executor, 
														HexagonalWeightedRing grid, int radius,
														Map<Key, Double>[] innerDistances, 
														List<Key> keys) {
		List<Node> nodes = grid.nodesInRadius(radius);
		List< Future<PairGridDistance> > results = new ArrayList< Future<PairGridDistance> >();
		
		for (int i = 1, n = nodes.size(); i < n; ++i) {
			Node node = nodes.get(i);
			
			node.setContent( keys.get(1) );
			
			RingProximityBuilderTask task = new RingProximityBuilderTask(
													(HexagonalWeightedRing) grid.clone(), radius, 
													innerDistances, keys, 2);
			Future<PairGridDistance> future = executor.submit(task);
			results.add(future);
			
			node.resetContent();
		}
		return results;
	}

	private PairGridDistance findMinFutures(List<Future<PairGridDistance>> futures) {
		
		List<PairGridDistance> pairs = new ArrayList<PairGridDistance>( futures.size());
		try {
			for(Future<PairGridDistance> future : futures) {
				pairs.add( future.get() );
			}
		} 
		catch (InterruptedException e) {
			return null;
		} 
		catch (ExecutionException e) {
			return null;
		}
				
		return findMin(pairs);
	}

	private PairGridDistance findMin(List<PairGridDistance> data) {
		return Collections.min(data, new PairGridDistance(null, 0));
	}
	
	private void copyOuterKeys(HexagonalWeightedRing origin,
								HexagonalWeightedRing destiny, 
								int radius) {

		Iterator<Node> oIt = origin.nodesInRadius(radius).iterator();
		Iterator<Node> dIt = destiny.nodesInRadius(radius).iterator();
		
		while(oIt.hasNext()) {
			dIt.next().setContent( oIt.next().getContent() );
		}
	}
}
