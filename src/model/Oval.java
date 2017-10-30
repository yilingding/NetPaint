package model;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.io.Serializable;

/**
 * This is the Oval class that draw all the ovals(circle) in the graph. It
 * extends PaintObject class.
 * 
 * @author Yiling Ding and Mingjun Zhou
 */

public class Oval extends PaintObject implements Serializable {

	private int x1;
	private int x2;
	private int y1;
	private int y2;
	private Color color;

	/*
	 * Purpose: the constructor of class Oval.
	 * 
	 * Parameter: Color color, Point point1, Point point2
	 * 
	 * Return type: none
	 */
	public Oval(Color color, Point point1, Point point2) {
		super(color, point1, point2);
		x1 = point1.x;
		x2 = point2.x;
		y1 = point1.y;
		y2 = point2.y;
		this.color = color;
	}

	/*
	 * Purpose: draws the Oval.
	 * 
	 * Parameter: Graphics g
	 * 
	 * Return type: none
	 */
	@Override
	public void draw(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		if (x1 > x2) {
			int x = x1;
			x1 = x2;
			x2 = x;
		}

		if (y1 > y2) {
			int y = y1;
			y1 = y2;
			y2 = y;
		}
		g2.setColor(color);
		g2.fillOval(x1, y1, (x2 - x1), (y2 - y1));

	}

}
