/*
 * Copyright (c) 2015
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
*/

package es.csc.proximitykeyboardlayoutbuilder;

import static org.junit.Assert.*;

import org.junit.Test;

import es.csc.geometry.Point;

public class NodeTest {
	@Test
	public void testDistance() {		
		Node<Integer> node1 = new Node<Integer>(0, 0);
		assertEquals(0, node1.distance(node1), 0.00001);
				
		Node<Integer> node2 = new Node<Integer>( new Point(4, 3) );
		assertEquals(5, node1.distance(node2), 0.00001);
	}
	
	@Test
	public void testEmptyAfterConstrutor() {		
		Node<Integer> node = new Node<Integer>(0, 0);
		assertTrue(node.isEmpty());
	}
	
	@Test
	public void testReset() {		
		Node<Integer> node = new Node<Integer>(0, 0);
		
		node.setContent( 1 );
		assertFalse(node.isEmpty());
		
		node.resetContent();
		assertTrue(node.isEmpty());
	}

}
