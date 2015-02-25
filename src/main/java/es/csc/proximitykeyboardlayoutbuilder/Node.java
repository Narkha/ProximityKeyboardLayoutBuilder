/*
 * Copyright (c) 2015
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
*/

package es.csc.proximitykeyboardlayoutbuilder;

import es.csc.geometry.Point;

public class Node<E> {

	private Point center;
	private E content = null;

	public Node(double x, double y) {
		this.center = new Point(x, y);		
	}
	
	public Node(Point center) {
		this.center = center;		
	}
	
	public void setContent(E data) {
		content = data;
	}
	
	public E getContent() {
		return content;
	}
	
	public void resetContent() {
		content = null;
	}
	
	public boolean isEmpty() {
		return content == null;
	}
	
	public double distance(Node<E> other) {
		return this.center.distance(other.center);
	}
}
