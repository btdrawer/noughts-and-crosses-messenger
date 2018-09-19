package server;

import java.net.Socket;
import java.util.List;

import protocol.Protocol;

/**
 * 
 * @author Ben Drawer
 * @version 20 September 2018
 *
 */
class ServerActions {
	private String[] outArr;
	private Protocol protocol;
	
	/**
	 * Constructor.
	 * 
	 * @param clientSocket
	 */
	ServerActions(Socket clientSocket) {
		this.protocol = Main.getProtocol();
		this.outArr = new String[2];
	}
	
	/**
	 * Tests the connection.
	 * 
	 * @return output stating that the connection has been successful
	 */
	String connect() {
		Main.joinedServer();
		System.out.println("Connection with client established.\n" +
				"numberOfOnlineUsers: " + Main.getNumberOfOnlineUsers());
		
		outArr = new String[2];
		outArr[0] = "true";
		outArr[1] = "Connection test successful.";
		
		return protocol.transmit("connect", outArr);
	}
	
	String getSecurityQuestions() {
		List<String> securityQuestions = Database.getSecurityQuestions();
		StringBuilder s = new StringBuilder();
		
		for (String sv : securityQuestions) {
			s.append(sv + "//");
		}
		
		return protocol.transmit("securityquestions", s.toString());
	}
	
	/**
	 * Builds a String of online users.
	 * 
	 * @return output containing information of all online users
	 */
	String requestUsers(String[] input) {
		StringBuilder sb = new StringBuilder();
		
		List<String> users = Database.getUsers();
		
		for (String s : users) {
			if (!s.equals((input[0]))) {
				sb.append(s + "//");
			}
		}
		
		return protocol.transmit("requestusers", sb.toString());
	}
	
	/**
	 * Provides the usernames, gross wins and net wins of the n highest-achieving
	 * users.
	 * 
	 * @param input input[0] gives the number of users needed
	 * @return
	 */
	String leaderboard(String[] input) {
		List<String[]> leaderboardList = Database.getLeaderboard();
		int size = leaderboardList.size();
		
		if (size > 0) {
			outArr = new String[size * 3 + 1];
			outArr[0] = "true";
			int i = 0;
			
			for (String[] s : leaderboardList) {
				outArr[i] = s[0];
				outArr[i+1] = s[1];
				outArr[i+2] = s[2];
				
				i += 3;
			}
		} else {
			outArr = new String[2];
			outArr[0] = "false";
			outArr[1] = "It's lonely in here!";
		}
		
		return protocol.transmit("leaderboard", outArr);
	}
	
	String timedLeaderboard(String[] input) {
		return null;
	}
}
