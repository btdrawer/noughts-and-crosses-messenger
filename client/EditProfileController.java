package client;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import protocol.Constants;

/**
 * Controller for the Edit Profile scene.
 * 
 * @author Ben Drawer
 * @version 11 October 2018
 *
 */
public class EditProfileController extends HomeController {
	@FXML protected TextField usernameField;
	@FXML protected PasswordField password, newPassword, confirmPassword;
	@FXML protected Text editResponseText;
	private static Client client = Main.getClient();
	private static final String EDIT_PROFILE = Constants.EDIT_PROFILE,
			LEADERBOARD_PANEL = PanelConstants.LEADERBOARD_PANEL;
	private String currentUsername;
	
	@Override
	public void initialize() {
		super.initialize();
		
		currentUsername = client.getUsername();
		usernameField.setText(currentUsername);
	}
	
	@Override
	void processInput(String action, boolean result, String[] input) {
		switch (action) {
			case EDIT_PROFILE:
				editProfile(result, input);
				break;
			default:
				super.processInput(action, result, input);
		}
	}
	
	/**
	 * Returns the user to the leaderboard.
	 * 
	 * @param event
	 */
	@FXML
	protected void backButton(ActionEvent event) {
		//TODO if user was on a profile, make it return to there
		Main.changeScene(LEADERBOARD_PANEL);
	}
	
	/**
	 * Handler for the "save changes" button.
	 * 
	 * @param event
	 * @throws IOException
	 * @throws NoSuchAlgorithmException 
	 */
	@FXML
	protected void saveChangesButton(ActionEvent event) throws IOException, NoSuchAlgorithmException {
		String passwordStr = client.getProtocol().getMD5(password.getText());
		String[] changes = {currentUsername, ".", passwordStr, "."};
		boolean send = false;
		
		if (passwordStr.isEmpty())
			editResponseText.setText("You must enter your password to make any changes.");
		else {
			String newUsername = usernameField.getText();
			
			if (!(newUsername.isEmpty() || newUsername.equals(currentUsername))) {
				changes[1] = newUsername;
				send = true;
			}
			
			String newPasswordStr = newPassword.getText();
			String confirmPasswordStr = confirmPassword.getText();
			
			if (!newPasswordStr.isEmpty()) {
				if (!newPasswordStr.equals(confirmPasswordStr))
					editResponseText.setText("Your passwords do not match.");
				else {
					changes[3] = client.getProtocol().getMD5(newPasswordStr);
					send = true;
				}
			}
			
			//Conditional on "send" so that, if the user does not change anything but
			//nonetheless presses "Save changes", the server isn't needlessly notified.
			if (send)
				client.sendMessage(EDIT_PROFILE, changes);
		}
	}
	
	/**
	 * Handles response from server regarding attempted changes.
	 * 
	 * @param input [0] = message; [1] = new username (if changed)
	 */
	private void editProfile(boolean result, String[] input) {
		if (result) {
			if (!input[1].equals("."))
				client.setUsername(input[1]);
			
			Main.changeScene(LEADERBOARD_PANEL);
		} else
			editResponseText.setText(Main.twoLines(input[0]));
	}
}
