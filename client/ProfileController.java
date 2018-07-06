package client;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.text.Text;

/**
 * Controller for the Profile scene.
 * 
 * @author Ben Drawer
 * @version 6 July 2018
 *
 */
public class ProfileController extends HomeController {
	@FXML protected Text username, wins, losses, net, shortestTime, 
			status, responseText;
	private String[] profileData;
	private static Client client = Main.getClient();
	private String statusStr;
	
	/**
	 * Initialize method.
	 * Calls HomeController initialize method, and then takes
	 * data from the Main class and inputs it to the various text fields
	 * that need to be filled.
	 */
	public void initialize() {
		super.initialize();
		
		profileData = Main.getData();
		username.setText(profileData[1]);
		wins.setText(profileData[3]);
		losses.setText(profileData[4]);
		net.setText(profileData[5]);
		
		//TODO change colours
		this.statusStr = profileData[2];
		
		if (statusStr.equals("0")) {
			status.setText("Offline");
			status.setStyle("-fx-text-fill: red;");
		} else if (statusStr.equals("1")) {
			status.setText("Unavailable");
			status.setStyle("-fx-text-fill: grey;");
		} else if (statusStr.equals("2")) {
			status.setText("Available");
			status.setStyle("-fx-text-fill: green;");
		}
	}
	
	@Override
	void processInput(String action, String[] input) {
		if (action.equals("challengeresponse"))
			try {
				challengeResponseHandler(input);
			} catch (IOException e) {
				e.printStackTrace();
			}
		else
			super.processInput(action, input);
	}
	
	/**
	 * Handler for the "play" button, which sends a challenge to the
	 * owner of the profile.
	 * 
	 * @param event
	 * @throws IOException
	 */
	@FXML
	protected void playButton(ActionEvent event) throws IOException {
		if (statusStr.equals("0") || statusStr.equals("1"))
			responseText.setText("This player is currently not available.");
		else {
			String[] outArr = {client.getUsername(), username.getText()};
			client.sendMessage("challenge", outArr);
		}
	}
	
	/**
	 * Handles the response received from the server after a player
	 * has been challenged.
	 * 
	 * @param input
	 * @throws IOException 
	 */
	private void challengeResponseHandler(String[] input) throws IOException {
		if (input[0].equals("true")) {
			String[] data = {client.getUsername(), input[1], 0 + ""};
			Main.changeScene("Board", 575, 545, data);
			client.sendMessage("newgame", data);
		} else
			responseText.setText(input[2]);
	}
}
