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
 * @version 12 June 2018
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
	 * Setter method for client.
	 * 
	 * @param newClient new client information
	 */
	public static void setClient(Client newClient) {
		client = newClient;
	}
	
	/**
	 * Splits an input String into two lines separated by a newline character.
	 * 
	 * @param input input String
	 * @return String with newline character inserted
	 */
	public static String twoLines(String input) {
		String[] text = input.split(" ");
		StringBuilder s = new StringBuilder();
		
		for (int i = 0; i < text.length / 2; i++) {
			s.append(text[i] + " ");
		}
		
		s.append("\n");
		
		for (int i = text.length / 2; i < text.length; i++) {
			s.append(text[i] + " ");
		}
		
		return s.toString();
	}
	
	/**
	 * Start method.
	 */
	@Override
	public void start(Stage stage) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("/fxml/Connect.fxml"));
		
		stage.setTitle("Noughts and Crosses");
		stage.setScene(new Scene(root, 325, 350));
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
