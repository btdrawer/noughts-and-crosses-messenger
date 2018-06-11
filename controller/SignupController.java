package controller;

import client.Client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuButton;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import view.Main;

/**
 * Controller for the signup pane.
 * 
 * @author Ben Drawer
 * @version 11 June 2018
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
	
	@FXML
	protected void signUpButton(ActionEvent event) {
		//TODO sign-up button method
	}
}
