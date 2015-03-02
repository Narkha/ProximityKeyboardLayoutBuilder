/*
 * Copyright (c) 2015
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
*/

package es.csc.proximitykeyboardlayoutbuilder;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


public class KeyFrecuencyGraph {
	private ArrayList<Key> keys;
	private HashMap<Key, Integer> keysIndex;
	private int[] keyFrecuencies;
	private int[][] frecuencies;

	public KeyFrecuencyGraph(String keysFile, String sourceFile) throws IOException {
		readKeys(keysFile);
		buildKeysIndex();
		parseSource(sourceFile);
	}

	private void readKeys(String keysFile) throws IOException {
		List<String> lines = Files.readAllLines(Paths.get(keysFile));
		
		keys = new ArrayList<Key>();
		for(String line: lines) {
			if (line.length() > 0) {
				keys.add( new Key(line) );
			}
		}
	}

	private void buildKeysIndex() {
		keysIndex = new HashMap<Key, Integer>();
		for (int i = 0, n = keys.size(); i < n; ++i) {
			keysIndex.put(keys.get(i), i);
		}
	}

	private void parseSource(String sourceFile) throws IOException {
		keyFrecuencies = new int[keysIndex.size()];
		frecuencies = new int[keysIndex.size()][keysIndex.size()];
		
		BufferedReader input = null;
		try {
			input = new BufferedReader(new FileReader(sourceFile));
			
			parseSource(input);
		}
		finally {
			if (input != null) {
				input.close();
			}
		}
	}

	private void parseSource(BufferedReader input) throws IOException {
		Key defaultKey = keys.get(0), previousKey = null, actualKey = null;
		
		String line;
		while((line = input.readLine()) != null) {
			for (int i = 0; i < line.length(); ++i) {
				
				char character = Character.toLowerCase(line.charAt(i));
				actualKey = searchKey(character);
				if (actualKey == null) {
					if (previousKey != null) {
						strengthenEdge(previousKey, defaultKey);
					}
					previousKey = null;
				}
				else {		
					++keyFrecuencies[ keysIndex.get(actualKey) ];
					
					if (previousKey != null) {
						strengthenEdge(previousKey, actualKey);
					}
					
					previousKey = actualKey;					
				}
			}
		}
	}

	private Key searchKey(char character) {
		for(Key key : keys) {
			if (key.containsCharacter(character)) {
				return key;
			}
		}
		
		return null;
	}	

	private void strengthenEdge(Key key1, Key key2) {
		int index1 = keysIndex.get(key1),
				index2 = keysIndex.get(key2);
				
		++frecuencies[index1][index2];
	}
	
	@SuppressWarnings("unchecked")
	public List<Key> keys() {
		return (List<Key>) keys.clone();
	}
	
	/***
	 * @return the number of keys
	 */
	public int size() {
		return keysIndex.size();
	}
	
	public int getFrecuency(Key key) {
		return keyFrecuencies[ keysIndex.get(key) ];
	}
	
	/**
	 * @return the frecuency in whic key1 and key2 apperar toguether
	 *         the order of the arguments is irrelenvant.
	 */
	public int getFrecuency(Key key1, Key key2) {
		int index1 = keysIndex.get(key1),
				index2 = keysIndex.get(key2);

		if (index1 == index2) {
			return frecuencies[index1][index2];
		}
		else {
			return frecuencies[index1][index2] + frecuencies[index2][index1];
		}
	}

	/***
	 * 
	 * @return keys sorted by descending frecuency
	 */
	@SuppressWarnings("unchecked")
	public List<Key> keysSortedByFrecuency() {
		class KeysComparator implements java.util.Comparator<Key> {
			public int compare(Key key1, Key key2) {
		        return getFrecuency(key1) - getFrecuency(key2);
			}			
		}		

		List<Key> sortedKeys = (List<Key>) keys.clone();
		Collections.sort(sortedKeys, Collections.reverseOrder( new KeysComparator() ));
		return sortedKeys;
	}
}
