/*
 * Copyright (c) 2015
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
*/

package es.csc.pklb.frecuency;

import java.io.IOException;
import java.util.List;

import org.junit.*;

import es.csc.pklb.frecuency.Key;
import es.csc.pklb.frecuency.KeyFrecuencyGraph;
import static org.junit.Assert.*;

public class KeyFrecuencyGraphTest {
	
	@Test
	public void config() throws IOException {
		String keysFile = "data/test/KeyFrecuencyGraphTest/test.config";
		String sourceFile = "data/test/KeyFrecuencyGraphTest/empty.in";
		KeyFrecuencyGraph graph = new KeyFrecuencyGraph(keysFile, sourceFile);
		
		assertEquals(3, graph.size());
		
		List<Key> keys = graph.keys();
		
		assertEquals(new Key(" .,"), keys.get(0));
		assertEquals(new Key("a"), keys.get(1));
		assertEquals(new Key("uúü"), keys.get(2));
	}
	
	@Test
	public void singleLineSorce() throws IOException {
		String keysFile = "data/test/KeyFrecuencyGraphTest/test.config";
		String sourceFile = "data/test/KeyFrecuencyGraphTest/singleLine.in";
		KeyFrecuencyGraph graph = new KeyFrecuencyGraph(keysFile, sourceFile);
		
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
	public void multipleLinesSource() throws IOException {
		String keysFile = "data/test/KeyFrecuencyGraphTest/test.config";
		String sourceFile = "data/test/KeyFrecuencyGraphTest/multipleLines.in";
		KeyFrecuencyGraph graph = new KeyFrecuencyGraph(keysFile, sourceFile);
		
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
	public void otherCharacters() throws IOException {
		String keysFile = "data/test/KeyFrecuencyGraphTest/test.config";
		String sourceFile = "data/test/KeyFrecuencyGraphTest/otherCharacters.in";
		KeyFrecuencyGraph graph = new KeyFrecuencyGraph(keysFile, sourceFile);
				
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
	public void otherCharactersUppercase() throws IOException {
		String keysFile = "data/test/KeyFrecuencyGraphTest/test.config";
		String sourceFile = "data/test/KeyFrecuencyGraphTest/otherCharactersUppercase.in";
		KeyFrecuencyGraph graph = new KeyFrecuencyGraph(keysFile, sourceFile);
				
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
	public void keysSortedByFrecuency() throws IOException {
		String keysFile = "data/test/KeyFrecuencyGraphTest/test.config";
		String sourceFile = "data/test/KeyFrecuencyGraphTest/keysSortedByFrecuency.in";
		KeyFrecuencyGraph graph = new KeyFrecuencyGraph(keysFile, sourceFile);
				
		List<Key> keys = graph.keys();
		
		assertEquals(3, graph.getFrecuency(keys.get(0)));
		assertEquals(2, graph.getFrecuency(keys.get(1)));
		assertEquals(8, graph.getFrecuency(keys.get(2)));
		
		List<Key> keysByFrecueny = graph.keysSortedByFrecuency();
		
		assertEquals(keys.get(0), keysByFrecueny.get(1));
		assertEquals(keys.get(1), keysByFrecueny.get(2));
		assertEquals(keys.get(2), keysByFrecueny.get(0));
		
		assertEquals(8, graph.getFrecuency(keysByFrecueny.get(0)) );		
		assertEquals(3, graph.getFrecuency(keysByFrecueny.get(1)) );
		assertEquals(2, graph.getFrecuency(keysByFrecueny.get(2)) );
	}
}
