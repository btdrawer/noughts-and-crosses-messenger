package view;

import java.io.IOException;

import client.Client;
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
	private static Stage primaryStage;
	
	/**
	 * 
	 * @return current client
	 */
	public static Client getClient() {
		return client;
	}
	
	/**
	 * 
	 * @return primary stage
	 */
	public static Stage getPrimaryStage() {
		return primaryStage;
	}
	
	/**
	 * Start method.
	 */
	@Override
	public void start(Stage stage) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("/fxml/Connect.fxml"));
		primaryStage = stage;
		
		stage.setTitle("Noughts and Crosses");
		stage.setScene(new Scene(root, 375, 300));
		stage.show();
	}
	
	/**
	 * Main method.
	 * @param args
	 */
	public static void main(String[] args) {
		launch(args);
	}
}
