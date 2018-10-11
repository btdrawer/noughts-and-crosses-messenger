package client;

import java.io.IOException;
import java.util.Optional;
import java.util.Random;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

import protocol.Constants;

/**
 * Controller for the leaderboard pane.
 * 
 * @author Ben Drawer
 * @version 11 October 2018
 *
 */
class HomeController extends Controller {
	@FXML protected ListView<String> onlineUsers;
	@FXML protected Button settings, signout;
	@FXML protected Text responseText;
	private ObservableList<String> onlineUserList;
	private static Client client = Main.getClient();
	private static final String SIGNED_IN = Constants.SIGNED_IN,
			CHANGED_USERNAME = Constants.CHANGED_USERNAME,
			SIGNED_OUT = Constants.SIGNED_OUT,
			GET_ONLINE_USERS = Constants.GET_ONLINE_USERS,
			VIEW_PROFILE = Constants.VIEW_PROFILE,
			SEND_CHALLENGE = Constants.SEND_CHALLENGE,
			SEND_CHALLENGE_PINGBACK = Constants.SEND_CHALLENGE_PINGBACK,
			RESPOND_TO_CHALLENGE = Constants.RESPOND_TO_CHALLENGE,
			SIGN_OUT = Constants.SIGN_OUT,
			NEW_GAME = Constants.NEW_GAME,
			SIGN_IN_PANEL = PanelConstants.SIGN_IN_PANEL,
			PROFILE_PANEL = PanelConstants.PROFILE_PANEL,
			EDIT_PROFILE_PANEL = PanelConstants.EDIT_PROFILE_PANEL,
			BOARD_PANEL = PanelConstants.BOARD_PANEL;
	
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
			client.sendMessage(GET_ONLINE_USERS, client.getUsername());
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
			case SIGNED_IN:
				this.addToOnlineUserList(input[0]);
				break;
			case SIGNED_OUT:
				this.removeFromOnlineUserList(input[0]);
				break;
			case CHANGED_USERNAME:
				changedUsernameHandler(input);
				break;
			case GET_ONLINE_USERS:
				compileUserList(input);
				break;
			case VIEW_PROFILE:
				viewProfile(result, input);
				break;
			case SEND_CHALLENGE:
				receiveChallenge(input);
				break;
			case SEND_CHALLENGE_PINGBACK:
				sendChallengePingbackHandler(result, input);
				break;
			case RESPOND_TO_CHALLENGE:
				challengeResponseHandler(result, input);
				break;
			case SIGN_OUT:
				try {
					signOut(result, input);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				break;
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
	 * Called when someone on the server changes their username.
	 * 
	 * [0] = old username
	 * [1] = new username
	 * 
	 * @param input
	 */
	private void changedUsernameHandler(String[] input) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				if (!client.getUsername().equals(input[0])) {
					onlineUserList.remove(input[0]);
					onlineUserList.add(input[1]);
					onlineUsers.setItems(onlineUserList);
				}
			}
		});
	}
	
	/**
	 * Sends the user to the login pane, having been signed out.
	 * 
	 * @param input response from server indicating whether sign out was successful
	 * @throws IOException
	 */
	private void signOut(boolean result, String[] input) throws IOException {
		if (result) {
			client.setUsername("");
			Main.changeScene(SIGN_IN_PANEL);
		}
	}
	
	/**
	 * Called when a client receives a challenge.
	 * 
	 * @param input
	 * @throws IOException
	 */
	private void receiveChallenge(String[] input) {
		String challenger = input[0];
		String recipient = client.getUsername();
		
		String[] outArr = new String[2];
		outArr[0] = challenger;
		outArr[1] = recipient;
		
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				Alert alert = new Alert(AlertType.CONFIRMATION, challenger + " has challenged you." +
						" Accept?", ButtonType.YES, ButtonType.NO);
				Optional<ButtonType> response = alert.showAndWait();
				
				boolean result = false;
				
				if (response.isPresent() && response.get() == ButtonType.YES) {
					String[] data = {recipient, challenger, 1 + ""};
					result = true;
					Main.changeScene(BOARD_PANEL, data);
				}
				
				try {
					client.sendMessage(RESPOND_TO_CHALLENGE, result, outArr);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * After a user sends a challenge, the server provides a response
	 * stating whether or not that user is available.
	 * 
	 * @param input [0] = response text
	 */
	private void sendChallengePingbackHandler(boolean result, String[] input) {
		if (!result)
			responseText.setText(input[0]);
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
		if (event.getClickCount() > 1)
			client.sendMessage(VIEW_PROFILE, onlineUsers.getSelectionModel().getSelectedItem());
	}
	
	/**
	 * Called by the processInput method.
	 * Changes the scene to a user's profile.
	 * 
	 * @param input data for the user's profile
	 */
	private void viewProfile(boolean result, String[] input) {
		if (result) {
			Main.changeScene(PROFILE_PANEL, input);
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
		Main.changeScene(EDIT_PROFILE_PANEL);
	}
	
	/**
	 * Handler for the sign out button.
	 * 
	 * @param event
	 * @throws IOException
	 */
	@FXML
	protected void signOutButton(ActionEvent event) throws IOException {
		String[] outArr = {client.getUsername()};
		client.sendMessage(SIGN_OUT, outArr);
	}
	
	/**
	 * Handles the response received from the server after a player
	 * has been challenged.
	 * 
	 * @param input
	 * @throws IOException 
	 */
	private void challengeResponseHandler(boolean result, String[] input) {
		if (result) {
			try {
				String[] data = {client.getUsername(), input[0], 0 + ""};
				Main.changeScene(BOARD_PANEL, data);
				client.sendMessage(NEW_GAME, data);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else
			responseText.setText(input[1]);
	}
	
	/**
	 * Handler for the 'Play a random opponent' button.
	 * 
	 * @param event
	 * @throws IOException 
	 */
	@FXML
	protected void playRandom(ActionEvent event) throws IOException {
		if (onlineUserList.size() == 0) {
			responseText.setText("It's lonely in here!");
		} else {
			int userNum = new Random().nextInt(onlineUserList.size());
			String username = onlineUserList.get(userNum);
			
			String[] outArr = {client.getUsername(), username};
			client.sendMessage(SEND_CHALLENGE, outArr);
			responseText.setText("Challenging random user: " + username + "...");
		}
	}
}
