package server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import protocol.Protocol;
import protocol.Constants;

/**
 * This class handles a request.
 * 
 * @author Ben Drawer
 * @version 11 October 2018
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
	private MessageActions messageActions;
	
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
		this.messageActions = new MessageActions();
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
	 * Run method.
	 * This runs in a continuous loop, listening on the client socket and calling
	 * the appropriate methods accordingly.
	 */
	public synchronized void run() {
		try {
			while(true) {
				String s, action;
				String[] input;
				boolean result, sendToOtherUser;
				
				while((s = in.readLine()) != null) {
					System.out.println("Input: " + s);
					
					action = protocol.getAction(s);
					input = protocol.receive(s);
					
					sendToOtherUser = false;
					
					switch (action) {
						case Constants.CONNECT:
							output = serverActions.connect();
							break;
						case Constants.GET_SECURITY_QUESTIONS:
							output = serverActions.getSecurityQuestions();
							break;
						case Constants.SIGN_UP:
							output = profileActions.signup(input);
							break;
						case Constants.SIGN_IN:
							output = profileActions.signin(input);
							break;
						case Constants.FORGOT_PASSWORD_REQUEST:
							output = profileActions.forgotPasswordRequest(input);
							break;
						case Constants.FORGOT_PASSWORD_ANSWER:
							output = profileActions.forgotPassword(input);
							break;
						case Constants.FORGOT_PASSWORD_CHANGE:
							output = profileActions.forgotPasswordChange(input);
							break;
						case Constants.GET_ONLINE_USERS:
							output = serverActions.requestUsers(input);
							break;
						case Constants.VIEW_PROFILE:
							output = profileActions.viewProfile(input[0]);
							break;
						case Constants.GET_LEADERBOARD:
							output = serverActions.leaderboard(input);
							break;
						case Constants.GET_TIMED_LEADERBOARD:
							output = serverActions.timedLeaderboard(input);
							break;
						case Constants.SEND_CHALLENGE:
							output = gameActions.sendChallenge(input);
							break;
						case Constants.RESPOND_TO_CHALLENGE:
							result = protocol.getResult(s);
							outArr = gameActions.challengeResponse(result, input);
							sendToOtherUser = true;
							break;
						case Constants.NEW_GAME:
							output = gameActions.newGame(input);
							break;
						case Constants.ADD_CHAR:
							outArr = gameActions.addChar(input);
							sendToOtherUser = true;
							break;
						case Constants.EDIT_PROFILE:
							output = profileActions.changes(input);
							break;
						case Constants.GET_MESSAGES:
							output = messageActions.getMessages(input);
							break;
						case Constants.SEND_MESSAGE:
							output = messageActions.sendMessage(input);
							break;
						case Constants.LEFT_GAME:
							output = gameActions.leftGame(input, profileActions);
							break;
						case Constants.SIGN_OUT:
							output = profileActions.signout(input, false);
							break;
						case Constants.LEFT_SERVER:
							output = profileActions.signout(input, true);
							break;
					}
					
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
