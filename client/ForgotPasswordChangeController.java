package client;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.text.Text;

import protocol.Constants;
import protocol.Protocol;

/**
 * Controller for pane to set a new password.
 * 
 * @author Ben Drawer
 * @version 20 September 2018
 *
 */
public class ForgotPasswordChangeController extends Controller {
	@FXML protected PasswordField newPassword, confirmPassword;
	@FXML protected Text responseText;
	private String username;
	private static Client client = Main.getClient();
	private static String[] data = Main.getData();
	private static Protocol protocol = client.getProtocol();
	private static final String FORGOT_PASSWORD_CHANGE = Constants.FORGOT_PASSWORD_CHANGE,
			TRUE = Constants.TRUE,
			SIGN_IN_PANEL = PanelConstants.SIGN_IN_PANEL;
	
	public void initialize() {
		super.initialize();
		
		this.username = data[0];
	}
	
	void processInput(String action, String[] input) {
		switch (action) {
			case FORGOT_PASSWORD_CHANGE:
				forgotPasswordChangeHandler(input);
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
	 * 
	 * @param event
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 */
	@FXML
	protected void submitButtonHandler(ActionEvent event) throws IOException, NoSuchAlgorithmException {
		if (!newPassword.getText().equals(confirmPassword.getText()))
			responseText.setText("Your passwords must match.");
		else {
			String[] outArr = {username, protocol.getMD5(newPassword.getText())};
			client.sendMessage(FORGOT_PASSWORD_CHANGE, outArr);
			responseText.setText("Setting new password...");
		}
	}
	
	/**
	 * Handler for response from server.
	 * 
	 * [0] = true or false
	 * [1] = error message if present
	 * 
	 * @param input
	 */
	private void forgotPasswordChangeHandler(String[] input) {
		if (input[0].equals(TRUE)) {
			Main.changeScene(SIGN_IN_PANEL);
		} else {
			responseText.setText(input[1]);
		}
	}
}
