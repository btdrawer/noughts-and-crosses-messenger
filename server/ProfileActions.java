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
 * @version 20 September 2018
 *
 */
class ProfileActions {
	private String[] outArr;
	private Socket clientSocket;
	private Protocol protocol;
	private static Map<String, Socket> sockets = Main.getSockets();
	private static final String SIGNED_IN = Constants.SIGNED_IN,
			SIGNED_OUT = Constants.SIGNED_OUT,
			SIGN_UP = Constants.SIGN_UP,
			SIGN_IN = Constants.SIGN_IN,
			SIGN_OUT = Constants.SIGN_OUT,
			FORGOT_PASSWORD_REQUEST = Constants.FORGOT_PASSWORD_REQUEST,
			FORGOT_PASSWORD_ANSWER = Constants.FORGOT_PASSWORD_ANSWER,
			VIEW_PROFILE = Constants.VIEW_PROFILE,
			EDIT_PROFILE = Constants.EDIT_PROFILE,
			TRUE = Constants.TRUE,
			FALSE = Constants.FALSE,
			ONLINE = Constants.ONLINE,
			OFFLINE = Constants.OFFLINE;
	
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
		int usernameLength = input[0].length(), passwordLength = input[1].length();
		
		//TODO regex pattern for password
		Pattern p = Pattern.compile("[a-zA-Z0-9]+");
		
		outArr = new String[2];
		
		if (Database.usernameExists(username)) {
			outArr[0] = FALSE;
			outArr[1] = "Sorry that username has been taken.";
		} else if (username.equals(p.toString())) {
			outArr[0] = FALSE;
			outArr[1] = "Please ensure your username has only alphabetic or numeric characters.";
		} else if (usernameLength > 12) {
			outArr[0] = FALSE;
			outArr[1] = "Your username cannot be more than 12 characters long.";
		} else if (usernameLength == 0) {
			outArr[0] = FALSE;
			outArr[1] = "Your username cannot be empty!";
		} else {
			boolean signUp = Database.signUp(username, password, securityQ, securityA);
			
			if (signUp) {
				String[] notifyOnlineUsers = {SIGNED_IN, username};
				sockets.put(username, clientSocket);
				Main.broadcastMessage(notifyOnlineUsers);
				
				outArr[0] = TRUE;
				outArr[1] = "Sign-up successful. Welcome!";
			} else {
				outArr[0] = FALSE;
				outArr[1] = "An error occurred; please try again later.";
			}
		}
		
		return protocol.transmit(SIGN_UP, outArr);
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
			outArr[0] = FALSE;
			outArr[1] = "Your username and/or password were incorrect.";
		} else {
			Database.setStatus(username, ONLINE);
			sockets.put(username, clientSocket);
			
			String[] notifyOnlineUsers = {SIGNED_IN, username};
			Main.broadcastMessage(notifyOnlineUsers);
			
			outArr[0] = TRUE;
			outArr[1] = "Welcome back!";
		}
		
		return protocol.transmit(SIGN_IN, outArr);
	}
	
	/**
	 * Forgot password, step 1.
	 * Checks if username exists.
	 * 
	 * @param input username
	 * @return output indicating whether username has been found
	 */
	String forgotPasswordRequest(String[] input) {
		outArr = new String[2];
		
		String question = Database.forgotPasswordRequest(input[0]);
		
		if (question != null) {
			outArr[0] = TRUE;
			outArr[1] = question;
		} else {
			outArr[0] = FALSE;
			outArr[1] = "Username not found.";
		}
		
		return protocol.transmit(FORGOT_PASSWORD_REQUEST, outArr);
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
		outArr = new String[2];
		
		if (Database.forgotPasswordAnswer(input[0], input[1])) {
			outArr[0] = TRUE;
			outArr[1] = "Enter your new password:";
		} else {
			outArr[0] = FALSE;
			outArr[1] = "Sorry, the answer you gave did not match our records.";
		}
		
		return protocol.transmit(FORGOT_PASSWORD_ANSWER, outArr);
	}
	
	/**
	 * Creates a String of details of a user's profile.
	 * 
	 * @param input username String
	 * @return String with user's details
	 */
	String viewProfile(String input) {
		Profile p = Database.getProfile(input);
		
		String[] outArr = {TRUE, p.getUsername(), p.getStatus() + "", 
				p.getWins() + "", p.getLosses() + "", p.getTotal() + ""};
		
		return protocol.transmit(VIEW_PROFILE, outArr);
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
		
		outArr = new String[3];
		outArr[2] = ".";
		
		if (!newUsername.equals(".")) {
			if (Database.usernameExists(newUsername)) {
				outArr[0] = FALSE;
				outArr[1] = "Someone already has that username.";
				error = true;
			} else
				usernameToChange = true;
		}
		
		if (!newPassword.equals("."))
			passwordToChange = true;
		
		if (Database.signIn(currentUsername, password)) {
			if (!error) {
				outArr[0] = "true";
				boolean changesMade = false;
				
				//TODO how to update things using the Writer
				if (usernameToChange && passwordToChange) {
					changesMade = Database.changeProfileDetails(currentUsername, newUsername, newPassword);
					
					outArr[1] = "Your username and password have been successfully changed.";
					outArr[2] = newUsername;
				} else if (usernameToChange) {
					changesMade = Database.changeProfileDetails(currentUsername, newUsername, password);
					
					outArr[1] = "Your username has been successfully changed.";
					outArr[2] = newUsername;
				} else if (passwordToChange) {
					changesMade = Database.changeProfileDetails(currentUsername, currentUsername, newPassword);
					
					outArr[1] = "Your password has been successfully changed.";
				}
				
				if (!changesMade) {
					outArr[0] = FALSE;
					outArr[1] = "An error occurred. Please try again later.";
				}
			}
		} else {
			outArr[0] = FALSE;
			outArr[1] = "Your password was incorrect.";
		}
		
		return protocol.transmit(EDIT_PROFILE, outArr);
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
			String[] notifyOnlineUsers = {SIGNED_OUT, username};
			Main.broadcastMessage(notifyOnlineUsers);
			
			String[] outArr = {TRUE, "Signed out. See you soon!"};
			
			if (leftServer)
				Main.leftServer();
			
			return protocol.transmit(SIGN_OUT, outArr);
		} else {
			String[] outArr = {FALSE, "An error occurred. Please try again later."};
			
			return protocol.transmit(SIGN_OUT, outArr);
		}
	}
}
