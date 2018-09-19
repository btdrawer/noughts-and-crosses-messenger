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
 * Main server class.
 * 
 * @author Ben Drawer
 * @version 17 September 2018
 *
 */
class Main {
	private static LinkedBlockingQueue<Request> requestQueue = new LinkedBlockingQueue<>();
	private static Map<String, Socket> sockets = new HashMap<>();
	private static List<Game> openGames = new LinkedList<>();
	private static int quantity, port, numberOfOnlineUsers;
	private static String ip;
	private static Protocol protocol;
	
	/**
	 * 
	 * @return request queue
	 */
	static LinkedBlockingQueue<Request> getRequestQueue() {
		return requestQueue;
	}
	
	/**
	 * 
	 * @return mapping of users to sockets
	 */
	static Map<String, Socket> getSockets() {
		return sockets;
	}
	
	/**
	 * Finds an open game for a specific player.
	 * 
	 * @param player
	 * @return game
	 */
	static Game findGame(String player) {
		for (Game g : openGames) {
			for (String s : g.getPlayers()) {
				if (s.equals(player))
					return g;
			}
		}
		
		return null;
	}
	
	/**
	 * Opens a new game between two players.
	 * 
	 * @param players
	 * @return true or false
	 */
	static void newGame(String[] players) {
		boolean inGame = false;
		
		for (Game g : openGames) {
			for (String s : g.getPlayers()) {
				if (s.equals(players[0]) || s.equals(players[1]))
					inGame = true;
			}
		}
		
		if (!inGame)
			openGames.add(new Game(players[0], players[1]));
	}
	
	/**
	 * Adds a character to an open game.
	 * 
	 * @param player
	 * @param x x-position of new character
	 * @param y y-position of new character
	 * @param c character to be placed
	 * @return true or false
	 */
	static void addChar(String player, int x, int y, char c) {
		for (Game g : openGames) {
			for (String s : g.getPlayers()) {
				if (s.equals(player)) {
					g.addChar(x, y, c);
					g.addTurn();
				}
			}
		}
	}
	
	/**
	 * Removes a game from the open games list when finished.
	 * 
	 * @param player
	 */
	static void gameFinished(String player) {
		int index = -1;
		
		for (Game g : openGames) {
			for (String s : g.getPlayers()) {
				if (s.equals(player))
					index = openGames.indexOf(g);
			}
		}
		
		openGames.remove(index);
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
		new Thread() {
			@Override
			public void run() {
				System.out.println("Broadcasting message to online clients...");
				
				for (Socket s : sockets.values()) {
					try {
						s.getOutputStream().write(protocol.transmit(input[0], input[1]).getBytes());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
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
		
		System.out.println("Enter the database URL:");
		
		s = new Scanner(System.in);
		
		while(s.hasNext()) {
			String dbUrl, dbUsername;
			dbUrl = s.next();
			
			System.out.println("Database username:");
			
			s = new Scanner(System.in);
			
			while(s.hasNext()) {
				dbUsername = s.next();
				
				System.out.println("Database password:");
				
				s = new Scanner(System.in);
				
				while(s.hasNext()) {
					boolean isConnected = Database.setConnection(dbUrl, dbUsername, s.next());
					
					if (isConnected) {
						System.out.println("Successfully connected to database!");
					} else {
						System.out.println("Database connection failed.");
					}
					
					break;
				}
				
				break;
			}
			
			break;
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
			ThreadPool threadPool = new ThreadPool(quantity);
			
			System.out.println("Server is up and running!");
			
			while(true) {
				if (numberOfOnlineUsers < quantity) {
					Socket clientSocket = serverSocket.accept();
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
