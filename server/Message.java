package server;

import java.sql.Timestamp;

/**
 * Message class.
 * 
 * @author Ben Drawer
 * @version 20 October 2018
 *
 */
class Message {
	private Timestamp timestamp;
	private String sender, recipient;
	private String message;
	
	/**
	 * Constructor.
	 * 
	 * @param timestamp
	 * @param sender
	 * @param recipient
	 * @param message
	 */
	Message(Timestamp timestamp, String sender, String recipient, 
			String message) {
		this.timestamp = timestamp;
		this.sender = sender;
		this.recipient = recipient;
		this.message = message;
	}
	
	/**
	 * Constructor.
	 * 
	 * @param timestamp
	 * @param sender
	 * @param message
	 */
	Message(Timestamp timestamp, String sender, String message) {
		this.timestamp = timestamp;
		this.sender = sender;
		this.message = message;
	}
	
	/**
	 * 
	 * @return timestamp
	 */
	Timestamp getTimestamp() {
		return timestamp;
	}
	
	/**
	 * 
	 * @return username of sender
	 */
	String getSender() {
		return sender;
	}
	
	/**
	 * 
	 * @return username of recipient
	 */
	String getRecipient() {
		return recipient;
	}
	
	/**
	 * 
	 * @return message body
	 */
	String getMessage() {
		return message;
	}
	
	public String toString() {
		return "Timestamp: " + timestamp + ", Sender: " + sender + ", Recipient: " +
			recipient + ", Message: " + message;
	}
}
