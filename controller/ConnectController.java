package controller;

import java.io.IOException;
import java.net.ConnectException;

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
 * @version 12 June 2018
 *
 */
public class ConnectController {
	private Stage primaryStage;
	@FXML protected TextField ip;
	@FXML protected TextField port;
	@FXML protected Text responseText;
	
	/**
	 * Event handler for when the connect button is pressed.
	 * 
	 * @param event
	 * @throws IOException
	 */
	@FXML
	protected void connect(ActionEvent event) throws IOException {
		String ipStr = ip.getText(), portStr = port.getText();
		
		if (ipStr.isEmpty() || portStr.isEmpty()) {
			responseText.setText("IP address and/or port number\ncannot be left blank.");
		} else {
			try {
				Main.setClient(new Client(ipStr, Integer.parseInt(portStr)));
				
				Parent root = FXMLLoader.load(getClass().getResource("/fxml/Login.fxml"));
				
				primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
				primaryStage.setScene(new Scene(root, 315, 350));
				primaryStage.show();
			} catch (ConnectException e) {
				responseText.setText("Could not find server.");
			}
		}
	}
}
