package server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Server class.
 * 
 * @author Ben Drawer
 * @version 30 May 2018
 *
 */
class Server {
	private static LinkedBlockingQueue<Task> taskQueue = new LinkedBlockingQueue<>();
	private static Map<String, Profile> users = new HashMap<>();
	private static Map<String, LinkedList<Game>> games = new HashMap<>();
	private static Map<Short, String> securityQuestions = new HashMap<>();
	private static int quantity, port, numberOfOnlineUsers;
	private static String ip;
	
	/**
	 * 
	 * @return request queue
	 */
	static LinkedBlockingQueue<Task> getTaskQueue() {
		return taskQueue;
	}
	
	/**
	 * 
	 * @return users
	 */
	static Map<String, Profile> getUsers() {
		return users;
	}
	
	/**
	 * 
	 * @return games
	 */
	static Map<String, LinkedList<Game>> getGames() {
		return games;
	}
	
	/**
	 * 
	 * @return security questions
	 */
	static Map<Short, String> getSecurityQuestions() {
		return securityQuestions;
	}
	
	/**
	 * 
	 * @return number of online users
	 */
	static int getNumberOfOnlineUsers() {
		return numberOfOnlineUsers;
	}
	
	/**
	 * Decrements the number of online users when one leaves.
	 */
	static void leftServer() {
		numberOfOnlineUsers -= 1;
	}
	
	/**
	 * 
	 * @param ip IP address
	 * @return whether the input represents a valid IP address
	 */
	private static boolean isValidIp(String ip) {
		return ip.matches("(([2][0-5][0-5])|([0-1][0-9][0-9]))[.]"
				+ "(([2][0-5]?[0-5]?)|([0-1][0-9]?[0-9]?))[.]" + 
				"(([2][0-5]?[0-5]?)|([0-1][0-9]?[0-9]?))[.]" +
				"(([2][0-5]?[0-5]?)|([0-1][0-9]?[0-9]?))");
	}
	
	/**
	 * Asks the user for inputs to set up the server.
	 */
	private static void setUp() {
		System.out.println("Noughts and Crosses set up\nEnter the maximum" + 
				" number of clients you would like at any one time:");
		
		Scanner s = new Scanner(System.in);
		int temp;
		
		while(s.hasNextInt()) {
			temp = s.nextInt();
			
			if (temp > 0) {
				quantity = temp;
				break;
			} else {
				throw new IllegalArgumentException("Maximum number must be greater than 0!");
			}
		}
		
		System.out.println("Enter the IP address your server will listen from.\n"
				+ "If you want to play locally, enter '127.0.0.1':");
		
		s = new Scanner(System.in);
		
		while(s.hasNext()) {
			String tempStr = s.next();
			
			if (isValidIp(tempStr)) {
				ip = tempStr;
				break;
			} else {
				throw new IllegalArgumentException("Not a valid IP address.");
			}
		}
		
		System.out.println("Enter the port number your server will listen from (e.g., 8080):");
		
		s = new Scanner(System.in);
		
		while(s.hasNextInt()) {
			temp = s.nextInt();
			
			if (temp > 0 && temp < 65343) {
				port = temp;
				break;
			} else if (temp <= 0) {
				throw new IllegalArgumentException("Port number cannot be negative.");
			} else {
				throw new IllegalArgumentException("Port number is too large.");
			}
		}
		
		System.out.println("Thank you.");
	}
	
	/**
	 * 
	 * Main method.
	 */
	public static void main(String[] args) {
		setUp();
		ServerSocket serverSocket = null;
		
		try {
			serverSocket = new ServerSocket(port, 0, InetAddress.getByName(ip));
			
			ThreadPool threadPool = new ThreadPool(quantity);
			threadPool.add(new Writer(50000));
			
			System.out.println("Server is up and running!");
			
			while(true) {
				if (numberOfOnlineUsers < quantity) {
					Socket clientSocket = serverSocket.accept();
					threadPool.add(new Request(clientSocket));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				serverSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
