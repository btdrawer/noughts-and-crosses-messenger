package server;

import java.io.IOException;
import java.net.Socket;
import java.util.Map;

import protocol.Protocol;

/**
 * 
 * @author Ben Drawer
 * @version 20 September 2018
 *
 */
class GameActions {
	private String[] outArr;
	private Protocol protocol;
	private static Map<String, Socket> sockets = Main.getSockets();
	
	/**
	 * Constructor.
	 */
	GameActions() {
		this.protocol = Main.getProtocol();
		this.outArr = new String[2];
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
	String[] sendChallenge(String[] input) throws IOException {
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
	String[] challengeResponse(String[] input) throws IOException {
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
	String newGame(String[] input) {
		outArr = new String[2];
		
		if (Database.getProfile(input[0]).getStatus() == "offline" || 
				Database.getProfile(input[1]).getStatus() == "busy") {
			outArr[0] = "false";
			outArr[1] = "This user isn't available.";
		} else {
			Main.newGame(input);
			
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
	String[] addChar(String[] input) throws IOException {
		String[] players = {input[0], input[1]};
		int x = Integer.parseInt(input[2]);
		int y = Integer.parseInt(input[3]);
		char c = input[4].charAt(0);
		
		Main.addChar(players[0], x, y, c);
		
		Game currentGame = Main.findGame(input[0]);
		short turns = currentGame.getTurns();
		
		outArr = new String[4];
		
		if (checkWin(currentGame.getBoard(), c, x, y)) {
			outArr[0] = "true_lost";
			
			players = currentGame.getPlayers();
			String winner = "", loser = "";
			
			if (players[0].equals(input[0])) {
				winner = players[0];
				loser = players[1];
			} else if (players[1].equals(input[0])) {
				winner = players[1];
				loser = players[1];
			}
			
			Database.newGame(winner, loser);
			
			Database.setStatus(players[0], "online");
			Database.setStatus(players[1], "online");
			Main.gameFinished(winner);
		} else if (turns == 9) {
			outArr[0] = "true_draw";
			
			Database.setStatus(players[0], "online");
			Database.setStatus(players[1], "online");
			Main.gameFinished(players[0]);
		} else
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
	 * Is called when a player quits a game.
	 * 
	 * @param input [0] = quitting player; [1] = other player
	 * @return output indicating whether game was saved successfully
	 * @throws IOException 
	 */
	String leftGame(String[] input, ProfileActions profileActions) throws IOException {
		Database.setStatus(input[0], "online");
		Database.setStatus(input[1], "online");
		
		String otherUsersProfile = profileActions.viewProfile(input[0]);
		
		sockets.get(input[1]).getOutputStream().write(
				protocol.transmit("leavegame", 
						otherUsersProfile.substring(13, 
								otherUsersProfile.length())).getBytes());
		
		return profileActions.viewProfile(input[1]);
	}
}
