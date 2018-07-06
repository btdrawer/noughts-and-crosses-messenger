package client;

import java.io.BufferedReader;
import java.io.IOException;

import protocol.Protocol;

/**
 * This class creates a thread that listens for messages from the server.
 * 
 * @author Ben Drawer
 * @version 21 June 2018
 *
 */
class Listener extends Thread {
	private Client client;
	private String[] input;
	private Protocol protocol;
	private Controller controller;
	
	/**
	 * Constructor.
	 * 
	 * @param controller controller class for the client
	 */
	Listener(Client client) {
		this.client = client;
		this.protocol = client.getProtocol();
	}
	
	/**
	 * Sets the controller that the Listener sends input to.
	 * 
	 * @param controller
	 */
	void setController(Controller controller) {
		this.controller = controller;
	}
	
	/**
	 * Run method.
	 */
	@Override
	public void run() {
		String s;
		BufferedReader in = client.getBufferedReader();
		
		try {
			while((s = in.readLine()) != null) {
				System.out.println("Input: " + s);
				
				String action = protocol.getAction(s);
				input = protocol.receive(s);
				
				controller.processInput(action, input);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				//Signs the user out if they close the application
				//TODO currently not working
				System.out.println("Signout");
				client.sendMessage("signout", client.getUsername());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
