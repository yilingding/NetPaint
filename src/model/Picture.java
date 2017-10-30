package model;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import javax.imageio.ImageIO;

/**
 * This is the Picture class that draw all the images in the graph. It extends
 * PaintObject class.
 * 
 * @author Minjun Zhou
 */

public class Picture extends PaintObject implements Serializable {
	private Image image;
	private int x1;
	private int x2;
	private int y1;
	private int y2;
	private Color color;

	/*
	 * Purpose: the constructor of class Picture.
	 * 
	 * Parameter: Color color, Point point1, Point point2
	 * 
	 * Return type: none
	 */
	public Picture(Color color, Point point1, Point point2) {
		super(color, point1, point2);
		x1 = point1.x;
		x2 = point2.x;
		y1 = point1.y;
		y2 = point2.y;
		this.color = color;

	}

	/*
	 * Purpose: draws the Image.
	 * 
	 * Parameter: Graphics g
	 * 
	 * Return type: none
	 */
	@Override
	public void draw(Graphics g) {
		try {
			image = ImageIO.read(new File("images/pic.png"));
		} catch (IOException io) {
			System.out.println("error reading file");
			return;
		}

		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(color);
		g2.drawImage(image, x1, y1, x2 - x1, y2 - y1, null);

	}

}
