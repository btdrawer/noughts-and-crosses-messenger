package client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import protocol.Protocol;

/**
 * Client class.
 * Manages Socket connection to server, and data input and output streams.
 * 
 * @author Ben Drawer
 * @version 21 June 2018
 *
 */
class Client {
	private Socket clientSocket;
	private BufferedReader in;
	private DataOutputStream out;
	private Protocol protocol;
	private String username;
	private String host;
	private int port;
	private Listener listener;
	private String[] input;
	
	/**
	 * Initialize method.
	 * Initialises the Socket, BufferedReader, DataOutputStream and Protocol so that the
	 * client can communicate with the server.
	 * Also starts a Listener thread so that the client is always listening for
	 * messages.
	 * 
	 * @throws IOException
	 */
	public void initialize() throws IOException {
		this.clientSocket = new Socket(host, port);
		this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		this.out = new DataOutputStream(clientSocket.getOutputStream());
		this.protocol = new Protocol();
		
		this.listener = new Listener(this);
		listener.start();
	}
	
	/**
	 * 
	 * @return response from server
	 */
	String[] getResponse() {
		return input;
	}
	
	/**
	 * 
	 * @return IP address of server
	 */
	String getHost() {
		return host;
	}
	
	/**
	 * 
	 * @return port number client is listening on
	 */
	int getPort() {
		return port;
	}
	
	/**
	 * Sets the IP address to listen on.
	 * 
	 * @param host new IP address
	 */
	void setHost(String newHost) {
		host = newHost;
	}
	
	/**
	 * Sets the port number to listen on.
	 * 
	 * @param port new port number
	 */
	void setPort(int newPort) {
		port = newPort;
	}
	
	/**
	 * 
	 * @return client's BufferedReader
	 */
	BufferedReader getBufferedReader() {
		return in;
	}
	
	/**
	 * 
	 * @return client's DataOutputStream
	 */
	DataOutputStream getDataOutputStream() {
		return out;
	}
	
	/**
	 * 
	 * @return socket client is listening on
	 */
	Socket getClientSocket() {
		return clientSocket;
	}
	
	/**
	 * 
	 * @return username of current user
	 */
	String getUsername() {
		return username;
	}
	
	/**
	 * Sets the username of the current client
	 * 
	 * @param username new username
	 */
	void setUsername(String username) {
		this.username = username;
	}
	
	/**
	 * 
	 * @return Listener class
	 */
	Listener getListener() {
		return listener;
	}
	
	/**
	 * 
	 * @return protocol
	 */
	Protocol getProtocol() {
		return protocol;
	}
	
	/**
	 * Sends a message to the server and returns the response.
	 * 
	 * @param action action to be undertaken - e.g., 'signup', 'signin', etc.
	 * @param input information associated with action
	 * @throws IOException
	 */
	void sendMessage(String action, String[] input) throws IOException {
		String toSend = protocol.transmit(action, input);
		System.out.println("Output: " + toSend);
		out.writeBytes(toSend);
	}
	
	/**
	 * Sends a message to the server and returns the response.
	 * 
	 * @param action action to be undertaken - e.g., 'signup', 'signin', etc.
	 * @param input information associated with action
	 * @throws IOException
	 */
	void sendMessage(String action, String input) throws IOException {
		String toSend = protocol.transmit(action, input);
		System.out.println("Output: " + toSend);
		out.writeBytes(toSend);
	}
}
