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
import java.util.List;

import org.junit.*;

import static org.junit.Assert.*;

public class KeyFrecuencyGraphTest {
	
	@Test
	public void configTest() throws IOException {
		KeyFrecuencyGraph graph = new KeyFrecuencyGraph("data/test/test.config", "data/test/empty.in");
		
		assertEquals(3, graph.size());
		
		List<Key> keys = graph.keys();
		
		assertEquals(new Key(" .,"), keys.get(0));
		assertEquals(new Key("a"), keys.get(1));
		assertEquals(new Key("uúü"), keys.get(2));
	}
	
	@Test
	public void singleLineSorceTest() throws IOException {
		KeyFrecuencyGraph graph = new KeyFrecuencyGraph("data/test/test.config", "data/test/singleLine.in");
		
		List<Key> keys = graph.keys();
		
		assertEquals(4, graph.getFrecuency(keys.get(0)));
		assertEquals(4, graph.getFrecuency(keys.get(1)));
		assertEquals(2, graph.getFrecuency(keys.get(2)));
		
		assertArrayEquals(
				new int[][] {{1, 3, 2},
					         {3, 1, 2},
					         {2, 2, 0}},
			    new int[][] {{graph.getFrecuency(keys.get(0), keys.get(0)), graph.getFrecuency(keys.get(0), keys.get(1)), graph.getFrecuency(keys.get(0), keys.get(2))},
					         {graph.getFrecuency(keys.get(1), keys.get(0)), graph.getFrecuency(keys.get(1), keys.get(1)), graph.getFrecuency(keys.get(1), keys.get(2))},
					         {graph.getFrecuency(keys.get(2), keys.get(0)), graph.getFrecuency(keys.get(2), keys.get(1)), graph.getFrecuency(keys.get(2), keys.get(2))}}
	    );
	}
	
	@Test
	public void multipleLinesSourceTest() throws IOException {
		KeyFrecuencyGraph graph = new KeyFrecuencyGraph("data/test/test.config", "data/test/multipleLines.in");
		
		List<Key> keys = graph.keys();
		
		assertEquals(4, graph.getFrecuency(keys.get(0)));
		assertEquals(4, graph.getFrecuency(keys.get(1)));
		assertEquals(2, graph.getFrecuency(keys.get(2)));
		
		assertArrayEquals(
				new int[][] {{1, 3, 2},
					         {3, 1, 2},
					         {2, 2, 0}},
		         new int[][] {{graph.getFrecuency(keys.get(0), keys.get(0)), graph.getFrecuency(keys.get(0), keys.get(1)), graph.getFrecuency(keys.get(0), keys.get(2))},
				              {graph.getFrecuency(keys.get(1), keys.get(0)), graph.getFrecuency(keys.get(1), keys.get(1)), graph.getFrecuency(keys.get(1), keys.get(2))},
				              {graph.getFrecuency(keys.get(2), keys.get(0)), graph.getFrecuency(keys.get(2), keys.get(1)), graph.getFrecuency(keys.get(2), keys.get(2))}}
			);
	}
	

	@Test
	public void otherCharactersSoruceFile() throws IOException {
		KeyFrecuencyGraph graph = new KeyFrecuencyGraph("data/test/test.config", "data/test/otherCharacters.in");
				
		List<Key> keys = graph.keys();
		
		assertEquals(4, graph.getFrecuency(keys.get(0)));
		assertEquals(4, graph.getFrecuency(keys.get(1)));
		assertEquals(4, graph.getFrecuency(keys.get(2)));
		
		assertArrayEquals(
				new int[][] {{1, 3, 4},
					         {3, 1, 2},
					         {4, 2, 1}},
					         new int[][] {{graph.getFrecuency(keys.get(0), keys.get(0)), graph.getFrecuency(keys.get(0), keys.get(1)), graph.getFrecuency(keys.get(0), keys.get(2))},
							              {graph.getFrecuency(keys.get(1), keys.get(0)), graph.getFrecuency(keys.get(1), keys.get(1)), graph.getFrecuency(keys.get(1), keys.get(2))},
							              {graph.getFrecuency(keys.get(2), keys.get(0)), graph.getFrecuency(keys.get(2), keys.get(1)), graph.getFrecuency(keys.get(2), keys.get(2))}}
	    );
	}
	

	

	@Test
	public void otherCharactersUppercaseSoruceFile() throws IOException {
		KeyFrecuencyGraph graph = new KeyFrecuencyGraph("data/test/test.config", "data/test/otherCharactersUppercase.in");
				
		List<Key> keys = graph.keys();
		
		assertEquals(4, graph.getFrecuency(keys.get(0)));
		assertEquals(4, graph.getFrecuency(keys.get(1)));
		assertEquals(4, graph.getFrecuency(keys.get(2)));
		
		assertArrayEquals(
				new int[][] {{1, 3, 4},
					         {3, 1, 2},
					         {4, 2, 1}},
					         new int[][] {{graph.getFrecuency(keys.get(0), keys.get(0)), graph.getFrecuency(keys.get(0), keys.get(1)), graph.getFrecuency(keys.get(0), keys.get(2))},
							              {graph.getFrecuency(keys.get(1), keys.get(0)), graph.getFrecuency(keys.get(1), keys.get(1)), graph.getFrecuency(keys.get(1), keys.get(2))},
							              {graph.getFrecuency(keys.get(2), keys.get(0)), graph.getFrecuency(keys.get(2), keys.get(1)), graph.getFrecuency(keys.get(2), keys.get(2))}}
	    );
	}
}
