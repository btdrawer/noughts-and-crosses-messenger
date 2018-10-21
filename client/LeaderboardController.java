package client;

import java.io.IOException;
import java.util.ArrayList;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

import protocol.Constants;

/**
 * Controller for the leaderboard pane.
 * 
 * @author Ben Drawer
 * @version 21 October 2018
 *
 */
public class LeaderboardController extends HomeController {
	@FXML protected ListView<String> onlineUsers;
	@FXML protected GridPane leaderboard, timedLeaderboard;
	@FXML protected Button settings;
	@FXML protected Button signout;
	private static Client client = Main.getClient();
	@FXML protected ArrayList<Text> username, gross, net;
	private static final String GET_LEADERBOARD = Constants.GET_LEADERBOARD,
			GET_TIMED_LEADERBOARD = Constants.GET_TIMED_LEADERBOARD;
	
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
			client.sendMessage(GET_LEADERBOARD, 6 + "");
			client.sendMessage(GET_TIMED_LEADERBOARD, 5 + "");
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
	void processInput(String action, boolean result, String[] input) {
		switch (action) {
			case GET_LEADERBOARD:
				setLeaderboard(result, input);
				break;
			case GET_TIMED_LEADERBOARD:
				setTimedLeaderboard(result, input);
				break;
			default:
				super.processInput(action, result, input);
		}
	}
	
	/**
	 * Sets the content of the leaderboard.
	 * 
	 * @param input user's usernames, gross and net wins to be displayed
	 */
	private void setLeaderboard(boolean result, String[] input) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				if (result) {
					int j = 0;
					
					for (int i = 0; i < input.length; i += 3) {
						leaderboard.add(new Text(input[i]), 0, j);
						leaderboard.add(new Text(input[i+1]), 1, j);
						leaderboard.add(new Text(input[i+2]), 2, j);
						
						j += 1;
					}
				}
			}
		});
	}
	
	/**
	 * Sets the content of the timed leaderboard.
	 * 
	 * @param input
	 */
	private void setTimedLeaderboard(boolean result, String[] input) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				if (result) {
					int j = 1;
					
					for (int i = 0; i < input.length; i += 2) {
						timedLeaderboard.add(new Text(input[i]), 0, j);
						timedLeaderboard.add(new Text(input[i+1]), 1, j);
						
						j += 1;
					}
				}
			}
		});
	}
}
