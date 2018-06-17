package client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import javafx.collections.ObservableList;
import protocol.Protocol;

/**
 * Main controller class; superclass for all other controllers.
 * 
 * @author Ben Drawer
 * @version 17 June 2018
 *
 */
class Controller {
	private Socket clientSocket;
	private BufferedReader in;
	private DataOutputStream out;
	private Protocol protocol;
	private String username;
	private static String host;
	private static int port;
	private ObservableList<String> onlineUserList;
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
	static String getHost() {
		return host;
	}
	
	/**
	 * 
	 * @return port number client is listening on
	 */
	static int getPort() {
		return port;
	}
	
	/**
	 * Sets the IP address to listen on.
	 * 
	 * @param host new IP address
	 */
	static void setHost(String newHost) {
		host = newHost;
	}
	
	/**
	 * Sets the port number to listen on.
	 * 
	 * @param port new port number
	 */
	static void setPort(int newPort) {
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
		out.writeBytes(protocol.transmit(action, input));
	}
	
	/**
	 * Processes input from the server.
	 * 
	 * @param action action to be undertaken - e.g., 'signup', 'signin', etc.
	 * @param input information associated with action
	 */
	void processInput(String action, String[] input) {
		if (action.equals("signedin"))
			this.addToOnlineUserList(input[0]);
		if (action.equals("signedout"))
			this.removeFromOnlineUserList(input[0]);
	}
	
	/**
	 * Adds a user to the list of online users.
	 * Use when a new user signs in.
	 * 
	 * @param username user to be added
	 */
	void addToOnlineUserList(String username) {
		onlineUserList.add(username);
	}
	
	/**
	 * Removes a user from the list of online users.
	 * Use when a user signs out.
	 * 
	 * @param username user to be removed
	 */
	void removeFromOnlineUserList(String username) {
		onlineUserList.remove(username);
	}
	
	/**
	 * Main method.
	 * Used for testing.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Controller c = new Controller();
		
		Controller.setHost("127.0.0.1");
		Controller.setPort(8080);
		
		try {
			c.initialize();
			
			String[] outArr = {};
			c.sendMessage("connect", outArr);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
