package server;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Thread class that takes an item from the request queue.
 * 
 * @author Ben Drawer
 *
 */
class TaskThread extends Thread {
	private LinkedBlockingQueue<Task> taskQueue = Main.getTaskQueue();
	
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
