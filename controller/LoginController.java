package controller;

import java.io.IOException;

import client.Client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import view.Main;

/**
 * Controller for the login pane.
 * 
 * @author Ben Drawer
 * @version 12 June 2018
 *
 */
public class LoginController {
	private static Client client = Main.getClient();
	private Stage primaryStage;
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
		String usernameStr = username.getText(), passwordStr = password.getText();
		
		if (usernameStr.isEmpty() || passwordStr.isEmpty()) {
			responseText.setText("Username and/or password\ncannot be left blank.");
		} else {
			this.response = client.signin(usernameStr, passwordStr);
			
			if (response[0].equals("false")) {
				responseText.setText(Main.twoLines(response[1]));
			} else {
				Parent root = FXMLLoader.load(getClass().getResource("/fxml/Leaderboard.fxml"));
				
				primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
				primaryStage.setScene(new Scene(root, 400, 550));
				primaryStage.show();
			}
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
		Parent root = FXMLLoader.load(getClass().getResource("/fxml/Signup.fxml"));
		
		primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		primaryStage.setScene(new Scene(root, 350, 300));
		primaryStage.show();
	}
	
	@FXML
	protected void forgotPassword(ActionEvent event) {
		//TODO forgot password panel
	}
}
