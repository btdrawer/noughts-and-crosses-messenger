package server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

/**
 * Database functions for user management.
 * 
 * @author Ben Drawer
 * @version 8 September 2018
 *
 */
class Database {
	private static Connection con;
	
	/**
	 * Setup the connection to the database.
	 * 
	 * @param url database url
	 * @param username database username
	 * @param password database password
	 * @return true or false
	 */
	static boolean setConnection(String url, String username, String password) {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			System.out.println("Driver registered.");
			
			url = "jdbc:mysql://" + url + "/noughtsandcrosses";
			con = DriverManager.getConnection(url, username, password);
			
			return true;
		} catch (ClassNotFoundException e) {
			System.out.println("Driver not found.");
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	/**
	 * 
	 * @return a list of security questions stored in the database.
	 */
	static List<String> getSecurityQuestions() {
		List<String> securityQuestions = new LinkedList<>();
		
		try {
			PreparedStatement stmt = con.prepareStatement(
					"SELECT question FROM security_question;");
			
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				securityQuestions.add(rs.getString(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return securityQuestions;
	}
	
	/**
	 * Sign-up.
	 * 
	 * @param con database connection
	 * @param username
	 * @param password
	 * @param securityQ number for security question
	 * @param securityAnswer
	 * @return true or false
	 */
	static boolean signUp(String username, String password, String securityQ, 
			String securityAnswer) {
		try {
			PreparedStatement stmt = con.prepareStatement(
					"SELECT * FROM user WHERE username LIKE ?;");
			
			stmt.setString(1, username);
			
			ResultSet rs = stmt.executeQuery();
			rs.last();
			
			if (rs.getRow() == 1) {
				return false;
			} else {
				stmt = con.prepareStatement(
						"INSERT INTO user (username, password, security_question, security_answer, status) " +
						"SELECT ?, ?, id, ?, " + 
						"(SELECT id FROM status WHERE status = 'online') " +
						"FROM security_question WHERE question = ?;");
				
				stmt.setString(1, username);
				stmt.setString(2, password);
				stmt.setString(3, securityAnswer);
				stmt.setString(4, securityQ);
				
				stmt.executeUpdate();
				
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Sign in.
	 * 
	 * @param con connection to database
	 * @param username
	 * @param password
	 * @return true or false
	 */
	static boolean signIn(String username, String password) {
		try {
			PreparedStatement stmt = con.prepareStatement(
					"SELECT * FROM user WHERE username = ? AND password = ?;");
			
			stmt.setString(1, username);
			stmt.setString(2, password);
			
			ResultSet rs = stmt.executeQuery();
			rs.last();
			
			return rs.getRow() == 1;
		} catch (SQLException e) {
			return false;
		}
	}
	
	/**
	 * Retrieve the usernames of all online users.
	 * 
	 * @param con connection to database
	 * @return list of online users
	 */
	static List<String> getUsers() {
		List<String> users = new LinkedList<>();
		
		try {
			PreparedStatement stmt = con.prepareStatement(
					"SELECT username FROM user WHERE status NOT LIKE 'offline';");
			
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				users.add(rs.getString(1));
				rs.next();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return users;
	}
	
	/**
	 * Retrieve a user's profile.
	 * 
	 * @param con connection to database
	 * @param username
	 * @return profile (if it exists)
	 */
	static Profile getProfile(String username) {
		Profile p = null;
		
		try {
			PreparedStatement stmt = con.prepareStatement(
				"SELECT username, status, wins, losses " +
				"FROM (" +
					"SELECT COUNT(id) AS wins FROM game WHERE won = ?" +
				") w, (" +
					"SELECT COUNT(id) AS losses FROM game WHERE lost = ?" +
				") l, user " +
				"WHERE username = ?");
			
			stmt.setString(1, username);
			stmt.setString(2, username);
			stmt.setString(3, username);
			
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				p = new Profile(rs.getString(1), rs.getString(2), rs.getInt(3),
						rs.getInt(4));
			}
			
			System.out.println(p);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return p;
	}
	
	/**
	 * Changes the user's status.
	 * Possibilities:
	 * * Online
	 * * Busy
	 * * Offline
	 * If in a game, user's status is automatically set to busy.
	 * 
	 * @param con connection to database
	 * @param username
	 * @param status
	 * @return true or false
	 */
	static boolean setStatus(String username, String status) {
		if (!(status.equals("offline") || status.equals("busy") 
			|| status.equals("online"))) {
			return false;
		} else {
			try {
				PreparedStatement stmt = con.prepareStatement(
						"UPDATE user SET status = (SELECT id FROM status WHERE status = ?) " + 
						"WHERE username = ?;");
				
				stmt.setString(1, status);
				stmt.setString(2, status);
				
				stmt.executeUpdate();
				
				return true;
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			return false;
		}
	}
	
	/**
	 * Requests the user's security question.
	 * 
	 * @param con connection to database
	 * @param username
	 * @return user's security question
	 */
	static String forgotPasswordRequest(String username) {
		try {
			PreparedStatement stmt = con.prepareStatement(
					"SELECT question FROM security_question JOIN user " +
					"ON security_question.id = securityQ " +
					"WHERE username LIKE ?");
			
			stmt.setString(1, username);
			
			ResultSet rs = stmt.executeQuery();
			
			return rs.getString(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * Evaluates the answer given to the security question.
	 * 
	 * @param con connection to database
	 * @param username
	 * @param securityAnswer
	 * @return true or false
	 */
	static boolean forgotPasswordAnswer(String username, String securityAnswer) {
		try {
			PreparedStatement stmt = con.prepareStatement(
					"SELECT * FROM user WHERE username = ? AND security_answer = ?");
			
			stmt.setString(1, username);
			stmt.setString(2, securityAnswer);
			
			ResultSet rs = stmt.executeQuery();
			rs.last();
			
			return rs.getRow() == 1;
		} catch (SQLException e) {
			return false;
		}
	}
	
	/**
	 * Retrieves the leaderboard.
	 * 
	 * @param con connection to database
	 * @return a list of usernames, wins, and losses
	 */
	static List<String[]> getLeaderboard() {
		List<String[]> leaderboard = new LinkedList<>();
		
		try {
			PreparedStatement stmt = con.prepareStatement(
					"SELECT username, COUNT(wins) FROM user, game WHERE " + 
					"users.id = winner ORDER BY username ASC;");
			
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				String[] temp = new String[3];
				
				temp[0] = rs.getString(1);
				temp[1] = rs.getInt(2) + "";
				
				leaderboard.add(temp);
			}
			
			stmt = con.prepareStatement(
					"SELECT COUNT(losses) FROM user, game WHERE " + 
					"user.id = winner ORDER BY username ASC;");
			
			rs = stmt.executeQuery();
			
			int i = 0;
			
			while (rs.next()) {
				leaderboard.get(i)[2] = rs.getInt(3) + "";
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return leaderboard;
	}
	
	/**
	 * Adds a game to the database.
	 * 
	 * @param winner
	 * @param loser
	 * @return true or false
	 */
	static boolean newGame(String winner, String loser) {
		try {
			PreparedStatement stmt = con.prepareStatement(
					"SELECT id FROM user WHERE username = ? OR username = ?;");
			
			stmt.setString(1, winner);
			stmt.setString(2, loser);
			
			ResultSet rs = stmt.executeQuery();
			
			int id1 = rs.getInt(1);
			rs.next();
			int id2 = rs.getInt(2);
			
			stmt = con.prepareStatement(
					"INSERT INTO game (winner, loser) VALUES (?, ?);");
			
			stmt.setInt(1, id1);
			stmt.setInt(2, id2);
			
			stmt.executeUpdate();
			
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	/**
	 * States whether a username has already been taken or not.
	 * 
	 * @param username
	 * @return true or false
	 */
	static boolean usernameExists(String username) {
		try {
			PreparedStatement stmt = con.prepareStatement(
					"SELECT * FROM user WHERE username LIKE ?");
			
			stmt.setString(1, username);
			
			ResultSet rs = stmt.executeQuery();
			rs.last();
			
			return rs.getRow() == 1;
		} catch (SQLException e) {
			return false;
		}
	}
	
	/**
	 * Changes username and/or password.
	 * 
	 * @param username
	 * @param changes
	 * @return true or false
	 */
	static boolean changeProfileDetails(String username, String changes) {
		try {
			PreparedStatement stmt = con.prepareStatement(
					"UPDATE user SET ? WHERE username = ?");
			
			stmt.setString(1, changes);
			stmt.setString(2, username);
			
			stmt.executeUpdate();
			
			return true;
		} catch (SQLException e) {
			return false;
		}
	}
}