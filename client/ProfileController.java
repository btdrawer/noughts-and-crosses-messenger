package client;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Calendar;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import protocol.Constants;

/**
 * Controller for the Profile scene.
 * 
 * @author Ben Drawer
 * @version 12 October 2018
 *
 */
public class ProfileController extends HomeController {
	@FXML protected Text username, wins, losses, net, shortestTime, 
			status, responseText;
	@FXML protected ListView<String> messageList;
	@FXML protected TextField messageField;
	private ObservableList<String> messages = FXCollections.<String>observableArrayList();
	private String[] profileData;
	private static Client client = Main.getClient();
	private static final String SEND_CHALLENGE = Constants.SEND_CHALLENGE,
			GET_MESSAGES = Constants.GET_MESSAGES,
			GET_NEW_MESSAGE = Constants.GET_NEW_MESSAGE,
			SEND_MESSAGE = Constants.SEND_MESSAGE;
	private String statusStr;
	
	/**
	 * Initialize method.
	 * Calls HomeController initialize method, and then takes
	 * data from the Main class and inputs it to the various text fields
	 * that need to be filled.
	 */
	public void initialize() {
		super.initialize();
		
		profileData = Main.getData();
		username.setText(profileData[0]);
		wins.setText(wins.getText() + " " + profileData[2]);
		losses.setText(losses.getText() + " " + profileData[3]);
		net.setText(net.getText() + " " + profileData[4]);
		
		//TODO change colours
		this.statusStr = profileData[1];
		status.setText(statusStr);
		
		if (statusStr.equals("offline"))
			status.setStyle("-fx-text-fill: red;");
		else if (statusStr.equals("busy"))
			status.setStyle("-fx-text-fill: grey;");
		else if (statusStr.equals("online"))
			status.setStyle("-fx-text-fill: green;");
		
		try {
			String[] getMessages = {client.getUsername(), username.getText(), 
					0 + ""};
			
			client.sendMessage(GET_MESSAGES, getMessages);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	void processInput(String action, boolean result, String[] input) {
		switch (action) {
			case GET_MESSAGES:
				getMessages(input);
				break;
			case GET_NEW_MESSAGE:
				addNewMessage(input);
				break;
			case SEND_MESSAGE:
				sendMessageCallback(result, input);
				break;
			default:
				super.processInput(action, result, input);
		}
	}
	
	/**
	 * Handler for the "play" button, which sends a challenge to the
	 * owner of the profile.
	 * 
	 * @param event
	 * @throws IOException
	 */
	@FXML
	protected void playButton(ActionEvent event) throws IOException {
		String[] outArr = {client.getUsername(), username.getText()};
		client.sendMessage(SEND_CHALLENGE, outArr);
	}
	
	/**
	 * Sends a message.
	 * 
	 * @param event
	 * @throws IOException
	 */
	@FXML
	protected void sendButton(ActionEvent event) throws IOException {
		String[] outArr = {new Timestamp(Calendar.getInstance().getTime().getTime()).toString(), 
				client.getUsername(), username.getText(), messageField.getText()};
		client.sendMessage(SEND_MESSAGE, outArr);
	}
	
	/**
	 * Populates the messageList ListView with messages.
	 * 
	 * @param input
	 */
	private void getMessages(String[] input) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				for (int i = 0; i < input.length; i += 3) {
					messages.add(input[i+1] + " (" + input[i] + "): " + input[i+2]);
				}
				
				messageList.setItems(messages);
			}
		});
	}
	
	/**
	 * Adds a message to the messageList ListView.
	 * 
	 * @param input
	 */
	private void addNewMessage(String[] input) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				messages.add(input[1] + " (" + input[0] + ") " + input[3]);
				
				messageList.setItems(messages);
			}
		});
	}
	
	/**
	 * Handles message from server after message has been sent.
	 * 
	 * @param input
	 */
	private void sendMessageCallback(boolean result, String[] input) {
		if (result) {
			addNewMessage(input);
			messageField.clear();
		} else
			responseText.setText("Message failed to send! :(");
	}
}
