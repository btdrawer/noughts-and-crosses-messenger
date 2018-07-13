package server;

/**
 * Game class.
 * 
 * @author Ben Drawer
 * @version 25 June 2018
 *
 */
class Game {
	private String[] players;
	private char[][] board;
	private int winner;
	private boolean finished;
	private short turns;
	
	/**
	 * Constructor for new games.
	 * 
	 * @param player1
	 * @param player2
	 */
	Game(String player1, String player2) {
		players = new String[2];
		players[0] = player1;
		players[1] = player2;
		
		board = new char[3][3];
		
		this.finished = false;
		this.turns = 0;
	}
	
	/**
	 * Constructor for finished games.
	 * Used by the Reader class when reading in previous games from a text file.
	 * 
	 * @param player1
	 * @param player2
	 * @param winner
	 */
	Game(String player1, String player2, int winner) {
		players = new String[2];
		players[0] = player1;
		players[1] = player2;
		
		this.winner = winner;
		this.finished = true;
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
	 * Key for winner value:
	 * 0 = draw
	 * 1 = player1
	 * 2 = player2
	 * 
	 * @return winner
	 */
	int getWinner() {
		return winner;
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
	char[][] getBoard() {
		return board;
	}
	
	/**
	 * Adds a character to the board
	 * 
	 * @param position
	 * @param x character to be added
	 */
	void addChar(int x, int y, char c) {
		board[x][y] = c;
	}
	
	/**
	 * Setter method for the winner value.
	 * 0 = draw
	 * 1 = player1
	 * 2 = player2
	 * 
	 * @param players the new player array
	 */
	void setWinner(int winner) {
		this.winner = winner;
	}
	
	/**
	 * Getter method for the number of turns played.
	 * When it reaches 9, the game should terminate.
	 * 
	 * @return turns
	 */
	short getTurns() {
		return turns;
	}
	
	/**
	 * Adds a turn to the total number.
	 */
	void addTurn() {
		turns += 1;
	}
	
	/**
	 * Is called when the game finishes.
	 */
	void finished() {
		this.finished = true;
	}
	
	public String toString() {
		return players[0] + "//" + players[1] + "//" + winner;
	}
}
