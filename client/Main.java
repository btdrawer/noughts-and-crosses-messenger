package client;

import java.io.IOException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

/**
 * This class provides the GUI for connecting to a server.
 * 
 * @author Ben Drawer
 * @version 2 July 2018
 *
 */
public class Main extends Application {
	private static Stage primaryStage;
	private static Client client;
	private static String[] data;
	
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
	 * 
	 * @return data to be passed to a particular scene
	 */
	static String[] getData() {
		return data;
	}
	
	/**
	 * Set the data String array to information that a particular scene
	 * needs from another.
	 * 
	 * @param newData
	 */
	static void setData(String[] newData) {
		data = newData;
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
	 */
	static void changeScene(String fxml, int x, int y) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				try {
					Parent root = FXMLLoader.load(getClass().getResource("/fxml/" + fxml + ".fxml"));
					
					primaryStage.setScene(new Scene(root, x, y));
					primaryStage.show();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * Changes the scene and sets the data String array to an input String array,
	 * so that the controller of the new scene can access necessary data.
	 * 
	 * @param fxml name of FXML file (not including the extension)
	 * @param x x number of pixels
	 * @param y y number of pixels
	 * @param input data to be passed
	 */
	static void changeScene(String fxml, int x, int y, String[] input) {
		changeScene(fxml, x, y);
		setData(input);
	}
	
	/**
	 * Start method.
	 */
	@Override
	public void start(Stage stage) throws IOException {
		primaryStage = stage;
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
