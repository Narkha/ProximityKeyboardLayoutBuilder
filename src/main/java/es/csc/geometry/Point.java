/*
 * Copyright (c) 2015
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
*/

package es.csc.geometry;

public class Point {
	double x, y;
	
	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public Point(Point other) {
		this.x = other.x;
		this.y = other.y;
	}	
	
	public double distance(Point other) {
		double xDistance = Math.abs(this.x - other.x);
		double yDistance = Math.abs(this.y - other.y);
		
		return Math.sqrt(xDistance*xDistance + yDistance*yDistance); 
	}
	
	@Override
	public String toString() {
		return "(" + String.format("%.3f", x) + ", " + String.format("%.3f", y) + ")"; 
	}
	
	@Override
	public boolean equals(Object other) {
		if (this.getClass() == other.getClass()) {
			Point otherPoint = (Point) other;
			return this.x == otherPoint.x && this.y == otherPoint.y;
		}
		else {
			return false;
		}
	}
}
