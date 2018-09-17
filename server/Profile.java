package server;

/**
 * Profile class.
 * Keeps details of a user's username, security question,
 * and scores to date.
 * 
 * @author Ben Drawer
 * @version 8 September 2018
 *
 */
public class Profile implements Comparable<Profile> {
	private String username, password, status, securityAnswer;
	private short securityQuestion;
	private int wins, losses, total;
	
	/**
	 * Constructor.
	 * For use when creating a new user; wins and losses by default set to 0.
	 * 
	 * @param username username
	 * @param password password
	 * @param securityQuestion number representing security question
	 * @param securityAnswer answer to security question
	 */
	public Profile(String username, String password, short securityQuestion, 
			String securityAnswer) {
		this.username = username;
		this.password = password;
		this.securityQuestion = securityQuestion;
		this.securityAnswer = securityAnswer;
		this.status = "online";
		
		this.wins = 0;
		this.losses = 0;
		this.total = 0;
	}
	
	/**
	 * Constructor.
	 * For use when retrieving a user from the database.
	 * 
	 * @param username
	 * @param status
	 * @param wins
	 * @param losses
	 */
	Profile(String username, String status, int wins, int losses) {
		this.username = username;
		this.status = status;
		
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
	 * Getter for user status.
	 * 0 = offline;
	 * 1 = online but unavailable;
	 * 2 = available.
	 * 
	 * @return user status
	 */
	String getStatus() {
		return status;
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
	 * Sets the user's status.
	 * 0 = offline;
	 * 1 = online but unavailable;
	 * 2 = available.
	 * 
	 * @param online new status
	 */
	void setStatus(String status) {
		this.status = status;
	}
	
	/**
	 * Setter for username.
	 * 
	 * @param username new username.
	 */
	void setUsername(String username) {
		this.username = username;
	}
	
	/**
	 * Setter for password.
	 * 
	 * @param password new password
	 */
	void setPassword(String password) {
		this.password = password;
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
		return username + "//" + status + "//" + wins + "//" 
				+ losses + "//" + total;
	}
}
