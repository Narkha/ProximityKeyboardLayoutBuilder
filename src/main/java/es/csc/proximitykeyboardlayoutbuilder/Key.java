/*
 * Copyright (c) 2015
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
*/

package es.csc.proximitykeyboardlayoutbuilder;

public class Key {
	int weight;
	String characters;
	
	public Key(String characters) {
		weight = 0;
		this.characters = characters;
	}
	
	public Key(Key other) {
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
				&& this.weight == ((Key) other).weight 
				&& this.characters.equals(((Key) other).characters);
	}
}
