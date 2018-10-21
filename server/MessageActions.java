package server;

import java.io.IOException;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import protocol.Constants;
import protocol.Protocol;

/**
 * 
 * @author Ben Drawer
 * @version 21 October 2018
 *
 */
class MessageActions {
	private Protocol protocol;
	private static Map<String, Socket> sockets = Main.getSockets();
	private static final String GET_MESSAGES = Constants.GET_MESSAGES,
			GET_NEW_MESSAGE = Constants.GET_NEW_MESSAGE,
			SEND_MESSAGE = Constants.SEND_MESSAGE;
	
	/**
	 * Constructor.
	 */
	MessageActions() {
		this.protocol = new Protocol();
	}
	
	/**
	 * Gets messages from server.
	 * 
	 * [0], [1] = usernames whose conversation is being requested
	 * [2] = offset
	 * 
	 * @param input
	 * @return
	 */
	String getMessages(String[] input) {
		List<Message> messagesList = Database.getMessages(input[0], input[1]);
		String[] messages;
		
		if (messagesList.size() > 0) {
			messages = new String[messagesList.size() * 3];
			
			int j = 0;
			
			for (int i = 0; i < messages.length && j < messagesList.size(); i += 3) {
				Message m = messagesList.get(j);
				
				messages[i] = m.getTimestamp().toString();
				messages[i+1] = m.getSender();
				messages[i+2] = m.getMessage();
				
				j += 1;
			}
		} else {
			messages = new String[1];
			messages[0] = "";
		}
		
		return protocol.transmit(GET_MESSAGES, true, messages);
	}
	
	/**
	 * Sends a message.
	 * 
	 * @param input
	 * @return
	 * @throws IOException
	 */
	String sendMessage(String[] input) throws IOException {
		boolean sent = Database.sendMessage(
				new Message(Timestamp.valueOf(input[0]), input[1], input[2], input[3]));
		
		if (sent) {
			sockets.get(input[2]).getOutputStream().write(
					protocol.transmit(GET_NEW_MESSAGE, true, input).getBytes());
			
			return protocol.transmit(SEND_MESSAGE, true, input);
		} else
			return protocol.transmit(SEND_MESSAGE, false, 
					"An error occurred. Please try again later.");
	}
}
