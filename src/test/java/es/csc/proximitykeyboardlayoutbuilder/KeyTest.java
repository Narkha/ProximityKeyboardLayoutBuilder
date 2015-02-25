/*
 * Copyright (c) 2015
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
*/

package es.csc.proximitykeyboardlayoutbuilder;

import org.junit.*;

import static org.junit.Assert.*;


public class KeyTest{
	Key node;
	
	@Before
	public void setUp() {
		node = new Key("abc");
	}
	
	@Test
	public void containsCharacterValidCharacters() {
		assertTrue("does not found 'a'", node.containsCharacter('a'));
		assertTrue("does not found 'b'", node.containsCharacter('b'));
		assertTrue("does not found 'c'", node.containsCharacter('c'));
	}
	
	@Test
	public void containsCharacterInvalidCharacters() {
		assertFalse("should not found 'A'", node.containsCharacter('A'));
		assertFalse("should not found 'd'", node.containsCharacter('d'));
		assertFalse("should not found '.'", node.containsCharacter('.'));
		assertFalse("should not found ' '", node.containsCharacter(' '));
		assertFalse("should not found 'A'", node.containsCharacter('\n'));
	}
	
	@Test
	public void incWeight() {
		assertEquals("Just created must be 0", 0, node.getWeight());
		node.incWeight();
		assertEquals(1, node.getWeight());
	}
}
