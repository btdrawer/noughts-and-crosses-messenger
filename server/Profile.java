package server;

/**
 * Profile class.
 * Keeps details of a user's username, security question,
 * and scores to date.
 * 
 * @author Ben Drawer
 * @version 13 June 2018
 *
 */
class Profile implements Comparable<Profile> {
	private String username, password, securityAnswer;
	private short securityQuestion;
	private int wins, losses, total;
	private boolean online, available;
	
	/**
	 * Constructor.
	 * For use when creating a new user; wins and losses by default set to 0.
	 * 
	 * @param username username
	 * @param password password
	 * @param securityQuestion number representing security question
	 * @param securityAnswer answer to security question
	 */
	Profile(String username, String password, short securityQuestion, 
			String securityAnswer) {
		this.username = username;
		this.password = password;
		this.securityQuestion = securityQuestion;
		this.securityAnswer = securityAnswer;
		this.online = true;
		this.available = true;
		
		this.wins = 0;
		this.losses = 0;
		this.total = 0;
	}
	
	/**
	 * Constructor.
	 * For use in the WriterThread when reading in existing users.
	 * 
	 * @param username username
	 * @param password password
	 * @param securityQuestion number representing security question
	 * @param securityAnswer answer to security question
	 * @param wins number of games the user has won
	 * @param losses number of games the user has lost
	 */
	Profile(String username, String password, short securityQuestion, 
			String securityAnswer, int wins, int losses) {
		this.username = username;
		this.password = password;
		this.securityQuestion = securityQuestion;
		this.securityAnswer = securityAnswer;
		this.online = true;
		this.available = true;
		
		this.wins = wins;
		this.losses = losses;
		this.total = wins - losses;
	}
	
	/**
	 * 
	 * @return username
	 */
	String getUsername() {
		return username;
	}
	
	/**
	 * 
	 * @return password
	 */
	String getPassword() {
		return password;
	}
	
	/**
	 * 
	 * @return number representing security question
	 */
	int getSecurityQuestion() {
		return securityQuestion;
	}
	
	/**
	 * 
	 * @return user's answer to security question
	 */
	String getSecurityAnswer() {
		return securityAnswer;
	}
	
	/**
	 * 
	 * @return whether the user is online or not
	 */
	boolean isOnline() {
		return online;
	}
	
	/**
	 * 
	 * @return whether the user is ready to play or not
	 */
	boolean isAvailable() {
		return available;
	}
	
	/**
	 * 
	 * @return user's total wins
	 */
	int getWins() {
		return wins;
	}
	
	/**
	 * 
	 * @return user's total losses
	 */
	int getLosses() {
		return losses;
	}
	
	/**
	 * 
	 * @return wins - losses
	 */
	int getTotal() {
		return total;
	}
	
	/**
	 * Increments a user's win count and total by 1
	 */
	void addWin() {
		wins += 1;
		total += 1;
	}
	
	/**
	 * Increments a user's loss count and decrements total by 1
	 */
	void addLoss() {
		losses += 1;
		total -= 1;
	}
	
	/**
	 * Sets the user's online status.
	 * It also sets the user's availability status, since this will also be
	 * true as soon as the user comes online, and will of course be false once
	 * the user is offline.
	 * 
	 * @param online new online status
	 */
	void setOnlineStatus(boolean online) {
		this.online = online;
		this.available = online;
	}
	
	/**
	 * Sets the user's availability to join a new game.
	 * When the user joins a new game, their availability is set to false,
	 * indicating that they can't join another game yet.
	 * When the game is finished, their availability status is set to false.
	 * 
	 * @param available new availability status
	 */
	void setAvailabilityStatus(boolean available) {
		this.available = available;
	}
	
	/**
	 * compareTo method.
	 * Compares users firstly on the basis of their total score,
	 * and if their score is the same then they are sorted alphabetically.
	 */
	public int compareTo(Profile p) {
		if (this.getTotal() > p.getTotal()) {
			return 1;
		} else if (this.getTotal() < p.getTotal()) {
			return -1;
		} else {
			return this.getUsername().compareTo(p.getUsername());
		}
	}
	
	/**
	 * toString() method.
	 */
	public String toString() {
		return username + "/" + online + "/" + available + "/" + wins + "/" 
				+ losses + "/" + total;
	}
}
