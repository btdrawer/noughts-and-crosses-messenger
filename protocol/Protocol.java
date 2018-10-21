package protocol;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Protocol class.
 * Transmits data between clients and server.
 * 
 * @author Ben Drawer
 * @version 11 October 2018
 *
 */
public class Protocol {
	/**
	 * 
	 * @param a input String from client
	 * @return action word (e.g., signup, signin, etc.)
	 */
	public String getAction(String a) {
		return a.split("//")[0];
	}
	
	/**
	 * 
	 * @param a true or false
	 * @return boolean value
	 */
	public boolean getResult(String a) {
		return Boolean.parseBoolean(a.split("//")[1]);
	}
	
	/**
	 * Transmits data.
	 * 
	 * @param action action word (e.g., signup, signin, etc.)
	 * @param a array of information pertaining to the attempted action
	 * @return String containing the action word and other information
	 */
	public String transmit(String action, String[] a) {
		StringBuilder sb = new StringBuilder();
		
		for(String s : a) {
			sb.append(s + "//");
		}
		
		return action + "//" + sb.toString() + "\n";
	}
	
	/**
	 * Transmits data.
	 * Similar to other transmit method, but takes a String for input information
	 * rather than an array.
	 * 
	 * @param action
	 * @param a String of information pertaining to the attempted action
	 * @return String containing the action word and other information
	 */
	public String transmit(String action, String a) {
		return action + "//" + a + "\n";
	}
	
	/**
	 * Transmits data.
	 * Similar to other transmit method, but takes a String for input information
	 * rather than an array.
	 * 
	 * @param action
	 * @param result response from the server stating whether or not the action was successful
	 * @param a array of information pertaining to the attempted action
	 * @return String containing the action word and other information
	 */
	public String transmit(String action, boolean result, String[] a) {
		StringBuilder sb = new StringBuilder();
		
		for(String s : a) {
			sb.append(s + "//");
		}
		
		return action + "//" + result + "//" + sb.toString() + "\n";
	}
	
	/**
	 * Transmits data.
	 * Similar to other transmit method, but takes a String for input information
	 * rather than an array.
	 * 
	 * @param action
	 * @param result response from the server stating whether or not the action was successful
	 * @param a String of information pertaining to the attempted action
	 * @return String containing the action word and other information
	 */
	public String transmit(String action, boolean result, String a) {
		return action + "//" + result + "//" + a + "\n";
	}
	
	/**
	 * Used to receive information.
	 * 
	 * @param a input String containing action word and other information
	 * @return String array of same information, excluding the action word
	 */
	public String[] receive(String a) {
		String[] s0 = a.split("//");
		int offset;
		
		if (s0.length < 2)
			offset = 1;
		else if (s0[1].equals("true") || s0[1].equals("false"))
			offset = 2;
		else
			offset = 1;
		
		int length = s0.length-offset;
		String[] s1 = new String[length];
		
		for(int i = 0; i < length; i++) {
			s1[i] = s0[i+offset];
		}
		
		return s1;
	}
	
	/**
	 * Password hasher.
	 * 
	 * @param input password
	 * @return MD5 password hash
	 * @throws NoSuchAlgorithmException if hashing algorithm is unavailable
	 */
	public String getMD5(String input) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.reset();
		md.update(input.getBytes());
		byte[] digest = md.digest();
		
		StringBuilder s = new StringBuilder();
		
		for (byte b : digest) {
			s.append(b);
		}
		
		return s.toString();
	}
}
