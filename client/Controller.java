package client;

abstract class Controller {
	private static Client client;
	
	public void initialize() {
		client = Main.getClient();
		client.getListener().setController(this);
	}
	
	abstract void processInput(String action, String[] input);
}
