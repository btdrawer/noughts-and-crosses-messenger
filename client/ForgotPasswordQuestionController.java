package client;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import protocol.Constants;
import protocol.Protocol;

/**
 * Controller for pane where the user gives their security answer.
 * 
 * @author Ben Drawer
 * @version 11 October 2018
 *
 */
public class ForgotPasswordQuestionController extends Controller {
	@FXML protected TextField questionAnswer;
	@FXML protected Text question, responseText;
	private String username;
	private static Client client = Main.getClient();
	private static String[] data = Main.getData();
	private Protocol protocol = client.getProtocol();
	private static final String FORGOT_PASSWORD_ANSWER = Constants.FORGOT_PASSWORD_ANSWER,
			FORGOT_PASSWORD_PANEL = PanelConstants.FORGOT_PASSWORD_PANEL,
			FORGOT_PASSWORD_CHANGE_PANEL = PanelConstants.FORGOT_PASSWORD_CHANGE_PANEL;
	
	public void initialize() {
		super.initialize();
		
		this.username = data[0];
		question.setText(data[1]);
	}
	
	void processInput(String action, boolean result, String[] input) {
		switch (action) {
			case FORGOT_PASSWORD_ANSWER:
				forgotPasswordAnswerHandler(result, input);
		}
	}
	
	/**
	 * Handler for back button.
	 * 
	 * @param event
	 */
	@FXML
	protected void backButtonHandler(ActionEvent event) {
		Main.changeScene(FORGOT_PASSWORD_PANEL);
	}
	
	/**
	 * Handler for submit button.
	 * 
	 * @param event
	 * @throws IOException
	 * @throws NoSuchAlgorithmException 
	 */
	@FXML
	protected void submitButtonHandler(ActionEvent event) throws IOException, 
			NoSuchAlgorithmException {
		String[] outArr = {username, protocol.getMD5(questionAnswer.getText())};
		client.sendMessage(FORGOT_PASSWORD_ANSWER, outArr);
	}
	
	/**
	 * Handler for message from server.
	 * 
	 * [0] = username
	 * [1] = error message if present
	 * 
	 * @param input
	 */
	private void forgotPasswordAnswerHandler(boolean result, String[] input) {
		if (result) {
			String[] data = {username};
			Main.setData(data);
			Main.changeScene(FORGOT_PASSWORD_CHANGE_PANEL);
		} else
			responseText.setText(input[1]);
	}
}
