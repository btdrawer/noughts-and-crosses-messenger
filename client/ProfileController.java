package client;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.text.Text;
import protocol.Constants;

/**
 * Controller for the Profile scene.
 * 
 * @author Ben Drawer
 * @version 20 September 2018
 *
 */
public class ProfileController extends HomeController {
	@FXML protected Text username, wins, losses, net, shortestTime, 
			status, responseText;
	private String[] profileData;
	private static Client client = Main.getClient();
	private static final String SEND_CHALLENGE = Constants.SEND_CHALLENGE;
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
		status.setText(statusStr);
		
		if (statusStr.equals("offline"))
			status.setStyle("-fx-text-fill: red;");
		else if (statusStr.equals("busy"))
			status.setStyle("-fx-text-fill: grey;");
		else if (statusStr.equals("online"))
			status.setStyle("-fx-text-fill: green;");
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
			client.sendMessage(SEND_CHALLENGE, outArr);
		}
	}
}
