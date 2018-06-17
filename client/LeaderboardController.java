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
 * @version 15 June 2018
 *
 */
public class LeaderboardController extends Controller {
	private Stage primaryStage;
	@FXML protected ListView<String> onlineUsers;
	@FXML protected Button settings;
	@FXML protected Button signout;
	private String[] response;
	private ObservableList<String> onlineUserList;
	private ActionEvent currentEvent;
	
	/**
	 * Constructor.
	 * Loads initial list of online users.
	 * After this, it initialises a thread that continually listens
	 * for new available users and game challenges.
	 * 
	 * @throws IOException
	 */
	public void initialize() throws IOException {
		super.initialize();
		
		String[] outArr = {super.getUsername()};
		super.sendMessage("requestusers", outArr);
		this.response = super.getResponse();
		this.onlineUserList = FXCollections.observableArrayList();
		
		for (String s : response) {
			onlineUserList.add(s);
		}
		
		onlineUsers.setItems(onlineUserList);
	}
	
	@Override
	void processInput(String action, String[] input) {
		super.processInput(action, input);
		
		if (action.equals("signout"))
			try {
				signOut(input);
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	
	/**
	 * Adds a user to the online user list and then refreshes the
	 * ListView to represent that change to the user.
	 * 
	 * @param username user to be added
	 */
	@Override
	void addToOnlineUserList(String username) {
		super.addToOnlineUserList(username);
		onlineUsers.setItems(onlineUserList);
	}
	
	/**
	 * Removes a user from the online user list and then refreshes
	 * the ListView to represent that change to the user.
	 * 
	 * @param username user to be removed
	 */
	@Override
	void removeFromOnlineUserList(String username) {
		super.removeFromOnlineUserList(username);
		onlineUsers.setItems(onlineUserList);
	}
	
	/**
	 * Sends the user to the login pane, having been signed out.
	 * 
	 * @param input response from server indicating whether sign out was successful
	 * @throws IOException
	 */
	private void signOut(String[] input) throws IOException {
		if (response[0].equals("true")) {
			super.setUsername("");
			
			Parent root = FXMLLoader.load(getClass().getResource("/fxml/Login.fxml"));
			
			primaryStage = (Stage) ((Node) currentEvent.getSource()).getScene().getWindow();
			primaryStage.setScene(new Scene(root, 325, 350));
			primaryStage.show();
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
		String[] outArr = {super.getUsername()};
		super.sendMessage("signout", outArr);
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
