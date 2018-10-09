package client;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Calendar;

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
 * @version 8 October 2018
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
		username.setText(profileData[1]);
		wins.setText(wins.getText() + " " + profileData[3]);
		losses.setText(losses.getText() + " " + profileData[4]);
		net.setText(net.getText() + " " + profileData[5]);
		
		//TODO change colours
		this.statusStr = profileData[2];
		status.setText(statusStr);
		
		if (statusStr.equals("offline"))
			status.setStyle("-fx-text-fill: red;");
		else if (statusStr.equals("busy"))
			status.setStyle("-fx-text-fill: grey;");
		else if (statusStr.equals("online"))
			status.setStyle("-fx-text-fill: green;");
		
		try {
			String[] getMessages = {client.getUsername(), username.getText(), 
					25 + ""};
			
			client.sendMessage(GET_MESSAGES, getMessages);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	void processInput(String action, String[] input) {
		switch (action) {
			case GET_MESSAGES:
				getMessages(input);
				break;
			case GET_NEW_MESSAGE:
				addNewMessage(input);
				break;
			default:
				super.processInput(action, input);
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
		String[] outArr = {new Timestamp(Calendar.MILLISECOND).toString(), 
				client.getUsername(), username.getText(), messageField.getText()};
		client.sendMessage(SEND_MESSAGE, outArr);
	}
	
	/**
	 * Populates the messageList ListView with messages.
	 * 
	 * @param input
	 */
	private void getMessages(String[] input) {
		for (int i = 0; i < input.length; i += 4) {
			messages.add(input[1] + " (" + input[0] + ") " + input[3]);
		}
		
		messageList.setItems(messages);
	}
	
	/**
	 * Adds a message to the messageList ListView.
	 * 
	 * @param input
	 */
	private void addNewMessage(String[] input) {
		messages.add(input[1] + " (" + input[0] + ") " + input[3]);
		
		messageList.setItems(messages);
	}
}
