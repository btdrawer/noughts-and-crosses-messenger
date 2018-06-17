package client;

import java.io.BufferedReader;
import java.io.IOException;

import protocol.Protocol;

/**
 * This class creates a thread that listens for messages from the server.
 * 
 * @author Ben Drawer
 * @version 17 June 2018
 *
 */
class Listener extends Thread {
	private Controller controller;
	private String[] input;
	private Protocol protocol;
	
	/**
	 * Constructor.
	 * 
	 * @param controller controller class for the client
	 */
	Listener(Controller controller) {
		this.controller = controller;
		this.protocol = controller.getProtocol();
	}
	
	/**
	 * 
	 * @return message from server
	 */
	String[] getResponse() {
		return input;
	}
	
	/**
	 * Run method.
	 */
	@Override
	public void run() {
		String s;
		BufferedReader in = controller.getBufferedReader();
		
		try {
			while((s = in.readLine()) != null) {
				System.out.println("Input: " + s);
				
				String action = protocol.getAction(s);
				input = protocol.receive(s);
				
				controller.processInput(action, input);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
