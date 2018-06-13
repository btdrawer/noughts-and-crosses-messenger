package server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

/**
 * This thread reads usernames from a data file and periodically writes to it.
 * 
 * @author Ben Drawer
 * @version 13 June 2018
 *
 */
class Writer implements Task {
	private static Map<String, Profile> users = Server.getUsers();
	private static Map<Short, String> securityQuestions = Server.getSecurityQuestions();
	private int interval;
	
	/**
	 * Constructor.
	 * 
	 * @param interval How frequently you want the data to be backed up.
	 */
	Writer(int interval) {
		this.interval = interval;
	}
	
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
				sArr = s.split("/");
				
				users.put(sArr[0], new Profile(sArr[0], Request.getMD5(sArr[1]), 
						Short.parseShort(sArr[2]), sArr[3], Integer.parseInt(sArr[4]),
						Integer.parseInt(sArr[5])));
			}
			
			in.close();
			
			in = new BufferedReader(new FileReader("src/server/securityQuestions"));
			
			System.out.println("Getting security questions...");
			
			while((s = in.readLine()) != null) {
				sArr = s.split("/");
				
				securityQuestions.put(Short.parseShort(sArr[0]), sArr[1]);
			}
			
			in.close();
			
			while(true) {
				System.out.println("Backing up data...");
				PrintWriter out = new PrintWriter(new FileWriter(dataFile, true));
				
				for (Profile p : users.values()) {
					out.println(p.getUsername() + "/" + p.getPassword() + "/" +
							p.getSecurityQuestion() + "/" + p.getSecurityAnswer() + "/" +
							p.getWins() + "/" + p.getLosses());
				}
				
				out.close();
				
				Thread.sleep(interval);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
}
