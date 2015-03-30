package es.csc.proximitykeyboardlayoutbuilder.proximitybuilder;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import es.csc.proximitykeyboardlayoutbuilder.HexagonalWeightedGrid;
import es.csc.proximitykeyboardlayoutbuilder.Key;
import es.csc.proximitykeyboardlayoutbuilder.Node;


class RingProximityBuilderTask implements Callable<PairGridDistance> {
	private GridCache cache;
	private HexagonalWeightedGrid originalGrid;
	private Map<Key, Double>[] innerDistances;
	private List<Key> keys;
	private int firstKeyToPlace;
	
	public RingProximityBuilderTask(HexagonalWeightedGrid grid, 
								Map<Key, Double>[] innerDistances, 
								List<Key> keys, 
								int firstKeyToPlace) {		
		cache = new GridCache(grid.radius(), grid.getWeights());
		
		this.originalGrid = grid;
		this.innerDistances = innerDistances;
		this.keys = keys;
		this.firstKeyToPlace = firstKeyToPlace;
	}

	public PairGridDistance call() {
		return minimizeOuterNodesDistance(originalGrid, firstKeyToPlace);
	}
	
	private PairGridDistance minimizeOuterNodesDistance(HexagonalWeightedGrid grid,
														int keyIndex) {		
		if (keyIndex == keys.size()) {
			return minDistanceInRotation(grid);
		}
		else {
			return placeNextKey(grid, keyIndex);
		}		
	}

	private PairGridDistance minDistanceInRotation(HexagonalWeightedGrid grid) {
		double outerDistance = grid.totalDistance();
		
		HexagonalWeightedGrid winner = cache.get();
		winner.copyContent(grid);
		double winnerDistance = innerDistanceRotation(grid) + outerDistance;
		
		List<Node> nodes = grid.nodesInRadius( grid.radius() );		
		for (int i = 1, n = nodes.size(); i < n; ++i) {			
			rotateContent(nodes);		
			
			double candidateDistance = innerDistanceRotation(grid) + outerDistance;
			if (candidateDistance < winnerDistance) {				
				winner.copyContent(grid);
				winnerDistance = candidateDistance;
			}
		}
		
		rotateContent(nodes);
		
		return new PairGridDistance(winner, winnerDistance);
	}

	private double innerDistanceRotation(HexagonalWeightedGrid grid) {
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

	private void rotateContent(List<Node> nodes) {
		for(int j = 1, n = nodes.size(); j < n; ++j) {
			Key content1 = nodes.get(j-1).getContent(),
					content2 = nodes.get(j).getContent();	
			
			nodes.get(j - 1).setContent(content2);
			nodes.get(j).setContent(content1);
		}
	}
	
	private PairGridDistance placeNextKey(HexagonalWeightedGrid grid, int nextKey) {		
		PairGridDistance winner = null;
		
		List<Node> nodes = grid.nodesInRadius( grid.radius() );
		for(Node node : nodes) {
			if (node.isEmpty()) {
				node.setContent( keys.get(nextKey) );
				
				PairGridDistance candidate = minimizeOuterNodesDistance(grid, nextKey + 1);

				if (winner == null) {
					winner = candidate;
				}
				else if (candidate.distance < winner.distance) {
					cache.release(winner.grid);
					winner = candidate;
				}
				else {
					cache.release(candidate.grid);
				}
				
				node.resetContent();
			}
		}
		
		return winner;
	}
}