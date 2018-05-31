package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import protocol.Protocol;

/**
 * This class handles a request.
 * 
 * @author Ben Drawer
 * @version 25 May 2018
 *
 */
class Request implements Task {
	private Socket clientSocket;
	private Protocol protocol;
	private String success, message, output;
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
		
		outArr = new String[2];
		outArr[0] = success;
		outArr[1] = message;
	}
	
	/**
	 * Sign-up method.
	 * 
	 * @param input [0] = username; [1] = password; [2] = security question; [3] = security answer
	 * @throws NoSuchAlgorithmException if password hashing algorithm unavailable
	 */
	private void signup(String[] input) throws NoSuchAlgorithmException {
		String username = input[0];
		String password = input[1];
		short securityQ = Short.parseShort(input[2]);
		String securityA = input[3];
		
		int usernameLength = input[0].length();
		int passwordLength = input[1].length();
		
		//TODO regex pattern for password
		Pattern p = Pattern.compile("[a-zA-Z0-9]+");
		
		if (users.containsKey(username)) {
			success = "false";
			message = "Sorry that username has been taken.";
		} else if (username.equals(p.toString())) {
			success = "false";
			message = "Please ensure your username has only alphabetic or numeric characters.";
		} else if (usernameLength > 12) {
			success = "false";
			message = "Your username cannot be more than 12 characters long.";
		} else if (usernameLength == 0) {
			success = "false";
			message = "Your username cannot be empty!";
		} else if (passwordLength < 6 || passwordLength > 15) {
			success = "false";
			message = "Your password must be between 6 and 15 characters long.";
		} else {
			Profile newUser = new Profile(username, getMD5(password), securityQ, securityA);
			users.put(username, newUser);
			
			success = "true";
			message = "Sign-up successful. Welcome!";
		}
		
		output = protocol.transmit("signup", outArr);
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
	
	private void signin(String[] input) throws NoSuchAlgorithmException {
		String username = input[0];
		String password = input[1];
		
		if (!users.containsKey(username)) {
			success = "false";
		} else if (!(users.get(username).getPassword() == getMD5(password))) {
			success = "false";
		} else {
			users.get(username).leftGame();
			
			success = "true";
			message = "Welcome back!";
		}
		
		if (success.equals("false")) {
			message = "Your username and/or password were incorrect.";
		}
		
		output = protocol.transmit("signin", outArr);
	}
	
	/**
	 * Forgot password, step 1.
	 * Checks if username exists.
	 */
	private void forgotPasswordRequest(String[] input) {
		if (users.containsKey(input[0])) {
			success = "true";
			message = securityQuestions.get(users.get(input[0]).getSecurityQuestion());
		} else {
			success = "false";
			message = "Username not found.";
		}
		
		output = protocol.transmit("forgot", outArr);
	}
	
	/**
	 * Forgot password, step 2.
	 * Checks if the user's answer matches their records.
	 * 
	 * @param input [0] = username; [1] = answer
	 */
	private void forgotPassword(String[] input) {
		if (users.get(input[0]).getSecurityAnswer().equals(input[1])) {
			success = "true";
			message = "Enter your new password:";
		} else {
			success = "false";
			message = "Sorry, the answer you gave did not match our records.";
		}
		
		output = protocol.transmit("forgot", outArr);
	}
	
	/**
	 * Builds a String of online users.
	 */
	private void requestUsers() {
		StringBuilder sb = new StringBuilder();
		
		for (Profile p : users.values()) {
			sb.append(p.toString());
		}
		
		output = protocol.transmit("requestusers", sb.toString());
	}
	
	/**
	 * Creates a new game, first checking that both users are available.
	 * 
	 * @param input users who want to play
	 */
	private void newGame(String[] input) {
		if (!(users.get(input[0]).isAvailable()) && 
				users.get(input[1]).isAvailable()) {
			success = "false";
			message = "This user isn't available.";
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
			
			success = "true";
		}
		
		output = protocol.transmit("newgame", outArr);
	}
	
	/**
	 * Add a character to a game.
	 * 
	 * @param input [0] = position on board; [1] = o or x
	 */
	private void addChar(String[] input) {
		Game currentGame = games.get(input[0] + "/" + input[1]).getLast();
		char[] board = currentGame.getBoard();
		int position = Integer.parseInt(input[2]) - 1;
		
		if (!(board[position] == ' ')) {
			success = "false";
			message = "Space already taken";
		} else {
			currentGame.addChar(position, input[3].charAt(0));
			
			success = "true";
		}
		
		output = protocol.transmit("addchar", outArr);
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
					action = protocol.getAction(s);
					input = protocol.receive(s);
					
					if (action.equals("connect")) {
						success = "true";
					} else if (action.equals("signup")) {
						signup(input);
					} else if (action.equals("signin")) {
						signin(input);
					} else if (action.equals("forgotReq")) {
						forgotPasswordRequest(input);
					} else if (action.equals("forgot")) {
						forgotPassword(input);
					} else if (action.equals("requestusers")) {
						requestUsers();
					} else if (action.equals("newgame")) {
						newGame(input);
					} else if (action.equals("addchar")) {
						addChar(input);
					}
					
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
