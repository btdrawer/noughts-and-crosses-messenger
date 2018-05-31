package server;

/**
 * Profile class.
 * Keeps details of a user's username, security question,
 * and scores to date.
 * 
 * @author Ben Drawer
 * @version 16 May 2018
 *
 */
class Profile implements Comparable<Profile> {
	private String username, securityAnswer;
	byte[] password;
	private short securityQuestion;
	private int wins, losses, total;
	private boolean available;
	
	/**
	 * Constructor.
	 * For use when creating a new user; wins and losses by default set to 0.
	 * 
	 * @param username username
	 * @param password password
	 * @param securityQuestion number representing security question
	 * @param securityAnswer answer to security question
	 */
	Profile(String username, byte[] password, short securityQuestion, 
			String securityAnswer) {
		this.username = username;
		this.password = password;
		this.securityQuestion = securityQuestion;
		this.securityAnswer = securityAnswer;
		this.available = false;
		
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
	Profile(String username, byte[] password, short securityQuestion, 
			String securityAnswer, int wins, int losses) {
		this.username = username;
		this.password = password;
		this.securityQuestion = securityQuestion;
		this.securityAnswer = securityAnswer;
		this.available = false;
		
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
	byte[] getPassword() {
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
	 * Sets the user's availability to false.
	 */
	void joinedGame() {
		available = false;
	}
	
	/**
	 * Sets the user's availability to true.
	 */
	void leftGame() {
		available = true;
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
		return username + "/" + available + "/" + wins + "/" + losses + "/" + total;
	}
}
