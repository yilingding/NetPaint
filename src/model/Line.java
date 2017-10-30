package model;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.io.Serializable;

/**
 * This is the Line class that draw all the lines in the graph. It extends
 * PaintObject class.
 * 
 * @author Yiling Ding and Mingjun Zhou
 */
public class Line extends PaintObject implements Serializable {
	private int x1;
	private int x2;
	private int y1;
	private int y2;
	private Color color;

	/*
	 * Purpose: the constructor of class Line.
	 * 
	 * Parameter: Color color, Point point1, Point point2
	 * 
	 * Return type: none
	 */
	public Line(Color color, Point point1, Point point2) {
		super(color, point1, point2);
		x1 = point1.x;
		x2 = point2.x;
		y1 = point1.y;
		y2 = point2.y;
		this.color = color;
	}

	/*
	 * Purpose: draws the Line.
	 * 
	 * Parameter: Graphics g
	 * 
	 * Return type: none
	 */
	@Override
	public void draw(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(color);
		g2.drawLine(x1, y1, x2, y2);

	}

}
