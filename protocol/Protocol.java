package protocol;

/**
 * Protocol class.
 * Transmits data between clients and server.
 * 
 * @author Ben Drawer
 * @version 1 June 2018
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
	 * Used to receive information.
	 * 
	 * @param a input String containing action word and other information
	 * @return String array of same information, excluding the action word
	 */
	public String[] receive(String a) {
		String[] s0 = a.split("//");
		int length = s0.length-1;
		String[] s1 = new String[length];
		
		for(int i = 0; i < length; i++) {
			s1[i] = s0[i+1];
		}
		
		return s1;
	}
}
