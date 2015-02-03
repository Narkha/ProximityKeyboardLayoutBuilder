package es.csc.proximitykeyboardlayoutbuilder;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


public class KeyGraph {
	private List<KeyNode> nodes;
	private int[][] edges;

	public KeyGraph(String keysFile, String sourceFile) throws IOException {
		readNodes(keysFile);
		buildEdges(sourceFile);
	}

	private void readNodes(String keysFile) throws IOException {
		List<String> lines = Files.readAllLines(Paths.get(keysFile));
		
		nodes = new ArrayList<KeyNode>(lines.size());
		for(String line: lines) {
			if (line.length() > 0) {
				nodes.add( new KeyNode(line));
			}
		}
	}

	private void buildEdges(String sourceFile) throws IOException {
		edges = new int[nodes.size()][nodes.size()];

		
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
					nodes.get(actualKey).incWeight();
					
					if (previousKey >= 0) {
						strengthenEdge(previousKey, actualKey);
					}
					
					previousKey = actualKey;					
				}
			}
		}
	}

	private int getNodeIndex(char character) {
		for (int i = 0; i < nodes.size(); ++i) {
			if (nodes.get(i).containsCharacter(character)) {
				return i;
			}
		}
		
		return -1;
	}	

	private void strengthenEdge(int key1, int key2) {
		if (key1 <= key2) {
			++edges[key1][key2];
		}
		else {
			++edges[key2][key1];
		}
	}
	
	/***
	 * @return the number of nodes
	 */
	public int getNodeSize() {
		return nodes.size();
	}
	
	public KeyNode getNode(int index) {
		return new KeyNode( nodes.get(index) );
	}
	
	public int getWeight(int key1, int key2) {
		if (key1 <= key2) {
			return edges[key1][key2];
		}
		else  {
			return edges[key2][key1];
		}
	}
}
