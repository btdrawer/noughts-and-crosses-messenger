package client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Scanner;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
		out.writeBytes(protocol.transmit(action, input));
	}
	
	/**
	 * Main method.
	 * Used for testing.
	 * 
	 * 3 messages are sent by default: a connect, signin, and signup;
	 * after this, a Scanner launches, where the first line is the action
	 * and the second line is related information, separated by /.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Client c = new Client();
		
		c.setHost("127.0.0.1");
		c.setPort(8080);
		
		try {
			c.initialize();
			
			String[] outArr = {};
			c.sendMessage("connect", outArr);
			
			String[] out1 = {"ben", "password"};
			c.sendMessage("signin", out1);
			
			String[] out2 = {"ben", "password", 0 + "", "yeah"};
			c.sendMessage("signup", out2);
			
			Scanner s = new Scanner(System.in);
			
			while (s.hasNext()) {
				String action = s.nextLine();
				String[] arr = s.nextLine().split("/");
				c.sendMessage(action, arr);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
