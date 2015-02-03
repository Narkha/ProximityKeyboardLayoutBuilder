package es.csc.proximitykeyboardlayoutbuilder;

public class KeyNode {
	int weight;
	String characters;
	
	public KeyNode(String characters) {
		weight = 0;
		this.characters = characters;
	}
	
	public KeyNode(KeyNode other) {
		this.weight = other.weight;
		this.characters = other.characters;
	}

	public int getWeight() {
		return weight;
	}
	
	public void incWeight() {
		++weight;
	}

	public boolean containsCharacter(char character) {
		return characters.indexOf(character) != -1;
	}
	
	@Override
	public boolean equals(Object other) {
		return this.getClass() == other.getClass()
				&& this.weight == ((KeyNode) other).weight 
				&& this.characters.equals(((KeyNode) other).characters);
	}
}
