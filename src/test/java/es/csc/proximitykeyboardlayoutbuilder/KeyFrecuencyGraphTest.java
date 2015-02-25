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

public class KeyFrecuencyGraphTest {
	
	@Test
	public void configTest() throws IOException {
		KeyFrecuencyGraph graph = new KeyFrecuencyGraph("data/test/test.config", "data/test/empty.in");
		
		assertEquals(3, graph.getNodeSize());
		assertEquals(new Key(" .,"), graph.getKey(0));
		assertEquals(new Key("a"), graph.getKey(1));
		assertEquals(new Key("uúü"), graph.getKey(2));
	}
	
	@Test
	public void singleLineSorceTest() throws IOException {
		KeyFrecuencyGraph graph = new KeyFrecuencyGraph("data/test/test.config", "data/test/singleLine.in");
		
		assertEquals(4, graph.getKey(0).getWeight());
		assertEquals(4, graph.getKey(1).getWeight());
		assertEquals(2, graph.getKey(2).getWeight());
		
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
		KeyFrecuencyGraph graph = new KeyFrecuencyGraph("data/test/test.config", "data/test/multipleLines.in");
		
		assertEquals(4, graph.getKey(0).getWeight());
		assertEquals(4, graph.getKey(1).getWeight());
		assertEquals(2, graph.getKey(2).getWeight());
		
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
		KeyFrecuencyGraph graph = new KeyFrecuencyGraph("data/test/test.config", "data/test/otherCharacters.in");
				
		assertEquals(4, graph.getKey(0).getWeight());
		assertEquals(4, graph.getKey(1).getWeight());
		assertEquals(4, graph.getKey(2).getWeight());
		
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
		KeyFrecuencyGraph graph = new KeyFrecuencyGraph("data/test/test.config", "data/test/otherCharactersUppercase.in");
				
		assertEquals(4, graph.getKey(0).getWeight());
		assertEquals(4, graph.getKey(1).getWeight());
		assertEquals(4, graph.getKey(2).getWeight());
		
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
