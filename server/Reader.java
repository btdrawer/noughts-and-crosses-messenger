package server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Map;

/**
 * This thread reads user profiles from the data file.
 * 
 * @author Ben Drawer
 * @version 25 June 2018
 *
 */
class Reader implements Task {
	private static Map<String, Profile> users = Server.getUsers();
	private static Map<String, LinkedList<Game>> games = Server.getGames();
	private static Map<Short, String> securityQuestions = Server.getSecurityQuestions();
	
	/**
	 * Run method.
	 * When initiated, the writer draws down the information from the
	 * data and security files.
	 * After this, it periodically backs up server data to the data file,
	 * and goes to sleep in between for the allotted interval time.
	 */
	@Override
	public void run() {
		try {
			File dataFile = new File("src/server/data");
			BufferedReader in = new BufferedReader(new FileReader(dataFile));
			
			String s;
			String[] sArr;
			
			System.out.println("Reading data file...");
			
			while((s = in.readLine()) != null) {
				sArr = s.split("//");
				String type = sArr[0];
				
				if (type.equals("Profile")) {
					users.put(sArr[1], new Profile(sArr[1], sArr[2], 
							Short.parseShort(sArr[3]), sArr[4], 0, 0));
				} else if (type.equals("Game")) {
					String player1 = sArr[1];
					String player2 = sArr[2];
					int winner = Integer.parseInt(sArr[3]);
					
					if (games.containsKey(sArr[1] + "//" + sArr[2])) {
						games.put(player1 + "//" + player2, new LinkedList<Game>());
					} else {
						games.get(player1 + "//" + player2).add(new Game(
								player1, player2, winner));
					}
					
					if (winner == 1) {
						users.get(player1).addWin();
						users.get(player2).addLoss();
					} else if (winner == 2) {
						users.get(player2).addWin();
						users.get(player1).addLoss();
					}
				} else if (type.equals("TimedGame")) {
					//TODO
				}
			}
			
			in.close();
			
			in = new BufferedReader(new FileReader("src/server/securityQuestions"));
			
			System.out.println("Getting security questions...");
			
			while((s = in.readLine()) != null) {
				sArr = s.split("/");
				
				securityQuestions.put(Short.parseShort(sArr[0]), sArr[1]);
			}
			
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}
}
