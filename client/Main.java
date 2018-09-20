package client;

import java.io.IOException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import protocol.Constants;
import javafx.fxml.FXMLLoader;

/**
 * This class provides the GUI for connecting to a server.
 * 
 * @author Ben Drawer
 * @version 20 September 2018
 *
 */
public class Main extends Application {
	private static Stage primaryStage;
	private static Client client;
	private static String[] data;
	private static final String LEFT_SERVER = Constants.LEFT_SERVER,
			SIGN_IN_PANEL = PanelConstants.SIGN_IN_PANEL,
			SIGN_UP_PANEL = PanelConstants.SIGN_UP_PANEL,
			FORGOT_PASSWORD_PANEL = PanelConstants.FORGOT_PASSWORD_PANEL,
			FORGOT_PASSWORD_QUESTION_PANEL = PanelConstants.FORGOT_PASSWORD_QUESTION_PANEL,
			FORGOT_PASSWORD_CHANGE_PANEL = PanelConstants.FORGOT_PASSWORD_CHANGE_PANEL,
			BOARD_PANEL = PanelConstants.BOARD_PANEL;
	private static final int[] SIGN_IN_DIMENSIONS = PanelConstants.SIGN_IN_DIMENSIONS,
			SIGN_UP_DIMENSIONS = PanelConstants.SIGN_UP_DIMENSIONS,
			HOME_DIMENSIONS = PanelConstants.HOME_DIMENSIONS,
			BOARD_DIMENSIONS = PanelConstants.BOARD_DIMENSIONS,
			FORGOT_PASSWORD_DIMENSIONS = PanelConstants.FORGOT_PASSWORD_DIMENSIONS;
			
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
	static void changeScene(String fxml) {
		int[] dimensions = new int[2];
		int x, y;
		
		switch (fxml) {
			case SIGN_IN_PANEL:
				dimensions = SIGN_IN_DIMENSIONS;
				break;
			case SIGN_UP_PANEL:
				dimensions = SIGN_UP_DIMENSIONS;
				break;
			case FORGOT_PASSWORD_PANEL:
				dimensions = FORGOT_PASSWORD_DIMENSIONS;
				break;
			case FORGOT_PASSWORD_QUESTION_PANEL:
				dimensions = FORGOT_PASSWORD_DIMENSIONS;
				break;
			case FORGOT_PASSWORD_CHANGE_PANEL:
				dimensions = FORGOT_PASSWORD_DIMENSIONS;
				break;
			case BOARD_PANEL:
				dimensions = BOARD_DIMENSIONS;
				break;
			default:
				dimensions = HOME_DIMENSIONS;
		}
		
		x = dimensions[0];
		y = dimensions[1];
		
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				try {
					Parent root = FXMLLoader.load(getClass().getResource(fxml + ".fxml"));
					
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
	static void changeScene(String fxml, String[] input) {
		changeScene(fxml);
		setData(input);
	}
	
	/**
	 * Start method.
	 */
	@Override
	public void start(Stage stage) throws IOException {
		primaryStage = stage;
		Parent root = FXMLLoader.load(getClass().getResource("Connect.fxml"));
		
		stage.setTitle("Noughts and Crosses");
		stage.setScene(new Scene(root, 315, 350));
		stage.show();
	}
	
	/**
	 * Stop method.
	 */
	@Override
	public void stop() throws Exception {
		client.sendMessage(LEFT_SERVER, client.getUsername());
		super.stop();
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
