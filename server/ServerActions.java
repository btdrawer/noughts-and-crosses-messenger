package server;

import java.net.Socket;
import java.util.List;

import protocol.Constants;
import protocol.Protocol;

/**
 * 
 * @author Ben Drawer
 * @version 21 October 2018
 *
 */
class ServerActions {
	private String[] outArr;
	private Protocol protocol;
	private static final String CONNECT = Constants.CONNECT,
			GET_SECURITY_QUESTIONS = Constants.GET_SECURITY_QUESTIONS,
			GET_ONLINE_USERS = Constants.GET_ONLINE_USERS,
			GET_LEADERBOARD = Constants.GET_LEADERBOARD,
			GET_TIMED_LEADERBOARD = Constants.GET_TIMED_LEADERBOARD;
	
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
		
		return protocol.transmit(CONNECT, true, "Connection test successful.");
	}
	
	String getSecurityQuestions() {
		List<String> securityQuestions = Database.getSecurityQuestions();
		StringBuilder s = new StringBuilder();
		
		for (String sv : securityQuestions) {
			s.append(sv + "//");
		}
		
		return protocol.transmit(GET_SECURITY_QUESTIONS, true, s.toString());
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
		
		return protocol.transmit(GET_ONLINE_USERS, true, sb.toString());
	}
	
	/**
	 * Provides the usernames, gross wins and net wins of the n highest-achieving
	 * users.
	 * 
	 * @param input input[0] gives the number of users needed
	 * @return
	 */
	String leaderboard(String[] input) {
		List<String[]> leaderboardList = Database.getLeaderboard(Integer.parseInt(input[0]));
		int size = leaderboardList.size();
		
		if (size > 0) {
			outArr = new String[size * 3];
			int i = 0;
			
			for (String[] s : leaderboardList) {
				outArr[i] = s[0];
				outArr[i+1] = s[1];
				outArr[i+2] = s[2];
				
				i += 3;
			}
			
			return protocol.transmit(GET_LEADERBOARD, true, outArr);
		} else {
			return protocol.transmit(GET_LEADERBOARD, false, "It's lonely in here!");
		}
	}
	
	String timedLeaderboard(String[] input) {
		List<String[]> timedLeaderboardList = Database.getTimedLeaderboard(Integer.parseInt(input[0]));
		int size = timedLeaderboardList.size();
		
		if (size > 0) {
			outArr = new String[size * 2];
			int i = 0;
			
			for (String[] s : timedLeaderboardList) {
				outArr[i] = s[0];
				outArr[i+1] = s[1];
				
				i += 2;
			}
		} else {
			return protocol.transmit(GET_TIMED_LEADERBOARD, false, "It's lonely in here!");
		}
		
		return protocol.transmit(GET_TIMED_LEADERBOARD, true, outArr);
	}
}
