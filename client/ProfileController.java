package client;

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
		//TODO change colours
		status.setText(profileData[2]);
		wins.setText(profileData[3]);
		losses.setText(profileData[4]);
		net.setText(profileData[5]);
	}
}
