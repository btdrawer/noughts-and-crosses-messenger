package client;

import java.io.IOException;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;

/**
 * Controller for the leaderboard pane.
 * 
 * @author Ben Drawer
 * @version 21 June 2018
 *
 */
public class HomeController extends Controller {
	@FXML protected ListView<String> onlineUsers;
	@FXML protected Button settings;
	@FXML protected Button signout;
	private ObservableList<String> onlineUserList;
	private Event currentEvent;
	private static Client client = Main.getClient();
	
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
		this.onlineUserList = FXCollections.<String>observableArrayList();
		
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
	@Override
	void processInput(String action, String[] input) {
		if (action.equals("signedin"))
			this.addToOnlineUserList(input[0]);
		else if (action.equals("signedout"))
			this.removeFromOnlineUserList(input[0]);
		else if (action.equals("requestusers"))
			compileUserList(input);
		else if (action.equals("viewprofile"))
			viewProfile(input);
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
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				onlineUserList.add(username);
				onlineUsers.setItems(onlineUserList);
			}
		});
	}
	
	/**
	 * Removes a user from the online user list and then refreshes
	 * the ListView to represent that change to the user.
	 * 
	 * @param username user to be removed
	 */
	void removeFromOnlineUserList(String username) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				onlineUserList.remove(username);
				onlineUsers.setItems(onlineUserList);
			}
		});
	}
	
	/**
	 * Compiles the list of online users present when the user signs in.
	 * 
	 * @param input list of online users
	 */
	void compileUserList(String[] input) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				for (String s : input) {
					onlineUserList.add(s);
				}
				
				onlineUsers.setItems(onlineUserList);
			}
		});
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
	 * When a person's username is clicked, this method will be called,
	 * which sends a message to the server requesting the selected user's profile.
	 * 
	 * @param event
	 * @throws IOException
	 */
	@FXML
	protected void selectedProfile(MouseEvent event) throws IOException {
		this.currentEvent = event;
		String[] outArr = {onlineUsers.getSelectionModel().getSelectedItem()};
		client.sendMessage("viewprofile", outArr);
	}
	
	private void viewProfile(String[] input) {
		if (input[0].equals("true")) {
			Main.changeScene("Login", 325, 350, currentEvent, input);
		} else {
			//TODO Error occurred
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
