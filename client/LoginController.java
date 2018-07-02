package client;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

/**
 * Controller for the login pane.
 * 
 * @author Ben Drawer
 * @version 2 July 2018
 *
 */
public class LoginController extends Controller {
	@FXML protected TextField username;
	@FXML protected PasswordField password;
	@FXML private Text responseText;
	private static Client client = Main.getClient();
	
	/**
	 * Processes input from server.
	 * 
	 * @param action action to be undertaken
	 * @param input information associated with action
	 */
	void processInput(String action, String[] input) {
		if (action.equals("signin")) {
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
		if (input[0].equals("false")) {
			responseText.setText(Main.twoLines(input[1]));
		} else {
			client.setUsername(username.getText());
			Main.changeScene("Leaderboard", 575, 545);
		}
	}
	
	/**
	 * Listener for sign-in button.
	 * 
	 * @param event
	 * @throws IOException
	 */
	@FXML
	protected void signInButton(ActionEvent event) throws IOException {
		String usernameStr = username.getText(), passwordStr = password.getText();
		
		if (usernameStr.isEmpty() || passwordStr.isEmpty()) {
			responseText.setText("Username and/or password\ncannot be left blank.");
		} else {
			String[] outArr = {usernameStr, passwordStr};
			client.sendMessage("signin", outArr);
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
		Main.changeScene("Signup", 350, 325);	
	}
	
	@FXML
	protected void forgotPassword(ActionEvent event) {
		//TODO forgot password panel
	}
}
