package server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import protocol.Protocol;

/**
 * This class handles a request.
 * 
 * @author Ben Drawer
 * @version 20 September 2018
 *
 */
class Request {
	private Protocol protocol;
	private String output;
	private String[] outArr;
	private BufferedReader in;
	private DataOutputStream out;
	private static Map<String, Socket> sockets = Main.getSockets();
	private ServerActions serverActions;
	private ProfileActions profileActions;
	private GameActions gameActions;
	
	/**
	 * Constructor.
	 * 
	 * @param clientSocket connecting socket
	 * @throws IOException 
	 */
	Request(Socket clientSocket) throws IOException {
		this.protocol = Main.getProtocol();
		this.outArr = new String[2];
		this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		this.out = new DataOutputStream(clientSocket.getOutputStream());
		this.serverActions = new ServerActions(clientSocket);
		this.profileActions = new ProfileActions(clientSocket);
		this.gameActions = new GameActions();
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
	 * Creates a String of details of a user's profile.
	 * 
	 * @param input input array (the method takes the 0th element)
	 * @return String with user's details
	 */
	private String viewProfile(String[] input) {
		return profileActions.viewProfile(input[0]);
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
						output = serverActions.connect();
					else if (action.equals("securityquestions"))
						output = serverActions.getSecurityQuestions();
					else if (action.equals("signup"))
						output = profileActions.signup(input);
					else if (action.equals("signin"))
						output = profileActions.signin(input);
					else if (action.equals("forgotReq"))
						output = profileActions.forgotPasswordRequest(input);
					else if (action.equals("forgot"))
						output = profileActions.forgotPassword(input);
					else if (action.equals("requestusers"))
						output = serverActions.requestUsers(input);
					else if (action.equals("viewprofile"))
						output = viewProfile(input);
					else if (action.equals("leaderboard"))
						output = serverActions.leaderboard(input);
					else if (action.equals("timedlederboard"))
						output = serverActions.timedLeaderboard(input);
					else if (action.equals("challenge")) {
						outArr = gameActions.sendChallenge(input);
						sendToOtherUser = true;
					} else if (action.equals("challengeresponse")) {
						outArr = gameActions.challengeResponse(input);
						sendToOtherUser = true;
					} else if (action.equals("newgame"))
						output = gameActions.newGame(input);
					else if (action.equals("addchar")) {
						outArr = gameActions.addChar(input);
						sendToOtherUser = true;
					} else if (action.equals("editprofile"))
						output = profileActions.changes(input);
					else if (action.equals("leavegame"))
						output = gameActions.leftGame(input, profileActions);
					else if (action.equals("signout"))
						output = profileActions.signout(input, false);
					else if (action.equals("signout_leftserver"))
						output = profileActions.signout(input, true);
					
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
		}
	}
}
