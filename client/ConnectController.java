package client;

import java.io.IOException;
import java.net.ConnectException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import protocol.Constants;

/**
 * Controller for the Connect view.
 * 
 * @author Ben Drawer
 * @version 20 September 2018
 *
 */
public class ConnectController extends Controller {
	@FXML protected TextField ip;
	@FXML protected TextField port;
	@FXML protected Text responseText;
	private static Client client = Main.getClient();
	private static final String CONNECT = Constants.CONNECT,
			SIGN_IN_PANEL = PanelConstants.SIGN_IN_PANEL;
	
	/**
	 * The initialize method here is set to do nothing, as the
	 * client's connection with a server has not yet been established.
	 */
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
	void processInput(String action, String[] input) {
		if (action.equals(CONNECT))
			connect(input);
	}
	
	/**
	 * Tests the connection with the server.
	 * 
	 * @param input
	 * @throws IOException
	 */
	private void connect(String[] input) {
		if (input.length > 0)
			Main.changeScene(SIGN_IN_PANEL);
		else
			responseText.setText("Unable to join server.\nPlease try again later.");
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
		
		if (ipStr.isEmpty() || portStr.isEmpty()) {
			responseText.setText("IP address and/or port number\ncannot be left blank.");
		} else {
			try {
				Main.setClient(new Client());
				client = Main.getClient();
				
				client.setHost(ipStr);
				client.setPort(Integer.parseInt(portStr));
				client.initialize();
				client.getListener().setController(this);
				
				String[] outArr = {};
				client.sendMessage(CONNECT, outArr);
			} catch (ConnectException e) {
				responseText.setText("Could not find server.");
			}
		}
	}
}
