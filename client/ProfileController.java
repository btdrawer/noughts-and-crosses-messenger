package client;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.text.Text;

/**
 * Controller for the Profile scene.
 * 
 * @author Ben Drawer
 * @version 26 June 2018
 *
 */
public class ProfileController extends HomeController {
	@FXML protected Text username, wins, losses, net, shortestTime, status;
	private String[] profileData;
	private Client client;
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
	
	@FXML
	protected void playButton(ActionEvent event) throws IOException {
		if (statusStr.equals("0") || statusStr.equals("1")) {
			//TODO response text
		} else {
			String[] outArr = {client.getUsername()};
			client.sendMessage("sendchallenge", outArr);
		}
	}
}
