package server;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Thread class that takes an item from the request queue.
 * 
 * @author Ben Drawer
 *
 */
class RequestThread extends Thread {
	private LinkedBlockingQueue<Request> taskQueue = Main.getRequestQueue();
	
	/**
	 * Run method.
	 */
	@Override
	public void run() {
		try {
			taskQueue.take().run();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
