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
 * Controller for the login pane.
 * 
 * @author Ben Drawer
 * @version 20 September 2018
 *
 */
public class LoginController extends Controller {
	@FXML protected TextField username;
	@FXML protected PasswordField password;
	@FXML private Text responseText;
	private static Client client = Main.getClient();
	private static final String SIGN_IN = Constants.SIGN_IN,
			FALSE = Constants.FALSE,
			LEADERBOARD_PANEL = PanelConstants.LEADERBOARD_PANEL,
			SIGN_UP_PANEL = PanelConstants.SIGN_UP_PANEL;
	
	/**
	 * Processes input from server.
	 * 
	 * @param action action to be undertaken
	 * @param input information associated with action
	 */
	void processInput(String action, String[] input) {
		if (action.equals(SIGN_IN)) {
			try {
				signIn(input);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * This method takes input from the server stating whether the provided
	 * login information was correct or not, and depending on this, will either
	 * change the scene to the Leaderboard pane or print a message stating
	 * that the provided login details were incorrect.
	 * 
	 * @param input input from server saying whether the login details were correct
	 * @throws IOException
	 */
	private void signIn(String[] input) throws IOException {
		if (input[0].equals(FALSE)) {
			responseText.setText(Main.twoLines(input[1]));
		} else {
			client.setUsername(username.getText());
			Main.changeScene(LEADERBOARD_PANEL);
		}
	}
	
	/**
	 * Listener for sign-in button.
	 * 
	 * @param event
	 * @throws IOException
	 * @throws NoSuchAlgorithmException 
	 */
	@FXML
	protected void signInButton(ActionEvent event) throws IOException, NoSuchAlgorithmException {
		String usernameStr = username.getText(), 
				passwordStr = client.getProtocol().getMD5(password.getText());
		
		if (usernameStr.isEmpty() || passwordStr.isEmpty()) {
			responseText.setText("Username and/or password\ncannot be left blank.");
		} else {
			String[] outArr = {usernameStr, passwordStr};
			client.sendMessage(SIGN_IN, outArr);
		}
	}
	
	/**
	 * Listener for sign-up button.
	 * 
	 * @param event
	 * @throws IOException 
	 */
	@FXML
	protected void signUpButton(ActionEvent event) throws IOException {
		Main.changeScene(SIGN_UP_PANEL);	
	}
	
	@FXML
	protected void forgotPassword(ActionEvent event) {
		//TODO forgot password panel
	}
}
