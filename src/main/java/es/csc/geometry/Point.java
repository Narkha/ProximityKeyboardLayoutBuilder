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
	protected final double x;
	protected final double y;
	
	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public Point(Point other) {
		this.x = other.x;
		this.y = other.y;
	}	
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public double distance(Point other) {
		double xDistance = Math.abs(this.x - other.x);
		double yDistance = Math.abs(this.y - other.y);
		
		return Math.sqrt(xDistance*xDistance + yDistance*yDistance); 
	}
	
	/***
	 * @param other
	 * 
	 * @return other - this
	 */
	public Point vector(Point other) {
		double x = other.x - this.x,
				y = other.y - this.y;
		
		return new Point(x, y);
	}
	
	/***
	 * Return the angle of vector between (0, 0) and this point.
	 * 
	 * @return a value in the interval [0, 2*PI)
	 */
	public double angle() {
		double delta = 0.000001;	
		double angle;
		if (Math.abs(x) < delta) {
			angle = (Math.abs(y) < delta) ? 0 : Math.PI / 2;
			
			if (y < 0) {
				angle += Math.PI;
			}
		}
		else {
			angle = Math.atan(y / x);
			if (x < 0) {
				angle += Math.PI;
			}
			else if (y < 0) {
				angle += 2 * Math.PI;
			}
		}
		
		return angle;
	}
	
	/***
	 * Return the module of vector between (0, 0) and this point.
	 * 
	 * @return a value in the interval [0, 2*PI)
	 */
	public double module() {
		return Math.sqrt(x*x + y*y);
	}
	
	@Override
	public String toString() {
		return "(" + String.format("%.3f", x) + ", " + String.format("%.3f", y) + ")"; 
	}
}
