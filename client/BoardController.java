package client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.text.Text;

/**
 * Controller for the noughts and crosses board.
 * Takes care of in-game actions.
 * 
 * @author Ben Drawer
 * @version 5 July 2018
 *
 */
public class BoardController extends Controller {
	@FXML protected Text username;
	@FXML protected ArrayList<ArrayList<Text>> board;
	private static Client client = Main.getClient();
	private static String[] data;
	
	@Override
	public void initialize() {
		super.initialize();
		
		data = Main.getData();
		username.setText(data[1]);
	}
	
	@Override
	void processInput(String action, String[] input) {
		if (action.equals("addchar"))
			receiveChar(input);
		else if (action.equals("leavegame"))
			leftGame(input);
		else if (action.equals("viewprofile"))
			viewProfile(input);
	}
	
	/**
	 * Handler for the "leave game" button.
	 * Creates an alert asking the user to confirm they want to finish the game,
	 * and if they click yes, sends a message to the server and returns the user
	 * to the other player's profile.
	 * 
	 * @param event
	 */
	@FXML
	protected void leaveGameButton(ActionEvent event) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				Alert alert = new Alert(AlertType.CONFIRMATION, "Are you sure you want to leave?", 
						ButtonType.YES, ButtonType.NO);
				Optional<ButtonType> result = alert.showAndWait();
				
				if (result.isPresent() && result.get() == ButtonType.YES) {
					try {
						String[] outArr = {client.getUsername(), username.getText()};
						client.sendMessage("leavegame", outArr);
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else if (result.isPresent() && result.get() == ButtonType.NO)
					alert.close();
			}
		});
	}
	
	/**
	 * When the other player adds a new character, this method is called to add it
	 * to the board.
	 * 
	 * @param input
	 */
	private void receiveChar(String[] input) {
		board.get(Integer.parseInt(input[0])).get(Integer.parseInt(input[1])).setText(input[2]);
	}
	
	/**
	 * Called if the other player leaves the game early.
	 * The input to this method will be the same as is provided by the viewProfile method.
	 * 
	 * @param input [1] = username
	 */
	private void leftGame(String[] input) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				Alert alert = new Alert(AlertType.INFORMATION, input[1] + " has left the game! :(", 
						ButtonType.OK);
				Optional<ButtonType> result = alert.showAndWait();
				
				if (result.isPresent()) {
					Main.changeScene("Profile", 575, 545, input);
					alert.close();
				}
			}
		});
	}
	
	/**
	 * This method is called when the present user leaves the game.
	 * The leftGame method requests the profile of the user they were playing against,
	 * and this method then displays that.
	 * 
	 * @param input
	 */
	private void viewProfile(String[] input) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				Main.changeScene("Profile", 575, 545, input);
			}
		});
	}
}
