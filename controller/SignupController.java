package controller;

import java.io.IOException;

import client.Client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuButton;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import view.Main;

/**
 * Controller for the signup pane.
 * 
 * @author Ben Drawer
 * @version 12 June 2018
 *
 */
public class SignupController {
	private static Client client = Main.getClient();
	private Stage primaryStage;
	@FXML protected TextField username;
	@FXML protected PasswordField password;
	@FXML protected MenuButton questions;
	@FXML protected TextField answer;
	private String[] response;
	@FXML protected Text responseText;
	
	/**
	 * Method for the back button, which takes the user back to the login pane.
	 * 
	 * @param event
	 * @throws IOException
	 */
	@FXML
	protected void backButton(ActionEvent event) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("/fxml/Login.fxml"));
		
		primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		primaryStage.setScene(new Scene(root, 325, 350));
		primaryStage.show();
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
			response = client.signup(usernameStr, passwordStr, 
					0, answerStr);
			
			if (response[0].equals("false")) {
				responseText.setText(response[1]);
			} else {
				Parent root = FXMLLoader.load(getClass().getResource("/fxml/Leaderboard.fxml"));
				
				primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
				primaryStage.setScene(new Scene(root, 400, 550));
				primaryStage.show();
			}
		}
	}
}
