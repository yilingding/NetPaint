package client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.Scrollable;

import model.Line;
import model.Oval;
import model.PaintObject;
import model.Picture;
import model.Rectangle;
import server.ToBeTheServer;


/**
 * A JPanel GUI for Netpaint that has all paint objects drawn on it. A JPanel
 * exists in this JFrame that will draw this list of paint objects.
 * 
 * Recommended: One team member work on drawing images with a mouse listener as
 * each new PaintObject is added to a Vector. Also need a "ghost" image that is
 * drawn as the mouse is dragged.
 * 
 * @author Yiling Ding and Mingjun Zhou
 */

public class ToBeTheClient extends JFrame implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final String ADDRESS = "localhost";

	/*
	 * Purpose: the main method of the GUI.
	 * 
	 * Parameter: String[] args
	 * 
	 * Return type: none
	 */
	public static void main(String[] args) {
		ToBeTheClient view = new ToBeTheClient();
		view.setVisible(true);
	}

	private Socket socket;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	private DrawingPanel drawingPanel;

	private JButton chooseColor = new JButton("Choose a color");

	private JRadioButton line = new JRadioButton("line");
	private JRadioButton rectangle = new JRadioButton("rectangle");
	private JRadioButton oval = new JRadioButton("oval");
	private JRadioButton image = new JRadioButton("image",true);
	private int option;
	private Color currentColor;
	private Point point1;
	private Point point2;
	private int click;
	private int moved;
	private static Vector<PaintObject> allPaintObjects;

	/*
	 * Purpose: the constructor of the GUI that sets up the interface.
	 * 
	 * Parameter: none
	 * 
	 * Return type: none
	 */
	public ToBeTheClient() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocation(20, 20);
		setSize(new Dimension(1030, 800));

		click = 0;
		setLayout(null);
		drawingPanel = new DrawingPanel();
		drawingPanel.setPreferredSize(new Dimension(1400, 700));
		moved = 0;
		currentColor = Color.BLACK;
		JScrollPane scroll = new JScrollPane(drawingPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

		drawingPanel.setAutoscrolls(true);
		add(scroll);
		option = 4;
		scroll.setSize(1030, 600);
		scroll.setLocation(0, 10);
		JPanel bottom = new JPanel();
		bottom.setSize(1030, 190);
		bottom.setLocation(0, 610);
		bottom.setBackground(Color.PINK);
		ButtonGroup group = new ButtonGroup();
		group.add(line);
		group.add(rectangle);
		group.add(oval);
		group.add(image);
		bottom.add(chooseColor);
		bottom.add(line);
		bottom.add(rectangle);
		bottom.add(oval);
		bottom.add(image);
		add(bottom);

		try {
			// Connect to the Server (construct a new Socket object)
			socket = new Socket(ADDRESS, ToBeTheServer.getServerPort());

			// Get the server's input and output streams for reads and writes
			oos = new ObjectOutputStream(socket.getOutputStream());
			ois = new ObjectInputStream(socket.getInputStream());

		} catch (IOException e) {
			this.cleanUpAndQuit("Couldn't connect to the server");
		}

		addListener();

		// Start a new ServerListener thread
		ServerListener serverListener = new ServerListener();
		serverListener.start();

	}

	/*
	 * Purpose: add listners to the buttons and drawingPanel.
	 * 
	 * Parameter: none
	 * 
	 * Return type: none
	 */
	private void addListener() {
		ShapeListener shapeListener = new ShapeListener();
		DrawingListener drawingListener = new DrawingListener();

		line.addActionListener(shapeListener);
		image.addActionListener(shapeListener);
		oval.addActionListener(shapeListener);
		rectangle.addActionListener(shapeListener);
		ColorOptionListener colorListener = new ColorOptionListener();
		chooseColor.addActionListener(colorListener);
		drawingPanel.addMouseListener(drawingListener);
		drawingPanel.addMouseMotionListener(drawingListener);
	}

	/*
	 * Purpose: construct the serverlistener that can synchronize data to
	 * server.
	 */
	private class ServerListener extends Thread {

		/*
		 * Purpose: synchronize data to server while it is not closed.
		 * 
		 * Parameter: none
		 * 
		 * Return type: none
		 */
		@Override
		public void run() {

			try {
				ToBeTheClient.this.allPaintObjects = ((Vector<PaintObject>) ois.readObject());
			} catch (ClassNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			drawingPanel.repaint();

			// Repeatedly accept String objects from the server and add
			// them to our model.
			try {
				/*
				 * The server sent us a PaintObject? Stick it in the
				 * allPaintObjects!
				 */
				while (true) {
					PaintObject temp = (PaintObject) ois.readObject();
					ToBeTheClient.this.allPaintObjects.add(temp);
					drawingPanel.repaint();
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	/*
	 * Purpose: clean up the soket and quit the client.
	 * 
	 * Parameter: String message
	 * 
	 * Return type: none
	 */
	private void cleanUpAndQuit(String message) {
		JOptionPane.showMessageDialog(ToBeTheClient.this, message);
		try {
			if (socket != null)
				socket.close();
		} catch (IOException e) {
			// Couldn't close the socket, we are in deep trouble. Abandon ship.
			e.printStackTrace();
		}
		ToBeTheClient.this.dispatchEvent(new WindowEvent(ToBeTheClient.this, WindowEvent.WINDOW_CLOSING));
	}

	/*
	 * Purpose: the class that listen to the drawingPanel.
	 */
	private class DrawingListener implements MouseListener, MouseMotionListener {
		@Override
		public void mouseDragged(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		/*
		 * Purpose: draw the image when mouseMoved.
		 * 
		 * Parameter: MouseEvent e
		 * 
		 * Return type: none
		 */
		@Override
		public void mouseMoved(MouseEvent e) {
			// TODO Auto-generated method stub
			if (click % 2 != 0) {
				point2 = e.getPoint();
				moved = 1;
				repaint();
			}
		}

		/*
		 * Purpose: draw the image when mouseClicked.
		 * 
		 * Parameter: MouseEvent e
		 * 
		 * Return type: none
		 */
		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub
			if (click % 2 == 0) {
				point1 = e.getPoint();
				System.out.println("1 " + e.getX() + "," + e.getY());
				click++;
				moved = 0;
			} else {
				point2 = e.getPoint();
				System.out.println("2 " + e.getX() + "," + e.getY());
				moved = 2;
				PaintObject paintObject;
				if (option == 1) {
					paintObject = new Line(currentColor, point1, point2);
					allPaintObjects.add(paintObject);
				} else if (option == 2) {
					paintObject = new Rectangle(currentColor, point1, point2);
					allPaintObjects.add(paintObject);
				} else if (option == 3) {
					paintObject = new Oval(currentColor, point1, point2);
					allPaintObjects.add(paintObject);
				} else {
					paintObject = new Picture(currentColor, point1, point2);
					allPaintObjects.add(paintObject);
				}
				try {
					// The serialized object will not change when a new
					// PaintObject
					// is added to the Vector unless you reset the
					// ObjectOutputStream.
					oos.reset();
					System.out.println("paintObject to write is: " + paintObject);
					oos.writeObject(paintObject);
					// Avoid sending the same serialized object next time.

				} catch (IOException a) {
					// TODO Auto-generated catch block
					a.printStackTrace();
					ToBeTheClient.this.cleanUpAndQuit("Couldn't send a message to the server");
				}

				drawingPanel.repaint();

				click++;
			}

		}

		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub

		}

	}

	/*
	 * Purpose: the class that construct the drawingPanel.
	 */
	class DrawingPanel extends JPanel implements Scrollable {

		/*
		 * Purpose: paint all the component in allPaintObjects.
		 * 
		 * Parameter: Graphics g
		 * 
		 * Return type: none
		 */
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.setColor(Color.white);
			g.fillRect(0, 0, this.getWidth(), this.getHeight());

			for (int i = 0; i < allPaintObjects.size(); i++)
				allPaintObjects.get(i).draw(g);
			PaintObject paintObject;
			if (moved == 1) {
				if (option == 1) {
					paintObject = new Line(currentColor, point1, point2);
					paintObject.draw(g);

				} else if (option == 2) {
					paintObject = new Rectangle(currentColor, point1, point2);
					paintObject.draw(g);

				} else if (option == 3) {
					paintObject = new Oval(currentColor, point1, point2);
					paintObject.draw(g);

				} else if (option == 4) {
					paintObject = new Picture(currentColor, point1, point2);
					paintObject.draw(g);
				}
			}

		}

		// set up scrollPanel
		@Override
		public Dimension getPreferredScrollableViewportSize() {
			// TODO Auto-generated method stub
			return new Dimension(1030, 600);
		}

		@Override
		public int getScrollableUnitIncrement(java.awt.Rectangle visibleRect, int orientation, int direction) {
			// TODO Auto-generated method stub
			return 100;
		}

		@Override
		public int getScrollableBlockIncrement(java.awt.Rectangle visibleRect, int orientation, int direction) {
			// TODO Auto-generated method stub
			return 100;
		}

		@Override
		public boolean getScrollableTracksViewportWidth() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean getScrollableTracksViewportHeight() {
			// TODO Auto-generated method stub
			return false;
		}
	}

	/*
	 * Purpose: construct the listener for choosing shapes.
	 */
	private class ShapeListener implements ActionListener {

		/*
		 * Purpose: when passed in action, update option.
		 * 
		 * Parameter: ActionEvent e
		 * 
		 * Return type: none
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource().equals(line))
				option = 1;
			else if (e.getSource().equals(rectangle))
				option = 2;
			else if (e.getSource().equals(oval))
				option = 3;
			else
				option = 4;
		}

	}

	/*
	 * Purpose: construct the listener for choosing colors.
	 */
	private class ColorOptionListener implements ActionListener {
		/*
		 * Purpose: when passed in action, update color.
		 * 
		 * Parameter: ActionEvent e
		 * 
		 * Return type: none
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			Color c = JColorChooser.showDialog(null, "Choose a Color", currentColor);
			if (c != null) {
				currentColor = c;
			}
		}

	}

}
