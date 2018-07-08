package client;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

/**
 * Controller for the Edit Profile scene.
 * 
 * @author Ben Drawer
 * @version 6 July 2018
 *
 */
public class EditProfileController extends HomeController {
	@FXML protected TextField usernameField;
	@FXML protected PasswordField password, newPassword, confirmPassword;
	@FXML protected Text responseText;
	private static Client client = Main.getClient();
	private String currentUsername;
	
	@Override
	public void initialize() {
		super.initialize();
		
		currentUsername = client.getUsername();
		usernameField.setText(currentUsername);
	}
	
	@Override
	void processInput(String action, String[] input) {
		if (action.equals("changes"))
			changes(input);
		else
			super.processInput(action, input);
	}
	
	/**
	 * Returns the user to the leaderboard.
	 * 
	 * @param event
	 */
	@FXML
	protected void backButton(ActionEvent event) {
		//TODO if user was on a profile, make it return to there
		Main.changeScene("Leaderboard", 575, 545);
	}
	
	/**
	 * Handler for the "save changes" button.
	 * 
	 * @param event
	 * @throws IOException
	 */
	@FXML
	protected void saveChangesButton(ActionEvent event) throws IOException {
		String passwordStr = password.getText();
		String[] changes = {"username", currentUsername, ".", "password", passwordStr, "."};
		boolean send = false;
		
		if (passwordStr.isEmpty())
			responseText.setText("You must enter your password to make any changes.");
		else {
			String newUsername = usernameField.getText();
			
			if (!(newUsername.isEmpty() || newUsername.equals(currentUsername))) {
				changes[2] = newUsername;
				send = true;
			}
			
			String newPasswordStr = newPassword.getText();
			String confirmPasswordStr = confirmPassword.getText();
			
			if (!newPasswordStr.isEmpty()) {
				if (!newPasswordStr.equals(confirmPasswordStr))
					responseText.setText("Your passwords do not match.");
				else {
					changes[5] = newPasswordStr;
					send = true;
				}
			}
			
			//Conditional on "send" so that, if the user does not change anything but
			//nonetheless presses "Save changes", the server isn't needlessly notified.
			if (send)
				client.sendMessage("editprofile", changes);
		}
	}
	
	/**
	 * Handles response from server regarding attempted changes.
	 * 
	 * @param input [0] = success or failure; [1] = message; [2] = new username (if changed)
	 */
	private void changes(String[] input) {
		if (input[0].equals("true")) {
			if (!input[2].equals("."))
				client.setUsername(input[2]);
			
			Main.changeScene("Leaderboard", 575, 545);
		} else
			responseText.setText(Main.twoLines(input[1]));
	}
}
