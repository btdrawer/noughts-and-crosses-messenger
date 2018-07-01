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

import protocol.Protocol;

/**
 * Server class.
 * 
 * @author Ben Drawer
 * @version 24 June 2018
 *
 */
class Server {
	private static LinkedBlockingQueue<Task> taskQueue = new LinkedBlockingQueue<>();
	private static Map<String, Profile> users = new HashMap<>();
	private static Map<String, LinkedList<Game>> games = new HashMap<>();
	private static Map<Short, String> securityQuestions = new HashMap<>();
	private static List<Socket> sockets = new LinkedList<>();
	private static int quantity, port, numberOfOnlineUsers;
	private static String ip;
	private static Protocol protocol;
	
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
	 * 
	 * @return protocol
	 */
	static Protocol getProtocol() {
		return protocol;
	}
	
	/**
	 * Increments the number of online users when one joins.
	 */
	static void joinedServer() {
		numberOfOnlineUsers += 1;
	}
	
	/**
	 * Decrements the number of online users when one leaves.
	 */
	static void leftServer() {
		numberOfOnlineUsers -= 1;
	}
	
	/**
	 * Broadcasts message to all clients.
	 * 
	 * @param input
	 */
	static void broadcastMessage(String[] input) {
		Thread broadcaster = new Thread() {
			@Override
			public void run() {
				System.out.println("Broadcasting message to online clients...");
				
				for (Socket s : sockets) {
					try {
						s.getOutputStream().write(protocol.transmit(input[0], input[1]).getBytes());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		};
		
		broadcaster.start();
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
			protocol = new Protocol();
			ThreadPool threadPool = new ThreadPool(quantity * 2);
			threadPool.add(new Reader());
			
			System.out.println("Server is up and running!");
			
			while(true) {
				if (numberOfOnlineUsers < quantity) {
					Socket clientSocket = serverSocket.accept();
					sockets.add(clientSocket);
					Request newRequest = new Request(clientSocket);
					threadPool.add(newRequest);
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
