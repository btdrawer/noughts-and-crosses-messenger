package server;

/**
 * Game class.
 * 
 * @author Ben Drawer
 * @version 20 May 2018
 *
 */
class Game {
	private String[] players;
	private char[] board;
	
	/**
	 * Constructor.
	 * 
	 * @param player1
	 * @param player2
	 */
	Game(String player1, String player2) {
		players = new String[2];
		players[0] = player1;
		players[1] = player2;
		
		board = new char[9];
	}
	
	/**
	 * 
	 * @return names of both players
	 */
	String[] getPlayers() {
		return players;
	}
	
	/**
	 * 
	 * @return array representing the board
	 */
	char[] getBoard() {
		return board;
	}
	
	/**
	 * Adds a character to the board
	 * 
	 * @param position
	 * @param x character to be added
	 */
	void addChar(int position, char x) {
		board[position - 1] = x;
	}
}
