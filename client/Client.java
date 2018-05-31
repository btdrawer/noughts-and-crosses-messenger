package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import protocol.Protocol;

/**
 * Client class.
 * Sends messages to the server and receives responses from it.
 * 
 * @author Ben Drawer
 *
 */
public class Client {
	private String host;
	private int port;
	private Socket clientSocket;
	private BufferedReader in;
	private PrintWriter out;
	private Protocol protocol;
	
	/**
	 * Constructor.
	 * 
	 * @param host
	 * @param port
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public Client(String host, int port) throws UnknownHostException, IOException {
		this.host = host;
		this.port = port;
		
		clientSocket = new Socket(host, port);
		in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		out = new PrintWriter(clientSocket.getOutputStream(), true);
		protocol = new Protocol();
	}
	
	/**
	 * Empty constructor.
	 * 
	 * @throws IOException
	 */
	public Client() throws IOException {
		clientSocket = new Socket();
		in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		out = new PrintWriter(clientSocket.getOutputStream(), true);
		protocol = new Protocol();
	}
	
	/**
	 * 
	 * @return response from server
	 * @throws IOException
	 */
	public String[] getResponse() throws IOException {
		return protocol.receive(in.readLine());
	}
	
	/**
	 * Sets the IP address the client listens on.
	 * 
	 * @param host new IP address
	 */
	public void setHost(String host) {
		this.host = host;
	}
	
	/**
	 * Sets the port number the client listens on.
	 * 
	 * @param port new port number
	 */
	public void setPort(int port) {
		this.port = port;
	}
	
	/**
	 * Tests the connection
	 * 
	 * @return response from server
	 * @throws IOException
	 */
	public String[] connect() throws IOException {
		String[] outArr = {};
		out.write(protocol.transmit("connect", outArr));
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
		out.write(protocol.transmit("signup", outArr));
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
		out.write(protocol.transmit("signin", outArr));
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
		out.write(protocol.transmit("forgot", outArr));
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
		out.write(protocol.transmit("newgame", outArr));
		return this.getResponse();
	}
}
