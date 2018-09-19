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
 * @version 20 September 2018
 *
 */
public class EditProfileController extends HomeController {
	@FXML protected TextField usernameField;
	@FXML protected PasswordField password, newPassword, confirmPassword;
	@FXML protected Text editResponseText;
	private static Client client = Main.getClient();
	private static final String EDIT_PROFILE = Constants.EDIT_PROFILE,
			TRUE = Constants.TRUE,
			LEADERBOARD_PANEL = PanelConstants.LEADERBOARD_PANEL;
	private String currentUsername;
	
	@Override
	public void initialize() {
		super.initialize();
		
		currentUsername = client.getUsername();
		usernameField.setText(currentUsername);
	}
	
	@Override
	void processInput(String action, String[] input) {
		if (action.equals(EDIT_PROFILE))
			editProfile(input);
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
	 * @param input [0] = success or failure; [1] = message; [2] = new username (if changed)
	 */
	private void editProfile(String[] input) {
		if (input[0].equals(TRUE)) {
			if (!input[2].equals("."))
				client.setUsername(input[2]);
			
			Main.changeScene(LEADERBOARD_PANEL);
		} else
			editResponseText.setText(Main.twoLines(input[1]));
	}
}
