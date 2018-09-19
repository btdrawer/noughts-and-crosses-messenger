package client;

import java.io.IOException;
import java.util.Optional;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.text.Text;

import protocol.Constants;

/**
 * Controller for the noughts and crosses board.
 * Takes care of in-game actions.
 * 
 * @author Ben Drawer
 * @version 20 September 2018
 *
 */
public class BoardController extends Controller {
	@FXML protected Text username;
	@FXML protected GridPane board;
	private static Client client = Main.getClient();
	private static final String ADD_CHAR = Constants.ADD_CHAR,
			LEFT_GAME = Constants.LEFT_GAME,
			VIEW_PROFILE = Constants.VIEW_PROFILE,
			TRUE = Constants.TRUE,
			TRUE_WON = Constants.TRUE_WON,
			TRUE_DRAW = Constants.TRUE_DRAW,
			TRUE_LOST = Constants.TRUE_LOST,
			PROFILE_PANEL = PanelConstants.PROFILE_PANEL;
	private static String[] data;
	private String opponent;
	private boolean turn;
	private char c;
	
	@Override
	public void initialize() {
		super.initialize();
		
		data = Main.getData();
		this.opponent = data[1];
		username.setText(opponent);
		
		if (Short.parseShort(data[2]) == 0) {
			this.turn = true;
			this.c = 'O';
		} else {
			this.turn = false;
			this.c = 'X';
		}
	}
	
	@Override
	void processInput(String action, String[] input) {
		if (action.equals(ADD_CHAR))
			receiveChar(input);
		else if (action.equals(LEFT_GAME))
			leftGame(input);
		else if (action.equals(VIEW_PROFILE))
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
						client.sendMessage(LEFT_GAME, outArr);
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else if (result.isPresent() && result.get() == ButtonType.NO)
					alert.close();
			}
		});
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
					Main.changeScene(PROFILE_PANEL, input);
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
				Main.changeScene(PROFILE_PANEL, input);
			}
		});
	}
	
	/**
	 * Handles the selection of a board cell.
	 * 
	 * @param event
	 * @throws IOException 
	 */
	@FXML
	protected void selectedCell(MouseEvent event) throws IOException {
		Node source = (Node) event.getSource();
		Text text = (Text) source.lookup("#text");
		
		if (turn && text.getText().isEmpty()) {
			text.setText(c + "");
			String[] outArr = {client.getUsername(), opponent, 
					GridPane.getRowIndex(source) + "", GridPane.getColumnIndex(source) + "",
					c + ""};
			client.sendMessage(ADD_CHAR, outArr);
			turn = false;
		}
	}
	
	/**
	 * Called when an "addchar" message appears from the server.
	 * Displays it on the user's board.
	 * 
	 * @param input [0] = x-coordinate, [1] = y-coordinate, [2] = O or X
	 */
	private void receiveChar(String[] input) {
		if (input[0].equals(TRUE) || input[0].equals(TRUE_DRAW) || 
				input[0].equals(TRUE_LOST)) {
			int x = Integer.parseInt(input[1]);
			int y = Integer.parseInt(input[2]);
			int index = y + (x * 3);
			Node source = (Node) board.getChildren().get(index);
			Text text = (Text) source.lookup("#text");
			text.setText(input[3]);
			
			if (input[0].equals(TRUE))
				turn = true;
		}
		
		if (input[0].equals(TRUE_WON) || input[0].equals(TRUE_DRAW) || 
				input[0].equals(TRUE_LOST)) {
			String alertText = "";
			
			if (input[0].equals(TRUE_WON))
				alertText = "You won!";
			else if (input[0].equals(TRUE_DRAW))
				alertText = "It's a draw!";
			else if (input[0].equals(TRUE_LOST))
				alertText = "Better luck next time!";
			
			final String alertFinal = alertText;
			
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					Alert alert = new Alert(AlertType.INFORMATION, alertFinal, 
							ButtonType.OK);
					Optional<ButtonType> result = alert.showAndWait();
					
					if (result.isPresent()) {
						try {
							client.sendMessage(VIEW_PROFILE, opponent);
						} catch (IOException e) {
							e.printStackTrace();
						}
						
						alert.close();
					}
				}
			});
		}
	}
}
