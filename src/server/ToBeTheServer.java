package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import model.PaintObject;

/**
 * A Server side for Netpaint that stores all painted objects in different
 * clients. It communicates with ToBeTheClients.
 * 
 * Recommended: One team member work on drawing images with a mouse listener as
 * each new PaintObject is added to a Vector. Also need a "ghost" image that is
 * drawn as the mouse is dragged.
 * 
 * @author Yiling Ding and Mingjun Zhou
 */

public class ToBeTheServer implements Serializable {
	private static final int SERVER_PORT = 4002;

	private static ServerSocket sock;
	private static List<ObjectOutputStream> clients = Collections.synchronizedList(new ArrayList<>());
	private static Vector<PaintObject> allPaintObjects;

	/*
	 * Purpose: the main method of the server side.
	 * 
	 * Parameter: String[] args
	 * 
	 * Return type: none
	 */
	public static void main(String[] args) throws IOException {
		sock = new ServerSocket(SERVER_PORT);
		System.out.println("Server started on port " + SERVER_PORT);

		while (true) {
			// Accept a connection from the ServerSocket.
			Socket s = sock.accept();

			ObjectInputStream is = new ObjectInputStream(s.getInputStream());
			ObjectOutputStream os = new ObjectOutputStream(s.getOutputStream());
			// Add the output stream to our list of clients
			// so we can write to all clients output streams later.
			if (clients.size() == 0)
				allPaintObjects = new Vector<PaintObject>();
			clients.add(os);
			// allPaintObjects.add(new Picture(Color.black,new Point(20,20),new
			// Point(100,100)));
			// Start a new ClientHandler thread for this client.
			ClientHandler c = new ClientHandler(is, os, clients, allPaintObjects);
			c.start();

			System.out.println("Accepted a new connection from " + s.getInetAddress());
		}
	}

	/*
	 * Purpose: pass the server port number.
	 * 
	 * Parameter: none
	 * 
	 * Return type: int
	 */
	public static int getServerPort() {
		return SERVER_PORT;
	}

}

/*
 * Purpose: construct the handler that can handle different clients.
 */
class ClientHandler extends Thread {

	private ObjectInputStream input;
	private ObjectOutputStream output;
	private List<ObjectOutputStream> clients;
	private Vector<PaintObject> allPaintObjects;

	/*
	 * Purpose: constructor of ClientHandler.
	 * 
	 * Parameter: ObjectInputStream input, ObjectOutputStream
	 * output,List<ObjectOutputStream> clients, Vector<PaintObject>
	 * allPaintObjects
	 * 
	 * Return type: none
	 */
	public ClientHandler(ObjectInputStream input, ObjectOutputStream output, List<ObjectOutputStream> clients,
			Vector<PaintObject> allPaintObjects) {
		this.input = input;
		this.output = output;
		this.clients = clients;
		this.allPaintObjects = allPaintObjects;
	}

	/*
	 * Purpose: synchronize data to clients when avaliable.
	 * 
	 * Parameter: none
	 * 
	 * Return type: none
	 */
	@Override
	public void run() {
		try {
			output.reset();
			output.writeObject(allPaintObjects);
		} catch (IOException e2) {
			e2.printStackTrace();
		}

		while (true) {
			PaintObject paintObject;
			try {
				// Read a String from the client
				paintObject = (PaintObject) input.readObject();
				allPaintObjects.add(paintObject);

			} catch (IOException e) {
				/* Client left -- clean up and let the thread die */
				clients.remove(output);
				this.cleanUp();
				return;
			} catch (ClassNotFoundException e) {
				/* This one is probably a bug though */
				e.printStackTrace();
				this.cleanUp();
				return;
			}
			this.writeObjectToClients(paintObject);
		}
	}

	/*
	 * Purpose: write objects to all clients when one of them updated.
	 * 
	 * Parameter: PaintObject paintObjects
	 * 
	 * Return type: none
	 */
	private void writeObjectToClients(PaintObject paintObjects) {
		synchronized (clients) {
			// Use an enhanced for loop to Send a string to all clients
			// the client list by iterating over all ObjectOutputStreams in
			// clients
			for (ObjectOutputStream client : clients) {
				try {
					client.writeObject(paintObjects);
					// After writeObject, do NOT forget reset(). This would be
					// a silent error that results in the server not working.
					client.reset();

					// In the catch block, remove the ObjectOutputStream
					// from the list (ArrayList has remove(E element))
				} catch (IOException e) {
					// If we can't write to the client, their socket was
					// closed. Therefore, remove it from the list.
					clients.remove(client);
				}
			}
		}
	}

	// Write a method that closes all the
	// resources of a ClientHandler and logs a message, and call it from every
	// place that a fatal error occurs in ClientHandler (the catch blocks that
	// you can't recover from).
	private void cleanUp() {
		// Don't forget to close those sockets. Not an issue here, but you WILL
		// run out eventually if you neglect this.
		try {
			this.input.close();
		} catch (IOException e) {
			e.getStackTrace();
		}
	}
}
