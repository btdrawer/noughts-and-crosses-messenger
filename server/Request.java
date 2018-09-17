package server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import protocol.Protocol;

/**
 * This class handles a request.
 * 
 * @author Ben Drawer
 * @version 8 September 2018
 *
 */
class Request implements Task {
	private Socket clientSocket;
	private Protocol protocol;
	private String output;
	private String[] outArr;
	private BufferedReader in;
	private DataOutputStream out;
	private Game currentGame;
	private static Map<String, Socket> sockets = Server.getSockets();
	
	/**
	 * Constructor.
	 * 
	 * @param clientSocket connecting socket
	 * @throws IOException 
	 */
	Request(Socket clientSocket) throws IOException {
		this.clientSocket = clientSocket;
		this.protocol = Server.getProtocol();
		this.outArr = new String[2];
		this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		this.out = new DataOutputStream(clientSocket.getOutputStream());
	}
	
	/**
	 * 
	 * @return input from client
	 */
	BufferedReader getBufferedReader() {
		return in;
	}
	
	/**
	 * 
	 * @return output to client
	 */
	DataOutputStream getDataOutputStream() {
		return out;
	}
	
	/**
	 * Tests the connection.
	 * 
	 * @return output stating that the connection has been successful
	 */
	private String connect() {
		Server.joinedServer();
		System.out.println("Connection with client established.\n" +
				"numberOfOnlineUsers: " + Server.getNumberOfOnlineUsers());
		
		outArr = new String[2];
		outArr[0] = "true";
		outArr[1] = "Connection test successful.";
		
		return protocol.transmit("connect", outArr);
	}
	
	private String getSecurityQuestions() {
		List<String> securityQuestions = Database.getSecurityQuestions();
		StringBuilder s = new StringBuilder();
		
		for (String sv : securityQuestions) {
			s.append(sv + "//");
		}
		
		return protocol.transmit("securityquestions", s.toString());
	}
	
	/**
	 * Sign-up method.
	 * 
	 * @param input [0] = username; [1] = password; [2] = security question; [3] = security answer
	 * @return output indicating whether sign-up has been successful
	 * @throws NoSuchAlgorithmException if password hashing algorithm unavailable
	 */
	private String signup(String[] input) throws NoSuchAlgorithmException {
		String username = input[0], password = input[1], securityQ = input[2],
				securityA = input[3];
		int usernameLength = input[0].length(), passwordLength = input[1].length();
		
		//TODO regex pattern for password
		Pattern p = Pattern.compile("[a-zA-Z0-9]+");
		
		outArr = new String[2];
		
		if (Database.usernameExists(username)) {
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
			boolean signUp = Database.signUp(username, password, securityQ, securityA);
			
			if (signUp) {
				String[] notifyOnlineUsers = {"signedin", username};
				sockets.put(username, clientSocket);
				Server.broadcastMessage(notifyOnlineUsers);
				
				outArr[0] = "true";
				outArr[1] = "Sign-up successful. Welcome!";
			} else {
				outArr[0] = "false";
				outArr[1] = "An error occurred; please try again later.";
			}
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
	static String getMD5(String input) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.reset();
		md.update(input.getBytes());
		byte[] digest = md.digest();
		
		StringBuilder s = new StringBuilder();
		
		for (byte b : digest) {
			s.append(b);
		}
		
		return s.toString();
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
		
		outArr = new String[2];
		
		if (!Database.signIn(username, password)) {
			outArr[0] = "false";
			outArr[1] = "Your username and/or password were incorrect.";
		} else {
			Database.setStatus(username, "online");
			sockets.put(username, clientSocket);
			
			String[] notifyOnlineUsers = {"signedin", username};
			Server.broadcastMessage(notifyOnlineUsers);
			
			outArr[0] = "true";
			outArr[1] = "Welcome back!";
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
		outArr = new String[2];
		
		String question = Database.forgotPasswordRequest(input[0]);
		
		if (question != null) {
			outArr[0] = "true";
			outArr[1] = question;
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
		outArr = new String[2];
		
		if (Database.forgotPasswordAnswer(input[0], input[1])) {
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
	private String requestUsers(String[] input) {
		StringBuilder sb = new StringBuilder();
		
		List<String> users = Database.getUsers();
		
		for (String s : users) {
			if (!s.equals((input[0]))) {
				sb.append(s + "//");
			}
		}
		
		return protocol.transmit("requestusers", sb.toString());
	}
	
	/**
	 * Creates a String of details of a user's profile.
	 * 
	 * @param input input array (the method takes the 0th element)
	 * @return String with user's details
	 */
	private String viewProfile(String[] input) {
		return viewProfile(input[0]);
	}
	
	/**
	 * Creates a String of details of a user's profile.
	 * 
	 * @param input username String
	 * @return String with user's details
	 */
	private String viewProfile(String input) {
		Profile p = Database.getProfile(input);
		
		String[] outArr = {"true", p.getUsername(), p.getStatus() + "", 
				p.getWins() + "", p.getLosses() + "", p.getTotal() + ""};
		
		return protocol.transmit("viewprofile", outArr);
	}
	
	/**
	 * Provides the usernames, gross wins and net wins of the n highest-achieving
	 * users.
	 * 
	 * @param input input[0] gives the number of users needed
	 * @return
	 */
	private String leaderboard(String[] input) {
		List<String[]> leaderboardList = Database.getLeaderboard();
		int size = leaderboardList.size();
		
		if (size > 0) {
			outArr = new String[size * 3 + 1];
			outArr[0] = "true";
			int i = 0;
			
			for (String[] s : leaderboardList) {
				outArr[i] = s[0];
				outArr[i+1] = s[1];
				outArr[i+2] = s[2];
				
				i += 3;
			}
		} else {
			outArr = new String[2];
			outArr[0] = "false";
			outArr[1] = "It's lonely in here!";
		}
		
		return protocol.transmit("leaderboard", outArr);
	}
	
	private String timedLeaderboard(String[] input) {
		//TODO
		return null;
	}
	
	/**
	 * Sends a challenge to the chosen recipient.
	 * 
	 * Input:
	 * 0 = user sending the challenge
	 * 1 = challenge recipient
	 * 
	 * @param input
	 * @throws IOException
	 */
	private String[] sendChallenge(String[] input) throws IOException {
		String[] toSend = {protocol.transmit("challenge", input[0]), input[1]};
		
		return toSend;
	}
	
	/**
	 * Sends the user's response to the challenge.
	 * 
	 * Input:
	 * 0 = "true" if challenge accepted, "false" if rejected
	 * 1 = challenger's username
	 * 2 = recipient's username 
	 * 
	 * @param input
	 * @throws IOException
	 */
	private String[] challengeResponse(String[] input) throws IOException {
		outArr = new String[3];
		outArr[0] = input[0];
		outArr[1] = input[2];
		
		if (input[0].equals("false"))
			outArr[2] = "Sorry, this user declined your challenge.";
		
		String[] toSend = {protocol.transmit("challengeresponse", outArr), input[1]};
		
		return toSend;
	}
	
	/**
	 * Creates a new game, first checking that both users are available.
	 * 
	 * @param input users who want to play
	 * @return output indicating whether the new game has been successfully initiated
	 */
	private String newGame(String[] input) {
		outArr = new String[2];
		
		if (Database.getProfile(input[0]).getStatus() == "offline" || 
				Database.getProfile(input[1]).getStatus() == "busy") {
			outArr[0] = "false";
			outArr[1] = "This user isn't available.";
		} else {
			currentGame = new Game(input[0], input[1]);
			
			Database.setStatus(input[0], "busy");
			Database.setStatus(input[1], "busy");
			
			outArr[0] = "true";
		}
		
		return protocol.transmit("newgame", outArr);
	}
	
	/**
	 * Evaluates whether a move played has won the game.
	 * 
	 * @param board the game board
	 * @param c the character to be checked (O or X)
	 * @param x x-position of the character
	 * @param y y-position of the character
	 * @return boolean indicating whether or not the game has been won.
	 */
	private boolean checkWin(char[][] board, char c, int x, int y) {
		boolean xWin = false, yWin = false, dWin0 = false, dWin1 = false;
		
		if (board[x][0] == c && board[x][0] == board[x][1] && 
				board[x][1] == board[x][2])
			xWin = true;
		
		if (board[0][y] == c && board[0][y] == board[1][y] 
				&& board[1][y] == board[2][y])
			yWin = true;
		
		if (board[0][0] == c && board[0][0] == board[1][1] 
				&& board[1][1] == board[2][2])
			dWin0 = true;
		
		if (board[0][2] == c && board[0][2] == board[1][1] 
				&& board[1][1] == board[2][0])
			dWin1 = true;
		
		return xWin || yWin || dWin0 || dWin1;
	}
	
	/**
	 * Add a character to a game.
	 * 
	 * @param input [0] = player on current turn; [1] = opponent; [2], [3] = x- and y-coordinates; [4] = O or X
	 * @return output indicating whether character input has been successful
	 * @throws IOException 
	 */
	private String[] addChar(String[] input) throws IOException {
		int x = Integer.parseInt(input[2]);
		int y = Integer.parseInt(input[3]);
		char c = input[4].charAt(0);
		
		currentGame.addChar(x, y, c);
		currentGame.addTurn();
		
		short turns = currentGame.getTurns();
		
		outArr = new String[4];
		
		if (checkWin(currentGame.getBoard(), c, x, y)) {
			outArr[0] = "true_lost";
			
			String[] players = currentGame.getPlayers();
			String winner = "", loser = "";
			
			if (players[0].equals(input[0])) {
				winner = players[0];
				loser = players[1];
			} else if (players[1].equals(input[0])) {
				winner = players[1];
				loser = players[1];
			}
			
			Database.newGame(winner, loser);
		} else if (turns == 9)
			outArr[0] = "true_draw";
		else
			outArr[0] = "true";
		
		outArr[1] = input[2];
		outArr[2] = input[3];
		outArr[3] = input[4];
		
		String[] toSend = {protocol.transmit("addchar", outArr), input[1]};
		
		if (outArr[0].equals("true_lost")) {
			outArr[0] = "true_won";
			sockets.get(input[0]).getOutputStream().write(
				protocol.transmit("addchar", outArr).getBytes());
		}
		
		return toSend;
	}
	
	/**
	 * Processes a request to change a user's username and/or password.
	 * 
	 * @param input
	 * @return output indicating whether changes have been successful
	 * @throws NoSuchAlgorithmException
	 */
	private String changes(String[] input) throws NoSuchAlgorithmException {
		String currentUsername = input[1];
		String newUsername = input[2];
		String password = input[4];
		String newPassword = input[5];
		boolean usernameToChange = false, passwordToChange = false, error = false;
		
		outArr = new String[3];
		outArr[2] = ".";
		
		if (!newUsername.equals(".")) {
			if (!Database.usernameExists(newUsername)) {
				outArr[0] = "false";
				outArr[1] = "Someone already has that username.";
				error = true;
			} else
				usernameToChange = true;
		}
		
		if (!newPassword.equals(".")) {
			if (newPassword.length() < 6 || newPassword.length() > 15) {
				outArr[0] = "false";
				outArr[1] = "Your password must be between 6 and 15 characters in length.";
				error = true;
			} else
				passwordToChange = true;
		}
		
		if (Database.signIn(currentUsername, getMD5(password))) {
			if (!error) {
				outArr[0] = "true";
				String newData = null;
				
				//TODO how to update things using the Writer
				if (usernameToChange && passwordToChange) {
					newData = "username = " + newUsername + ", password = " + getMD5(password);
					
					outArr[1] = "Your username and password have been successfully changed.";
					outArr[2] = newUsername;
				} else if (usernameToChange) {
					newData = "username = " + newUsername;
					
					outArr[1] = "Your username has been successfully changed.";
					outArr[2] = newUsername;
				} else if (passwordToChange) {
					newData = "password = " + getMD5(password);
					
					outArr[1] = "Your password has been successfully changed.";
				}
				
				boolean changesMade = Database.changeProfileDetails(currentUsername, newData);
				
				if (!changesMade) {
					outArr[0] = "false";
					outArr[1] = "An error occurred. Please try again later.";
				}
			}
		} else {
			outArr[0] = "false";
			outArr[1] = "Your password was incorrect.";
		}
		
		return protocol.transmit("changes", outArr);
	}
	
	/**
	 * Is called when a player quits a game.
	 * 
	 * @param input [0] = quitting player; [1] = other player
	 * @return output indicating whether game was saved successfully
	 * @throws IOException 
	 */
	private String leftGame(String[] input) throws IOException {
		Database.setStatus(input[0], "online");
		Database.setStatus(input[1], "online");
		
		String otherUsersProfile = viewProfile(input[0]);
		
		sockets.get(input[1]).getOutputStream().write(
				protocol.transmit("leavegame", 
						otherUsersProfile.substring(13, 
								otherUsersProfile.length())).getBytes());
		
		return viewProfile(input[1]);
	}
	
	/**
	 * Signs the user out.
	 * 
	 * @param input [0] = username
	 * @return output indicating that the sign out has been successful
	 */
	private String signout(String[] input) {
		String username = input[0];
		
		boolean signedOut = Database.setStatus(username, "offline");
		
		if (signedOut) {
			String[] notifyOnlineUsers = {"signedout", username};
			Server.broadcastMessage(notifyOnlineUsers);
			
			String[] outArr = {"true", "Signed out. See you soon!"};
			
			return protocol.transmit("signout", outArr);
		} else {
			String[] outArr = {"false", "An error occurred. Please try again later."};
			
			return protocol.transmit("signout", outArr);
		}
	}
	
	/**
	 * Run method.
	 * This runs in a continuous loop, listening on the client socket and calling
	 * the appropriate methods accordingly.
	 */
	public synchronized void run() {
		try {
			while(true) {
				String s, action;
				String[] input;
				boolean sendToOtherUser;
				
				while((s = in.readLine()) != null) {
					System.out.println("Input: " + s);
					
					action = protocol.getAction(s);
					input = protocol.receive(s);
					
					sendToOtherUser = false;
					
					if (action.equals("connect"))
						output = connect();
					else if (action.equals("securityquestions"))
						output = getSecurityQuestions();
					else if (action.equals("signup"))
						output = signup(input);
					else if (action.equals("signin"))
						output = signin(input);
					else if (action.equals("forgotReq"))
						output = forgotPasswordRequest(input);
					else if (action.equals("forgot"))
						output = forgotPassword(input);
					else if (action.equals("requestusers"))
						output = requestUsers(input);
					else if (action.equals("viewprofile"))
						output = viewProfile(input);
					else if (action.equals("leaderboard"))
						output = leaderboard(input);
					else if (action.equals("timedlederboard"))
						output = timedLeaderboard(input);
					else if (action.equals("challenge")) {
						outArr = sendChallenge(input);
						sendToOtherUser = true;
					} else if (action.equals("challengeresponse")) {
						outArr = challengeResponse(input);
						sendToOtherUser = true;
					} else if (action.equals("newgame"))
						output = newGame(input);
					else if (action.equals("addchar")) {
						outArr = addChar(input);
						sendToOtherUser = true;
					} else if (action.equals("editprofile"))
						output = changes(input);
					else if (action.equals("leavegame"))
						output = leftGame(input);
					else if (action.equals("signout"))
						output = signout(input);
					
					if (sendToOtherUser) {
						System.out.println("Output to " + outArr[1] + ": " + outArr[0]);
						
						sockets.get(outArr[1]).getOutputStream().write(outArr[0].getBytes());
					} else {
						System.out.println("Output: " + output);
						
						out.writeBytes(output);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} finally {
			try {
				clientSocket.close();
				Server.leftServer();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
