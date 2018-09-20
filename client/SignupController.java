package client;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import protocol.Constants;

/**
 * Controller for the signup pane.
 * 
 * @author Ben Drawer
 * @version 20 September 2018
 *
 */
public class SignupController extends Controller {
	@FXML protected TextField username;
	@FXML protected PasswordField password;
	@FXML protected ChoiceBox<String> questions;
	@FXML protected TextField answer;
	private String[] response;
	@FXML protected Text responseText;
	private static Client client = Main.getClient();
	private static final String GET_SECURITY_QUESTIONS = Constants.GET_SECURITY_QUESTIONS,
			SIGN_UP = Constants.SIGN_UP,
			FALSE = Constants.FALSE,
			LEADERBOARD_PANEL = PanelConstants.LEADERBOARD_PANEL,
			SIGN_IN_PANEL = PanelConstants.SIGN_IN_PANEL;
	
	@Override
	public void initialize() {
		super.initialize();
		
		try {
			client.sendMessage(GET_SECURITY_QUESTIONS, "");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Receives input, and if the action is "signup", calls the
	 * sign up method to process the input from the server.
	 * 
	 * @param action action to be undertaken
	 * @param input information associated with action
	 */
	@Override
	void processInput(String action, String[] input) {
		switch (action) {
			case SIGN_UP:
				signUp(input);
				break;
			case GET_SECURITY_QUESTIONS:
				populateSecurityQuestions(input);
				break;
		}
	}
	
	/**
	 * If the server returns to the client that the sign up has been successful,
	 * this method changes the scene to the Leaderboard pane.
	 * Otherwise, it prints a message saying sign up wasn't successful.
	 * 
	 * @param input information from server
	 */
	private void signUp(String[] input) {
		if (input[0].equals(FALSE)) {
			responseText.setText(Main.twoLines(response[1]));
		} else {
			client.setUsername(username.getText());
			Main.changeScene(LEADERBOARD_PANEL);
		}
	}
	
	private void populateSecurityQuestions(String[] input) {
		List<String> toList = new LinkedList<>();
		
		for (String s : input) {
			toList.add(s);
		}
		
		ObservableList<String> questionList = FXCollections.observableList(toList);
		questions.setItems(questionList);
	}
	
	/**
	 * Method for the back button, which takes the user back to the login pane.
	 * 
	 * @param event
	 * @throws IOException
	 */
	@FXML
	protected void backButton(ActionEvent event) throws IOException {
		Main.changeScene(SIGN_IN_PANEL);
	}
	
	/**
	 * This method handles the signing up process.
	 * when the sign-up button is selected, it sends the information to the server
	 * and then responds accordingly.
	 * 
	 * @param event
	 * @throws IOException
	 * @throws NoSuchAlgorithmException 
	 */
	@FXML
	protected void signUpButton(ActionEvent event) throws IOException, NoSuchAlgorithmException {
		String usernameStr = username.getText(), 
				passwordStr = client.getProtocol().getMD5(password.getText()),
				questionStr = questions.getValue(), 
				answerStr = client.getProtocol().getMD5(answer.getText());
		
		if (usernameStr.isEmpty() || passwordStr.isEmpty() ||
				questionStr.isEmpty() || answerStr.isEmpty()) {
			responseText.setText("Please ensure all fields are completed.");
		} else {
			String[] outArr = {usernameStr, passwordStr, questionStr, answerStr};
			client.sendMessage(SIGN_UP, outArr);
		}
	}
}
