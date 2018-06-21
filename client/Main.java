package client;

import java.io.IOException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.InputEvent;
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
	private static Stage primaryStage;
	private static Client client;
	
	/**
	 * 
	 * @return Client class that manages Socket input and output streams
	 */
	static Client getClient() {
		return client;
	}
	
	/**
	 * Sets the Client class.
	 * 
	 * @param newClient
	 */
	static void setClient(Client newClient) {
		client = newClient;
	}
	
	/**
	 * Splits an input String into two lines separated by a newline character.
	 * 
	 * @param input input String
	 * @return String with newline character inserted
	 */
	static String twoLines(String input) {
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
	 * Changes the scene.
	 * 
	 * @param fxml name of FXML file (not including the extension)
	 * @param x x number of pixels
	 * @param y y number of pixels
	 * @param event the current ActionEvent
	 */
	static void changeScene(String fxml, int x, int y, Event event) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				try {
					Parent root = FXMLLoader.load(getClass().getResource("/fxml/" + fxml + ".fxml"));
					
					primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
					primaryStage.setScene(new Scene(root, x, y));
					primaryStage.show();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
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
