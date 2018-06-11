package view;

import java.io.IOException;

import client.Client;
import controller.ConnectController;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

/**
 * This class provides the GUI for connecting to a server.
 * 
 * @author Ben Drawer
 * @version 31 May 2018
 *
 */
public class Main extends Application {
	private static Client client;
	
	/**
	 * 
	 * @return current client
	 */
	public static Client getClient() {
		return client;
	}
	
	/**
	 * Start method.
	 */
	@Override
	public void start(Stage stage) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("/fxml/Connect.fxml"));
		
		stage.setTitle("Noughts and Crosses");
		stage.setScene(new Scene(root, 375, 300));
		stage.show();
	}
	
	/**
	 * Main method.
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		launch(args);
	}
}
