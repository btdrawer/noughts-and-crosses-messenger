package server;

import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.regex.Pattern;

import protocol.Constants;
import protocol.Protocol;

/**
 * 
 * @author Ben Drawer
 * @version 21 October 2018
 *
 */
class ProfileActions {
	private String[] outArr;
	private Socket clientSocket;
	private Protocol protocol;
	private static Map<String, Socket> sockets = Main.getSockets();
	private static final String SIGNED_IN = Constants.SIGNED_IN,
			CHANGED_USERNAME = Constants.CHANGED_USERNAME,
			SIGNED_OUT = Constants.SIGNED_OUT,
			SIGN_UP = Constants.SIGN_UP,
			SIGN_IN = Constants.SIGN_IN,
			SIGN_OUT = Constants.SIGN_OUT,
			FORGOT_PASSWORD_REQUEST = Constants.FORGOT_PASSWORD_REQUEST,
			FORGOT_PASSWORD_ANSWER = Constants.FORGOT_PASSWORD_ANSWER,
			FORGOT_PASSWORD_CHANGE = Constants.FORGOT_PASSWORD_CHANGE,
			VIEW_PROFILE = Constants.VIEW_PROFILE,
			EDIT_PROFILE = Constants.EDIT_PROFILE,
			ONLINE = Constants.ONLINE,
			OFFLINE = Constants.OFFLINE;
	private boolean result;
	private String message;
	
	/**
	 * Constructor.
	 * 
	 * @param clientSocket
	 */
	ProfileActions(Socket clientSocket) {
		this.clientSocket = clientSocket;
		this.protocol = Main.getProtocol();
		this.outArr = new String[2];
	}
	
	/**
	 * Sign-up method.
	 * 
	 * @param input [0] = username; [1] = password; [2] = security question; [3] = security answer
	 * @return output indicating whether sign-up has been successful
	 * @throws NoSuchAlgorithmException if password hashing algorithm unavailable
	 */
	String signup(String[] input) throws NoSuchAlgorithmException {
		String username = input[0], password = input[1], securityQ = input[2],
				securityA = input[3];
		int usernameLength = input[0].length();
		
		//TODO regex pattern for password
		Pattern p = Pattern.compile("[a-zA-Z0-9]+");
		
		outArr = new String[2];
		result = false;
		
		if (Database.usernameExists(username))
			message = "Sorry that username has been taken.";
		else if (username.equals(p.toString()))
			message = "Please ensure your username has only alphabetic or numeric characters.";
		else if (usernameLength > 12)
			message = "Your username cannot be more than 12 characters long.";
		else if (usernameLength == 0)
			message = "Your username cannot be empty!";
		else {
			boolean signUp = Database.signUp(username, password, securityQ, securityA);
		
			if (signUp) {
				String[] notifyOnlineUsers = {username};
				sockets.put(username, clientSocket);
				Main.broadcastMessage(SIGNED_IN, notifyOnlineUsers);
				
				result = true;
				message = "Sign-up successful. Welcome!";
			} else 
				message = "An error occurred; please try again later.";
		}
		
		return protocol.transmit(SIGN_UP, result, message);
	}
	
	/**
	 * Sign-in method.
	 * 
	 * @param input username and password
	 * @return output indicating whether sign-in has been successful
	 * @throws NoSuchAlgorithmException
	 */
	String signin(String[] input) throws NoSuchAlgorithmException {
		String username = input[0];
		String password = input[1];
		
		outArr = new String[2];
		
		if (!Database.signIn(username, password)) {
			result = false;
			message = "Your username and/or password were incorrect.";
		} else if (Database.isOnline(username)) {
			result = false;
			message = "An error occurred. Please try again later.";
		} else {
			Database.setStatus(username, ONLINE);
			sockets.put(username, clientSocket);
			
			String[] notifyOnlineUsers = {username};
			Main.broadcastMessage(SIGNED_IN, notifyOnlineUsers);
			
			result = true;
			message = "Welcome back!";
		}
		
		return protocol.transmit(SIGN_IN, result, message);
	}
	
	/**
	 * Forgot password, step 1.
	 * Checks if username exists.
	 * 
	 * @param input username
	 * @return output indicating whether username has been found
	 */
	String forgotPasswordRequest(String[] input) {
		String username = input[0];
		outArr = new String[2];
		outArr[0] = username;
		
		String question = Database.forgotPasswordRequest(username);
		
		if (question != null) {
			result = true;
			outArr[1] = question;
		} else {
			result = false;
			outArr[1] = "Username not found.";
		}
		
		return protocol.transmit(FORGOT_PASSWORD_REQUEST, result, outArr);
	}
	
	/**
	 * Forgot password, step 2.
	 * Checks if the user's answer matches their records.
	 * 
	 * @param input [0] = username; [1] = answer
	 * @return output indicating whether the user has successfully answered the security question
	 * @throws NoSuchAlgorithmException 
	 */
	String forgotPassword(String[] input) throws NoSuchAlgorithmException {
		result = Database.forgotPasswordAnswer(input[0], input[1]);
		
		if (result)
			message = "Enter your new password:";
		else
			message = "Sorry, the answer you gave did not match our records.";
		
		return protocol.transmit(FORGOT_PASSWORD_ANSWER, result, message);
	}
	
	String forgotPasswordChange(String[] input) {
		result = Database.forgotPasswordChange(input[0], input[1]);
		
		if (result)
			message = "Password successfully reset!";
		else
			message = "An error occurred. Please try again later.";
		
		return protocol.transmit(FORGOT_PASSWORD_CHANGE, result, message);
	}
	
	/**
	 * Creates a String of details of a user's profile.
	 * 
	 * @param input username String
	 * @return String with user's details
	 */
	String viewProfile(String input) {
		Profile p = Database.getProfile(input);
		
		String[] outArr = {p.getUsername(), p.getStatus() + "", 
				p.getWins() + "", p.getLosses() + "", p.getTotal() + ""};
		
		return protocol.transmit(VIEW_PROFILE, true, outArr);
	}
	
	/**
	 * Processes a request to change a user's username and/or password.
	 * 
	 * @param input
	 * @return output indicating whether changes have been successful
	 * @throws NoSuchAlgorithmException
	 */
	String changes(String[] input) throws NoSuchAlgorithmException {
		String currentUsername = input[0];
		String newUsername = input[1];
		String password = input[2];
		String newPassword = input[3];
		boolean usernameToChange = false, passwordToChange = false, error = false;
		
		outArr = new String[2];
		outArr[1] = ".";
		
		if (!newUsername.equals(".")) {
			if (Database.usernameExists(newUsername)) {
				result = false;
				outArr[0] = "Someone already has that username.";
				error = true;
			} else
				usernameToChange = true;
		}
		
		if (!newPassword.equals("."))
			passwordToChange = true;
		
		if (Database.signIn(currentUsername, password)) {
			if (!error) {
				result = true;
				boolean changesMade = false;
				
				//TODO how to update things using the Writer
				if (usernameToChange && passwordToChange) {
					changesMade = Database.changeProfileDetails(currentUsername, newUsername, newPassword);
					
					String[] notifyOnlineUsers = {currentUsername, newUsername};
					Main.broadcastMessage(CHANGED_USERNAME, notifyOnlineUsers);
					
					outArr[0] = "Your username and password have been successfully changed.";
					outArr[1] = newUsername;
				} else if (usernameToChange) {
					changesMade = Database.changeProfileDetails(currentUsername, newUsername, password);
					
					String[] notifyOnlineUsers = {currentUsername, newUsername};
					Main.broadcastMessage(CHANGED_USERNAME, notifyOnlineUsers);
					
					outArr[0] = "Your username has been successfully changed.";
					outArr[1] = newUsername;
				} else if (passwordToChange) {
					changesMade = Database.changeProfileDetails(currentUsername, currentUsername, newPassword);
					
					outArr[0] = "Your password has been successfully changed.";
				}
				
				if (!changesMade) {
					result = false;
					outArr[0] = "An error occurred. Please try again later.";
				}
			}
		} else {
			result = false;
			outArr[0] = "Your password was incorrect.";
		}
		
		return protocol.transmit(EDIT_PROFILE, result, outArr);
	}
	
	/**
	 * Signs the user out.
	 * 
	 * @param input [0] = username
	 * @return output indicating that the sign out has been successful
	 */
	String signout(String[] input, boolean leftServer) {
		String username = input[0];
		
		boolean signedOut = Database.setStatus(username, OFFLINE);
		
		if (signedOut) {
			String[] notifyOnlineUsers = {username};
			Main.broadcastMessage(SIGNED_OUT, notifyOnlineUsers);
			
			if (leftServer)
				Main.leftServer();
			
			return protocol.transmit(SIGN_OUT, true, "Signed out. See you soon!");
		} else {
			return protocol.transmit(SIGN_OUT, false, "An error occurred. Please try again later.");
		}
	}
}
