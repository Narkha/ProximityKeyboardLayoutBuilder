/*
 * Copyright (c) 2015
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
*/

package es.csc.proximitykeyboardlayoutbuilder;

import java.io.IOException;

import org.junit.*;

import static org.junit.Assert.*;

public class KeyGraphTest {
	
	@Test
	public void configTest() throws IOException {
		KeyGraph graph = new KeyGraph("data/test/test.config", "data/test/empty.in");
		
		assertEquals(3, graph.getNodeSize());
		assertEquals(new KeyNode(" .,"), graph.getNode(0));
		assertEquals(new KeyNode("a"), graph.getNode(1));
		assertEquals(new KeyNode("uúü"), graph.getNode(2));
	}
	
	@Test
	public void singleLineSorceTest() throws IOException {
		KeyGraph graph = new KeyGraph("data/test/test.config", "data/test/singleLine.in");
		
		assertEquals(4, graph.getNode(0).getWeight());
		assertEquals(4, graph.getNode(1).getWeight());
		assertEquals(2, graph.getNode(2).getWeight());
		
		assertArrayEquals(
				new int[][] {{1, 3, 2},
					         {3, 1, 2},
					         {2, 2, 0}},
			    new int[][] {{graph.getWeight(0, 0), graph.getWeight(0, 1), graph.getWeight(0, 2)},
					         {graph.getWeight(1, 0), graph.getWeight(1, 1), graph.getWeight(1, 2)},
					         {graph.getWeight(2, 0), graph.getWeight(2, 1), graph.getWeight(2, 2)}}
	    );
	}
	
	@Test
	public void multipleLinesSourceTest() throws IOException {
		KeyGraph graph = new KeyGraph("data/test/test.config", "data/test/multipleLines.in");
		
		assertEquals(4, graph.getNode(0).getWeight());
		assertEquals(4, graph.getNode(1).getWeight());
		assertEquals(2, graph.getNode(2).getWeight());
		
		assertArrayEquals(
				new int[][] {{1, 3, 2},
					         {3, 1, 2},
					         {2, 2, 0}},
			    new int[][] {{graph.getWeight(0, 0), graph.getWeight(0, 1), graph.getWeight(0, 2)},
					         {graph.getWeight(1, 0), graph.getWeight(1, 1), graph.getWeight(1, 2)},
					         {graph.getWeight(2, 0), graph.getWeight(2, 1), graph.getWeight(2, 2)}}
	    );
	}
	

	@Test
	public void otherCharactersSoruceFile() throws IOException {
		KeyGraph graph = new KeyGraph("data/test/test.config", "data/test/otherCharacters.in");
				
		assertEquals(4, graph.getNode(0).getWeight());
		assertEquals(4, graph.getNode(1).getWeight());
		assertEquals(4, graph.getNode(2).getWeight());
		
		assertArrayEquals(
				new int[][] {{1, 3, 4},
					         {3, 1, 2},
					         {4, 2, 1}},
			    new int[][] {{graph.getWeight(0, 0), graph.getWeight(0, 1), graph.getWeight(0, 2)},
					         {graph.getWeight(1, 0), graph.getWeight(1, 1), graph.getWeight(1, 2)},
					         {graph.getWeight(2, 0), graph.getWeight(2, 1), graph.getWeight(2, 2)}}
	    );
	}
	

	

	@Test
	public void otherCharactersUppercaseSoruceFile() throws IOException {
		KeyGraph graph = new KeyGraph("data/test/test.config", "data/test/otherCharactersUppercase.in");
				
		assertEquals(4, graph.getNode(0).getWeight());
		assertEquals(4, graph.getNode(1).getWeight());
		assertEquals(4, graph.getNode(2).getWeight());
		
		assertArrayEquals(
				new int[][] {{1, 3, 4},
					         {3, 1, 2},
					         {4, 2, 1}},
			    new int[][] {{graph.getWeight(0, 0), graph.getWeight(0, 1), graph.getWeight(0, 2)},
					         {graph.getWeight(1, 0), graph.getWeight(1, 1), graph.getWeight(1, 2)},
					         {graph.getWeight(2, 0), graph.getWeight(2, 1), graph.getWeight(2, 2)}}
	    );
	}
}
