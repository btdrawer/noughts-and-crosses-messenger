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
 * @version 20 September 2018
 *
 */
public class ForgotPasswordController extends Controller {
	@FXML protected TextField username;
	@FXML protected Text responseText;
	private static Client client = Main.getClient();
	private static final String FORGOT_PASSWORD_REQUEST = Constants.FORGOT_PASSWORD_REQUEST,
			TRUE = Constants.TRUE,
			SIGN_IN_PANEL = PanelConstants.SIGN_IN_PANEL,
			FORGOT_PASSWORD_QUESTION_PANEL = PanelConstants.FORGOT_PASSWORD_QUESTION_PANEL;
	
	void processInput(String action, String[] input) {
		switch (action) {
			case FORGOT_PASSWORD_REQUEST:
				forgotPasswordRequestHandler(input);
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
	 * [0] = true or false
	 * [1] = username
	 * [2] = error message if false
	 * 
	 * @param input
	 */
	private void forgotPasswordRequestHandler(String[] input) {
		if (input[0].equals(TRUE)) {
			String[] data = {input[1], input[2]};
			Main.setData(data);
			Main.changeScene(FORGOT_PASSWORD_QUESTION_PANEL);
		} else {
			responseText.setText(input[2]);
		}
	}
}
