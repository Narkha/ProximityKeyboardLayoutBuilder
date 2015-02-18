/*
 * Copyright (c) 2015
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
*/

package es.csc.proximitykeyboardlayoutbuilder;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.junit.Test;

public class HexaGraphTest {

	@Test
	public void testNumberNodesRadiusZero() {
		HexaGraph graph = new HexaGraph(0);
		assertEquals(1, graph.getNumberNodes());
		assertEquals(1, graph.getNodesInRadius(0).size());
	}

	@Test
	public void testNumberNodesRadiusOne() {
		HexaGraph graph = new HexaGraph(1);
		assertEquals(7, graph.getNumberNodes());
		assertEquals(1, graph.getNodesInRadius(0).size());
		assertEquals(6, graph.getNodesInRadius(1).size());
	}
	

	@Test
	public void testNumberNodesRadius2() {
		HexaGraph graph = new HexaGraph(2);
		assertEquals(19, graph.getNumberNodes());
		assertEquals(1, graph.getNodesInRadius(0).size());
		assertEquals(6, graph.getNodesInRadius(1).size());
		assertEquals(12, graph.getNodesInRadius(2).size());
	}
	
	@Test
	public void testRadiusGrap() {
		for (int r = 0; r < 3; ++r) {
			HexaGraph graph = new HexaGraph(r);
			assertEquals(r, graph.getRadius());
		}
	}
	
	@Test
	public void testDistancesAllNodesRadiusOne() {
		HexaGraph graph = new HexaGraph(1);
		
		HexaNode center = graph.getNodesInRadius(0).get(0);
		assertEquals(6 * 2 * HexaGraph.INNER_RADIUS, graph.distanceToOtherNodes(center, false), 0.00001);
		
		List<HexaNode> listNodes = graph.getNodesInRadius(1);
		for (HexaNode node : listNodes) {
			assertEquals( 3 * 2 * HexaGraph.INNER_RADIUS
							+ 2 * 3 * HexaGraph.OUTER_RADIUS
							+ 4 * HexaGraph.INNER_RADIUS, graph.distanceToOtherNodes(node, false), 0.00001);
		}
	}
	
	@Test
	public void testDistancesFullNodesRadiusOne() {
		HexaGraph graph = new HexaGraph(1);

		Integer[] indexes = generateRandomIndex(4, 0, 6);
		
		HexaNode node = graph.getNodes().get( indexes[0] );
		node.setContent(new KeyNode("a"));
		
		double expectedDistances = 0;
		for (int i = 1; i < indexes.length; ++i) {
			HexaNode other = graph.getNodes().get( indexes[i] );
			other.setContent(new KeyNode("b"));
			
			expectedDistances += node.distance(other);
		}
		
		assertEquals(expectedDistances, graph.distanceToOtherNodes(node, true), 0.00001);
	}

	private Integer[] generateRandomIndex(int length, int floor, int ceiling) {
		Random random = new Random();
		
		Set<Integer> result = new HashSet<Integer>();
		for (int i = 0; i < length; ++i) {
			Integer number;
			do {
				number = random.nextInt(ceiling + 1) + floor;
			} while( result.contains( number) ) ;
				
			result.add(number);
		}
		
		return result.toArray( new Integer[0] );
		
	}
}
