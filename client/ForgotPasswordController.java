package client;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import protocol.Constants;

/**
 * Controller for forgot password pane.
 * 
 * @author Ben Drawer
 * @version 11 October 2018
 *
 */
public class ForgotPasswordController extends Controller {
	@FXML protected TextField username;
	@FXML protected Text responseText;
	private static Client client = Main.getClient();
	private static final String FORGOT_PASSWORD_REQUEST = Constants.FORGOT_PASSWORD_REQUEST,
			SIGN_IN_PANEL = PanelConstants.SIGN_IN_PANEL,
			FORGOT_PASSWORD_QUESTION_PANEL = PanelConstants.FORGOT_PASSWORD_QUESTION_PANEL;
	
	void processInput(String action, boolean result, String[] input) {
		switch (action) {
			case FORGOT_PASSWORD_REQUEST:
				forgotPasswordRequestHandler(result, input);
				break;
		}
	}
	
	/**
	 * Handler for back button.
	 * 
	 * @param event
	 */
	@FXML
	protected void backButtonHandler(ActionEvent event) {
		Main.changeScene(SIGN_IN_PANEL);
	}
	
	/**
	 * Handler for submit button.
	 * Sends username to server.
	 * 
	 * @param event
	 * @throws IOException
	 */
	@FXML
	protected void submitButtonHandler(ActionEvent event) throws IOException {
		client.sendMessage(FORGOT_PASSWORD_REQUEST, username.getText());
	}
	
	/**
	 * Handler for when username submitted.
	 * 
	 * [0] = username
	 * [1] = error message if false
	 * 
	 * @param input
	 */
	private void forgotPasswordRequestHandler(boolean result, String[] input) {
		if (result) {
			String[] data = {input[0], input[1]};
			Main.setData(data);
			Main.changeScene(FORGOT_PASSWORD_QUESTION_PANEL);
		} else
			responseText.setText(input[1]);
	}
}
