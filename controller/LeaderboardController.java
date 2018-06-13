package controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import client.Client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import view.Main;

/**
 * Controller for the leaderboard pane.
 * 
 * @author Ben Drawer
 * @version 13 June 2018
 *
 */
public class LeaderboardController {
	private static Client client = Main.getClient();
	private Stage primaryStage;
	@FXML protected Button settings;
	@FXML protected Button signout;
	private String[] response;
	
	@FXML
	protected void settings(ActionEvent event) {
		
	}
	
	@FXML
	protected void signOut(ActionEvent event) throws IOException {
		response = client.signOut(client.getUsername());
		
		if (response[0].equals("true")) {
			client.setUsername("");
			
			Parent root = FXMLLoader.load(getClass().getResource("/fxml/Login.fxml"));
			
			primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			primaryStage.setScene(new Scene(root, 325, 350));
			primaryStage.show();
		}
	}
	
	@FXML
	protected void playRandom(ActionEvent event) {
		
	}
}
