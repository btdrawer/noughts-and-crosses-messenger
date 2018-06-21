package client;

/**
 * Abstract superclass for JavaFX controllers.
 * 
 * @author Ben Drawer
 * @version 21 June 2018
 *
 */
abstract class Controller {
	private static Client client;
	
	/**
	 * Initialize method.
	 * Receives the Client class from the Main class
	 * and notifies the Listener to now call the current Controller,
	 * as opposed to the previously-running one.
	 */
	public void initialize() {
		client = Main.getClient();
		client.getListener().setController(this);
	}
	
	/**
	 * When implemented, this method will be called by the Listener class
	 * to pass on input from the server, which the Controller then has to
	 * handle.
	 * 
	 * @param action action to be undetaken (e.g., 'signin', 'signup', etc.)
	 * @param input information associated with the action (e.g., login information)
	 */
	abstract void processInput(String action, String[] input);
}
