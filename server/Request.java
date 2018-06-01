package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.Map;
import java.util.regex.Pattern;

import protocol.Protocol;

/**
 * This class handles a request.
 * 
 * @author Ben Drawer
 * @version 1 June 2018
 *
 */
class Request implements Task {
	private Socket clientSocket;
	private Protocol protocol;
	private String output;
	private String[] outArr;
	private static Map<String, Profile> users = Server.getUsers();
	private static Map<String, LinkedList<Game>> games = Server.getGames();
	private static Map<Short, String> securityQuestions = Server.getSecurityQuestions();
	
	/**
	 * Constructor.
	 * 
	 * @param clientSocket connecting socket
	 */
	Request(Socket clientSocket) {
		this.clientSocket = clientSocket;
		this.protocol = new Protocol();
		this.outArr = new String[2];
	}
	
	/**
	 * Tests the connection.
	 * 
	 * @return output stating that the connection has been successful
	 */
	private String connect() {
		outArr[0] = "true";
		outArr[1] = "Connection test successful.";
		
		return protocol.transmit("connect", outArr);
	}
	
	/**
	 * Sign-up method.
	 * 
	 * @param input [0] = username; [1] = password; [2] = security question; [3] = security answer
	 * @return output indicating whether sign-up has been successful
	 * @throws NoSuchAlgorithmException if password hashing algorithm unavailable
	 */
	private String signup(String[] input) throws NoSuchAlgorithmException {
		String username = input[0];
		String password = input[1];
		short securityQ = Short.parseShort(input[2]);
		String securityA = input[3];
		
		int usernameLength = input[0].length();
		int passwordLength = input[1].length();
		
		//TODO regex pattern for password
		Pattern p = Pattern.compile("[a-zA-Z0-9]+");
		
		if (users.containsKey(username)) {
			outArr[0] = "false";
			outArr[1] = "Sorry that username has been taken.";
		} else if (username.equals(p.toString())) {
			outArr[0] = "false";
			outArr[1] = "Please ensure your username has only alphabetic or numeric characters.";
		} else if (usernameLength > 12) {
			outArr[0] = "false";
			outArr[1] = "Your username cannot be more than 12 characters long.";
		} else if (usernameLength == 0) {
			outArr[0] = "false";
			outArr[1] = "Your username cannot be empty!";
		} else if (passwordLength < 6 || passwordLength > 15) {
			outArr[0] = "false";
			outArr[1] = "Your password must be between 6 and 15 characters long.";
		} else {
			Profile newUser = new Profile(username, getMD5(password), securityQ, securityA);
			users.put(username, newUser);
			
			outArr[0] = "true";
			outArr[1] = "Sign-up successful. Welcome!";
		}
		
		return protocol.transmit("signup", outArr);
	}
	
	/**
	 * Password hasher.
	 * 
	 * @param input password
	 * @return MD5 password hash
	 * @throws NoSuchAlgorithmException if hashing algorithm is unavailable
	 */
	static byte[] getMD5(String input) 
			throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.reset();
		md.update(input.getBytes());
		
		return md.digest();
	}
	
	/**
	 * Sign-in method.
	 * 
	 * @param input username and password
	 * @return output indicating whether sign-in has been successful
	 * @throws NoSuchAlgorithmException
	 */
	private String signin(String[] input) throws NoSuchAlgorithmException {
		String username = input[0];
		String password = input[1];
		
		if (!users.containsKey(username)) {
			outArr[0] = "false";
		} else if (!(users.get(username).getPassword() == getMD5(password))) {
			outArr[0] = "false";
		} else {
			users.get(username).leftGame();
			
			outArr[0] = "true";
			outArr[1] = "Welcome back!";
		}
		
		if (outArr[0].equals("false")) {
			outArr[1] = "Your username and/or password were incorrect.";
		}
		
		return protocol.transmit("signin", outArr);
	}
	
	/**
	 * Forgot password, step 1.
	 * Checks if username exists.
	 * 
	 * @param input username
	 * @return output indicating whether username has been found
	 */
	private String forgotPasswordRequest(String[] input) {
		if (users.containsKey(input[0])) {
			outArr[0] = "true";
			outArr[1] = securityQuestions.get(users.get(input[0]).getSecurityQuestion());
		} else {
			outArr[0] = "false";
			outArr[1] = "Username not found.";
		}
		
		return protocol.transmit("forgot", outArr);
	}
	
	/**
	 * Forgot password, step 2.
	 * Checks if the user's answer matches their records.
	 * 
	 * @param input [0] = username; [1] = answer
	 * @return output indicating whether the user has successfully answered the security question
	 */
	private String forgotPassword(String[] input) {
		if (users.get(input[0]).getSecurityAnswer().equals(input[1])) {
			outArr[0] = "true";
			outArr[1] = "Enter your new password:";
		} else {
			outArr[0] = "false";
			outArr[1] = "Sorry, the answer you gave did not match our records.";
		}
		
		return protocol.transmit("forgot", outArr);
	}
	
	/**
	 * Builds a String of online users.
	 * 
	 * @return output containing information of all online users
	 */
	private String requestUsers() {
		StringBuilder sb = new StringBuilder();
		
		for (Profile p : users.values()) {
			sb.append(p.toString());
		}
		
		return protocol.transmit("requestusers", sb.toString());
	}
	
	/**
	 * Creates a new game, first checking that both users are available.
	 * 
	 * @param input users who want to play
	 * @return output indicating whether the new game has been successfully initiated
	 */
	private String newGame(String[] input) {
		if (!(users.get(input[0]).isAvailable()) && 
				users.get(input[1]).isAvailable()) {
			outArr[0] = "false";
			outArr[1] = "This user isn't available.";
		} else {
			String key = input[0] + "/" + input[1];
			
			if (!games.containsKey(key)) {
				games.put(key, new LinkedList<>());
			}
			
			if (input[2] == "true") {
				games.get(key).add(new TimedGame(input[0], input[1]));
			} else {
				games.get(key).add(new Game(input[0], input[1]));
			}
			
			outArr[0] = "true";
		}
		
		return protocol.transmit("newgame", outArr);
	}
	
	/**
	 * Add a character to a game.
	 * 
	 * @param input [0] = position on board; [1] = o or x
	 * @return output indicating whether character input has been successful
	 */
	private String addChar(String[] input) {
		Game currentGame = games.get(input[0] + "/" + input[1]).getLast();
		char[] board = currentGame.getBoard();
		int position = Integer.parseInt(input[2]) - 1;
		
		if (!(board[position] == ' ')) {
			outArr[0] = "false";
			outArr[1] = "Space already taken";
		} else {
			currentGame.addChar(position, input[3].charAt(0));
			
			outArr[0] = "true";
		}
		
		return protocol.transmit("addchar", outArr);
	}
	
	/**
	 * Run method.
	 * This runs in a continuous loop, listening on the client socket and calling
	 * the appropriate methods accordingly.
	 */
	public synchronized void run() {
		try {
			BufferedReader in = new BufferedReader(new 
					InputStreamReader(clientSocket.getInputStream()));
			PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
			
			System.out.println("Connection with client established");
			
			while(true) {
				String s, action;
				String[] input;
				
				while((s = in.readLine()) != null) {
					System.out.println("Input: " + s);
					
					action = protocol.getAction(s);
					input = protocol.receive(s);
					
					if (action.equals("connect")) {
						output = connect();
					} else if (action.equals("signup")) {
						output = signup(input);
					} else if (action.equals("signin")) {
						output = signin(input);
					} else if (action.equals("forgotReq")) {
						output = forgotPasswordRequest(input);
					} else if (action.equals("forgot")) {
						output = forgotPassword(input);
					} else if (action.equals("requestusers")) {
						output = requestUsers();
					} else if (action.equals("newgame")) {
						output = newGame(input);
					} else if (action.equals("addchar")) {
						output = addChar(input);
					}
					
					System.out.println("Output: " + output);
					
					out.println(output);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
}
