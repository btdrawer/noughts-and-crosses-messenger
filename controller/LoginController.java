package controller;

import java.io.IOException;

import client.Client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import view.Main;

/**
 * Controller for the login pane.
 * 
 * @author Ben Drawer
 * @version 30 May 2018
 *
 */
public class LoginController {
	private static Client client = Main.getClient();
	@FXML protected TextField username;
	@FXML protected PasswordField password;
	private String[] response;
	@FXML private Text responseText;
	
	/**
	 * Listener for sign-in button.
	 * 
	 * @param event
	 * @throws IOException
	 */
	@FXML
	protected void signInButton(ActionEvent event) throws IOException {
		response = client.signin(username.toString(), password.toString());
		
		if (response[0].equals("false")) {
			responseText.setText(response[1]);
		} else {
			//TODO move to next panel
		}
	}
	
	/**
	 * Listener for sign-up button.
	 * 
	 * @param event
	 */
	@FXML
	protected void signUpButton(ActionEvent event) {
		//TODO sign up panel
	}
	
	@FXML
	protected void forgotPassword(ActionEvent event) {
		//TODO forgot password panel
	}
}
