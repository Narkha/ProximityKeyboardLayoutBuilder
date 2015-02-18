/*
 * Copyright (c) 2015
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
*/

package es.csc.geometry;

import org.junit.*;

import static org.junit.Assert.*;

public class PointTest {

	@Test
	public void distance() {
		Point p1 = new Point(0, 0),
				p2 = new Point(1, 0),
				p3 = new Point(0,2),
				p4 = new Point(-1, 2);
		
		assertEquals(1.0, p1.distance(p2), 0.0001);
		assertEquals(2.0, p1.distance(p3), 0.0001);
		
		// sqrt(5)
		assertEquals(2.236068, p1.distance(p4), 0.0001);
		//sqrt(8)
		assertEquals(2.828427, p2.distance(p4), 0.0001);
	}
}
