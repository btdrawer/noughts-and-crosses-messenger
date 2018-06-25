package server;

import java.sql.Timestamp;

/**
 * 
 * @author Ben Drawer
 * @version 20 May 2018
 *
 */
class TimedGame extends Game implements Comparable<TimedGame> {
	private Timestamp time;
	
	/**
	 * Constructor.
	 * 
	 * @param player1
	 * @param player2
	 */
	TimedGame(String player1, String player2) {
		super(player1, player2);
	}
	
	/**
	 * 
	 * @return length of game
	 */
	Timestamp getTime() {
		return time;
	}

	/**
	 * compareTo method.
	 * Compares games on the basis of how long they lasted.
	 */
	@Override
	public int compareTo(TimedGame o) {
		return this.time.compareTo(o.getTime());
	}
	
	public String toString() {
		return super.toString() + "//" + time;
	}
}
