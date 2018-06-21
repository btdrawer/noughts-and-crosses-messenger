package client;

import java.io.IOException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

/**
 * Controller for the leaderboard pane.
 * 
 * @author Ben Drawer
 * @version 21 June 2018
 *
 */
public class LeaderboardController extends Controller {
	@FXML protected ListView<String> onlineUsers;
	@FXML protected Button settings;
	@FXML protected Button signout;
	private ObservableList<String> onlineUserList;
	private ActionEvent currentEvent;
	private static Client client = Main.getClient();
	
	/**
	 * Initialize method.
	 * Loads initial list of online users.
	 * After this, it initialises a thread that continually listens
	 * for new available users and game challenges.
	 * 
	 * @throws IOException
	 */
	public void initialize() {
		try {
			String[] outArr = {client.getUsername()};
			client.sendMessage("requestusers", outArr);
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
	void processInput(String action, String[] input) {
		if (action.equals("signedin"))
			this.addToOnlineUserList(input[0]);
		else if (action.equals("signedout"))
			this.removeFromOnlineUserList(input[0]);
		else if (action.equals("requestusers"))
			compileUserList(input);
		else if (action.equals("signout")) {
			try {
				signOut(input);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Adds a user to the online user list and then refreshes the
	 * ListView to represent that change to the user.
	 * 
	 * @param username user to be added
	 */
	void addToOnlineUserList(String username) {
		onlineUserList.add(username);
		onlineUsers.setItems(onlineUserList);
	}
	
	/**
	 * Removes a user from the online user list and then refreshes
	 * the ListView to represent that change to the user.
	 * 
	 * @param username user to be removed
	 */
	void removeFromOnlineUserList(String username) {
		onlineUserList.remove(username);
		onlineUsers.setItems(onlineUserList);
	}
	
	/**
	 * Compiles the list of online users present when the user signs in.
	 * 
	 * @param input list of online users
	 */
	void compileUserList(String[] input) {
		for (String s : input) {
			onlineUserList.add(s);
		}
		
		onlineUsers.setItems(onlineUserList);
	}
	
	/**
	 * Sends the user to the login pane, having been signed out.
	 * 
	 * @param input response from server indicating whether sign out was successful
	 * @throws IOException
	 */
	private void signOut(String[] input) throws IOException {
		if (input[0].equals("true")) {
			client.setUsername("");
			Main.changeScene("Login", 325, 350, currentEvent);
		}
	}
	
	/**
	 * Handler for the edit profile button.
	 * 
	 * @param event
	 */
	@FXML
	protected void editProfileButton(ActionEvent event) {
		//TODO
	}
	
	/**
	 * Handler for the sign out button.
	 * 
	 * @param event
	 * @throws IOException
	 */
	@FXML
	protected void signOutButton(ActionEvent event) throws IOException {
		this.currentEvent = event;
		String[] outArr = {client.getUsername()};
		client.sendMessage("signout", outArr);
	}
	
	/**
	 * Handler for the 'Play a random opponent' button.
	 * 
	 * @param event
	 */
	@FXML
	protected void playRandom(ActionEvent event) {
		//TODO
	}
}
