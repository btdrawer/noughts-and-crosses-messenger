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

/**
 * Controller for the leaderboard pane.
 * 
 * @author Ben Drawer
 * @version 20 September 2018
 *
 */
class HomeController extends Controller {
	@FXML protected ListView<String> onlineUsers;
	@FXML protected Button settings, signout;
	@FXML protected Text responseText;
	private ObservableList<String> onlineUserList;
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
			client.sendMessage("requestusers", client.getUsername());
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
		else if (action.equals("challenge"))
			receiveChallenge(input);
		else if (action.equals("challengeresponse"))
			challengeResponseHandler(input);
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
			Main.changeScene("Login");
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
		
		String[] outArr = new String[4];
		outArr[1] = challenger;
		outArr[2] = recipient;
		
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				Alert alert = new Alert(AlertType.CONFIRMATION, challenger + " has challenged you." +
						" Accept?", ButtonType.YES, ButtonType.NO);
				Optional<ButtonType> result = alert.showAndWait();
				
				if (result.isPresent() && result.get() == ButtonType.YES) {
					outArr[0] = "true";
					
					String[] data = {recipient, challenger, 1 + ""};
					Main.changeScene("Board", data);
				} else if (result.isPresent() && result.get() == ButtonType.NO)
					outArr[0] = "false";
				
				try {
					client.sendMessage("challengeresponse", outArr);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
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
		String[] outArr = {onlineUsers.getSelectionModel().getSelectedItem()};
		
		if (event.getClickCount() > 1)
			client.sendMessage("viewprofile", outArr);
	}
	
	/**
	 * Called by the processInput method.
	 * Changes the scene to a user's profile.
	 * 
	 * @param input data for the user's profile
	 */
	private void viewProfile(String[] input) {
		if (input[0].equals("true")) {
			Main.changeScene("Profile", input);
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
		Main.changeScene("EditProfile");
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
		client.sendMessage("signout", outArr);
	}
	
	/**
	 * Handles the response received from the server after a player
	 * has been challenged.
	 * 
	 * @param input
	 * @throws IOException 
	 */
	private void challengeResponseHandler(String[] input) {
		if (input[0].equals("true")) {
			try {
				String[] data = {client.getUsername(), input[1], 0 + ""};
				Main.changeScene("Board", data);
				client.sendMessage("newgame", data);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else
			responseText.setText(input[2]);
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
			client.sendMessage("challenge", outArr);
			responseText.setText("Challenging random user: " + username + "...");
		}
	}
}
