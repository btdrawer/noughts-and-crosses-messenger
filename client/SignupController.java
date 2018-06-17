package client;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuButton;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

/**
 * Controller for the signup pane.
 * 
 * @author Ben Drawer
 * @version 17 June 2018
 *
 */
public class SignupController extends Controller {
	@FXML protected TextField username;
	@FXML protected PasswordField password;
	@FXML protected MenuButton questions;
	@FXML protected TextField answer;
	private String[] response;
	@FXML protected Text responseText;
	private ActionEvent currentEvent;
	
	/**
	 * Receives input, and if the action is "signup", calls the
	 * sign up method to process the input from the server.
	 * 
	 * @param action action to be undertaken
	 * @param input information associated with action
	 */
	@Override
	void processInput(String action, String[] input) {
		if (action.equals("signup"))
			signUp(input);
	}
	
	/**
	 * If the server returns to the client that the sign up has been successful,
	 * this method changes the scene to the Leaderboard pane.
	 * Otherwise, it prints a message saying sign up wasn't successful.
	 * 
	 * @param input information from server
	 */
	private void signUp(String[] input) {
		if (input[0].equals("false")) {
			responseText.setText(Main.twoLines(response[1]));
		} else {
			super.setUsername(input[1]);
			Main.changeScene("Leaderboard", 575, 425, currentEvent);
		}
	}
	
	/**
	 * Method for the back button, which takes the user back to the login pane.
	 * 
	 * @param event
	 * @throws IOException
	 */
	@FXML
	protected void backButton(ActionEvent event) throws IOException {
		this.currentEvent = event;
		Main.changeScene("Login", 325, 350, currentEvent);
	}
	
	/**
	 * This method handles the signing up process.
	 * when the sign-up button is selected, it sends the information to the server
	 * and then responds accordingly.
	 * 
	 * @param event
	 * @throws IOException
	 */
	@FXML
	protected void signUpButton(ActionEvent event) throws IOException {
		String usernameStr = username.getText(), passwordStr = password.getText(),
				answerStr = answer.getText();
		
		if (usernameStr.isEmpty() || passwordStr.isEmpty() ||
				answerStr.isEmpty()) {
			responseText.setText("Please ensure all fields are completed.");
		} else {
			//TODO get number from menu item
			String[] outArr = {usernameStr, passwordStr, 0 + "", answerStr};
			super.sendMessage("signup", outArr);
		}
	}
}
