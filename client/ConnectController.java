package client;

import java.io.IOException;
import java.net.ConnectException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

/**
 * Controller for the Connect view.
 * 
 * @author Ben Drawer
 * @version 17 June 2018
 *
 */
public class ConnectController extends Controller {
	@FXML protected TextField ip;
	@FXML protected TextField port;
	@FXML protected Text responseText;
	private ActionEvent currentEvent;
	
	@Override
	public void initialize() {
		//do nothing
	}
	
	/**
	 * Processes input from server.
	 * 
	 * @param action action to be undertaken
	 * @param input information associated with action
	 * @throws IOException
	 */
	@Override
	void processInput(String action, String[] input) {
		if (action.equals("connect")) {
			connect(input);
		}
	}
	
	/**
	 * Tests the connection with the server.
	 * 
	 * @param input
	 * @throws IOException
	 */
	private void connect(String[] input) {
		if (input.length > 0) {
			Main.changeScene("Login", 315, 350, currentEvent);
		} else {
			responseText.setText("Unable to join server.\nPlease try again later.");
		}
	}
	
	/**
	 * Event handler for when the connect button is pressed.
	 * 
	 * @param event
	 * @throws IOException
	 */
	@FXML
	protected void connectButton(ActionEvent event) throws IOException {
		String ipStr = ip.getText(), portStr = port.getText();
		this.currentEvent = event;
		
		if (ipStr.isEmpty() || portStr.isEmpty()) {
			responseText.setText("IP address and/or port number\ncannot be left blank.");
		} else {
			try {
				super.setHost(ipStr);
				super.setPort(Integer.parseInt(portStr));
				super.initialize();
				
				String[] outArr = {};
				super.sendMessage("connect", outArr);
			} catch (ConnectException e) {
				responseText.setText("Could not find server.");
			}
		}
	}
}
