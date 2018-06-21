package client;

import java.io.IOException;
import java.util.ArrayList;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;

/**
 * Controller for the leaderboard pane.
 * 
 * @author Ben Drawer
 * @version 21 June 2018
 *
 */
public class LeaderboardController extends HomeController {
	@FXML protected ListView<String> onlineUsers;
	@FXML protected Button settings;
	@FXML protected Button signout;
	private static Client client = Main.getClient();
	@FXML protected ArrayList<Text> username, gross, net;
	
	/**
	 * Initialize method.
	 * Loads initial list of online users.
	 * After this, it initialises a thread that continually listens
	 * for new available users and game challenges.
	 * 
	 * @throws IOException
	 */
	@Override
	public void initialize() {
		super.initialize();
		
		try {
			String[] outArr = {6 + ""};
			client.sendMessage("leaderboard", outArr);
			
			outArr[0] = 5 + "";
			client.sendMessage("timedleaderboard", outArr);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Processes input from server.
	 * 
	 * @param action action to be undertaken
	 * @param input information associated with action
	 * @throws IOException
	 */
	@Override
	void processInput(String action, String[] input) {
		if (action.equals("leaderboard"))
			setLeaderboard(input);
		else if (action.equals("timedleaderboard"))
			setTimedLeaderboard(input);
		else
			super.processInput(action, input);
	}
	
	/**
	 * Sets the content of the leaderboard.
	 * 
	 * @param input user's usernames, gross and net wins to be displayed
	 */
	private void setLeaderboard(String[] input) {
		if (input[0].equals("true")) {
			int n = input.length / 3 - 1;
			
			for (int i = 1; i < n; i += 3) {
				username.get(i).setText(input[i]);
				gross.get(i).setText(input[i+1]);
				net.get(i).setText(input[i+2]);
			}
		}
	}
	
	private void setTimedLeaderboard(String[] input) {
		//TODO
	}
}
