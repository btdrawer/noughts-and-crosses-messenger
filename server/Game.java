package server;

/**
 * Game class.
 * 
 * @author Ben Drawer
 * @version 13 June 2018
 *
 */
class Game {
	private String[] players;
	private char[] board;
	private boolean finished;
	
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
		
		this.finished = false;
	}
	
	/**
	 * The two players are saved in an array.
	 * If the game has finished, the order of the array indicates which
	 * player won, with players[0] being the winning player.
	 * 
	 * @return names of both players
	 */
	String[] getPlayers() {
		return players;
	}
	
	/**
	 * 
	 * @return boolean indicating whether the game has finished (true) or is ongoing (false)
	 */
	boolean isFinished() {
		return finished;
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
	
	/**
	 * Setter method for the players array.
	 * To be called when a game is finished, as once this is the case the order
	 * of the players in the array indicates who won.
	 * 
	 * @param players the new player array
	 */
	void setPlayers(String[] players) {
		this.players = players;
	}
	
	/**
	 * Is called when the game finishes.
	 */
	void finished() {
		this.finished = true;
	}
}
