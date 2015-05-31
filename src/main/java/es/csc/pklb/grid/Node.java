/*
 * Copyright (c) 2015
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
*/

package es.csc.pklb.grid;

import es.csc.geometry.Point;
import es.csc.pklb.frecuency.Key;

public class Node extends Point
					implements Cloneable {

	private Key content = null;

	public Node(double x, double y) {
		super(x, y);		
	}
	
	public Node(Point center) {
		super(center);		
	}
	
	public void setContent(Key data) {
		content = data;
	}
	
	public Key getContent() {
		return content;
	}
	
	public void resetContent() {
		content = null;
	}
	
	public boolean isEmpty() {
		return content == null;
	}
	
	@Override
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}
	
	@Override
	public String toString() {
		return super.toString() + " " + (isEmpty() ? "NULL" : content);
	}
	
	@Override
	public boolean equals(Object other) {
		return this == other;
	}
}
