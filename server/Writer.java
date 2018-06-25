package server;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * This class can be used to write a user profile to the data file.
 * 
 * @author Ben Drawer
 * @version 24 June 2018
 *
 */
class Writer implements Task {
	private Profile profile;
	private Game game;
	private TimedGame timedGame;
	
	/**
	 * Constructor.
	 * 
	 * @param profile profile to be backed up
	 */
	Writer(Profile profile) {
		this.profile = profile;
	}
	
	/**
	 * Constructor.
	 * 
	 * @param game game to be backed up
	 */
	Writer(Game game) {
		this.game = game;
	}
	
	/**
	 * Constructor.
	 * 
	 * @param timedGame timed game to be backed up
	 */
	Writer(TimedGame timedGame) {
		this.timedGame = timedGame;
	}
	
	/**
	 * Run method.
	 */
	@Override
	public synchronized void run() {
		try {
			System.out.println("Backing up data...");
			PrintWriter out = new PrintWriter(new FileWriter("src/server/data", true));
			
			if (profile != null) {
				out.println("Profile//" + profile.getUsername() + "//" + profile.getPassword()
						+ "//" + profile.getSecurityQuestion() + "//" + profile.getSecurityAnswer());
			} else if (game != null) {
				out.println("Game//" + game.toString());
			} else if (timedGame != null) {
				out.println("TimedGame//" + timedGame.toString());
			}
			
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
