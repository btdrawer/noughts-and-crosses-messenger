package client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

import protocol.Protocol;

/**
 * Client class.
 * Sends messages to the server and receives responses from it.
 * 
 * @author Ben Drawer
 * @version 1 June 2018
 *
 */
public class Client {
	private Socket clientSocket;
	private BufferedReader in;
	private DataOutputStream out;
	private Protocol protocol;
	private String username;
	
	/**
	 * Constructor.
	 * 
	 * @param host
	 * @param port
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public Client(String host, int port) throws UnknownHostException, IOException {
		clientSocket = new Socket(host, port);
		in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		out = new DataOutputStream(clientSocket.getOutputStream());
		protocol = new Protocol();
	}
	
	/**
	 * 
	 * @return username of current user
	 */
	public String getUsername() {
		return username;
	}
	
	/**
	 * 
	 * @param username new username
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	
	/**
	 * 
	 * @return response from server
	 * @throws IOException
	 */
	public String[] getResponse() throws IOException {
		String s = in.readLine();
		System.out.println("Input: " + s);
		return protocol.receive(s);
	}
	
	/**
	 * Tests the connection
	 * 
	 * @return response from server
	 * @throws IOException
	 */
	public String[] connect() throws IOException {
		String[] outArr = {};
		out.writeBytes(protocol.transmit("connect", outArr));
		return this.getResponse();
	}
	
	/**
	 * Sign-up method.
	 * 
	 * @param username
	 * @param password
	 * @param securityQ number identifying security question
	 * @param securityA security answer
	 * @return response from server
	 * @throws IOException
	 */
	public String[] signup(String username, String password, int securityQ, 
			String securityA) throws IOException {
		String[] outArr = {username, password, securityQ + "", securityA};
		out.writeBytes(protocol.transmit("signup", outArr));
		return this.getResponse();
	}
	
	/**
	 * Sign-in method.
	 * 
	 * @param username
	 * @param password
	 * @return response from server
	 * @throws IOException
	 */
	public String[] signin(String username, String password) throws IOException {
		String[] outArr = {username, password};
		out.writeBytes(protocol.transmit("signin", outArr));
		return this.getResponse();
	}
	
	/**
	 * Forgot password method.
	 * 
	 * @param username
	 * @param securityQ number identifying security question
	 * @param securityA security answer
	 * @return response from server
	 * @throws IOException
	 */
	public String[] forgotPassword(String username, int securityQ, String securityA) 
			throws IOException {
		String[] outArr = {username, securityQ + "", securityA};
		out.writeBytes(protocol.transmit("forgot", outArr));
		return this.getResponse();
	}
	
	/**
	 * New game method.
	 * 
	 * @param opponent
	 * @param timed
	 * @return response from server
	 * @throws IOException
	 */
	public String[] newGame(String opponent, boolean timed) throws IOException {
		String[] outArr = {opponent, timed + ""};
		out.writeBytes(protocol.transmit("newgame", outArr));
		return this.getResponse();
	}
	
	/**
	 * Sign out method.
	 * 
	 * @param username
	 * @return response from server
	 * @throws IOException
	 */
	public String[] signOut(String username) throws IOException {
		String[] outArr = {username};
		out.writeBytes(protocol.transmit("signout", outArr));
		return this.getResponse();
	}
	
	/**
	 * Main method.
	 * Use for testing communications with the server.
	 * 
	 * @param args
	 * @throws IOException 
	 * @throws UnknownHostException 
	 */
	public static void main(String[] args) throws UnknownHostException, IOException {
		Client client = new Client("127.0.0.1", 8080);
		String[] input;
		
		input = client.connect();
		System.out.println("Connect" + "/" + input.length + "/" + input[0] + "/" + input[1]);
		
		input = client.signin("a", "asdf");
		System.out.println("Sign-in" + "/" + input.length + "/" + input[0] + "/" + input[1]);
	}
}
