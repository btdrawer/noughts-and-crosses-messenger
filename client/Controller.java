package client;

abstract class Controller {
	private static Client client = Main.getClient();
	
	void initialize() {
		client.getListener().setController(this);
	}
	
	abstract void processInput(String action, String[] input);
}
