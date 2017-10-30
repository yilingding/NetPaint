package model;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.io.Serializable;

/**
 * The abstract class that all the other objects extended. It is the base class
 * or superclass of all Line, Rectangle, and Oval class.
 * 
 * @author Yiling Ding and Mingjun Zhou
 */

public abstract class PaintObject implements Serializable {
	private Color color;
	private Point point1;
	private Point point2;

	/*
	 * Purpose: the constructor of class PaintObject.
	 * 
	 * Parameter: Color color, Point point1, Point point2
	 * 
	 * Return type: none
	 */
	public PaintObject(Color color, Point point1, Point point2) {
		this.color = color;
		this.point1 = point1;
		this.point2 = point2;
	}

	/*
	 * Purpose: the abstract method of PaintObject.
	 * 
	 * Parameter: Graphics g
	 * 
	 * Return type: none
	 */
	abstract public void draw(Graphics g);

}
