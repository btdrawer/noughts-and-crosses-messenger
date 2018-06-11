package controller;

import java.io.IOException;

import client.Client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import view.Main;

/**
 * Controller for the Connect view.
 * 
 * @author Ben Drawer
 * @version 31 May 2018
 *
 */
public class ConnectController {
	private static Client client = Main.getClient();
	private Stage primaryStage;
	@FXML protected TextField ip;
	@FXML protected TextField port;
	private String[] response;
	@FXML protected Text responseText;
	
	/**
	 * Event handler for when the connect button is pressed.
	 * 
	 * @param event
	 * @throws IOException
	 */
	@FXML
	protected void connect(ActionEvent event) throws IOException {
		client = new Client(ip.getText(), Integer.parseInt(port.getText()));
		response = client.connect();
		
		if (response.length > 0 && response[0].equals("true")) {
			Parent root = FXMLLoader.load(getClass().getResource("/fxml/Login.fxml"));
			
			primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			primaryStage.setScene(new Scene(root, 350, 300));
			primaryStage.show();
		} else {
			responseText.setText("Could not find server.");
		}
	}
}
