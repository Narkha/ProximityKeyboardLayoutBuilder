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
import java.util.List;


public class KeyFrecuencyGraph {
	private List<Key> keys;
	private int[][] frecuencies;

	public KeyFrecuencyGraph(String keysFile, String sourceFile) throws IOException {
		readKeys(keysFile);
		buildEdges(sourceFile);
	}

	private void readKeys(String keysFile) throws IOException {
		List<String> lines = Files.readAllLines(Paths.get(keysFile));
		
		keys = new ArrayList<Key>(lines.size());
		for(String line: lines) {
			if (line.length() > 0) {
				keys.add( new Key(line));
			}
		}
	}

	private void buildEdges(String sourceFile) throws IOException {
		frecuencies = new int[keys.size()][keys.size()];

		
		BufferedReader input = null;
		try {
			input = new BufferedReader(new FileReader(sourceFile));
			
			buildEdges(input);
		}
		finally {
			if (input != null) {
				input.close();
			}
		}
	}

	private void buildEdges(BufferedReader input) throws IOException {
		int defaultKey = 0;
		int previousKey = -1, actualKey = -1;
		
		String line;
		while((line = input.readLine()) != null) {
			for (int i = 0; i < line.length(); ++i) {
				
				char character = Character.toLowerCase(line.charAt(i));
				actualKey = getNodeIndex(character);
				if (actualKey == -1) {
					if (previousKey != -1) {
						strengthenEdge(previousKey, defaultKey);
					}
					previousKey = -1;
				}
				else {									
					keys.get(actualKey).incWeight();
					
					if (previousKey >= 0) {
						strengthenEdge(previousKey, actualKey);
					}
					
					previousKey = actualKey;					
				}
			}
		}
	}

	private int getNodeIndex(char character) {
		for (int i = 0; i < keys.size(); ++i) {
			if (keys.get(i).containsCharacter(character)) {
				return i;
			}
		}
		
		return -1;
	}	

	private void strengthenEdge(int key1, int key2) {
		if (key1 <= key2) {
			++frecuencies[key1][key2];
		}
		else {
			++frecuencies[key2][key1];
		}
	}
	
	/***
	 * @return the number of keys
	 */
	public int size() {
		return keys.size();
	}
	
	public Key getKey(int index) {
		return new Key( keys.get(index) );
	}
	
	public int getWeight(int key1, int key2) {
		if (key1 <= key2) {
			return frecuencies[key1][key2];
		}
		else  {
			return frecuencies[key2][key1];
		}
	}
}
