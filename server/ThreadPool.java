package server;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Thread pool for threads handling requests.
 * 
 * @author Ben Drawer
 * @version 30 May 2018
 *
 */
class ThreadPool {
	private static LinkedBlockingQueue<Task> taskQueue = Server.getTaskQueue();
	private Thread[] threads;
	
	/**
	 * Constructor.
	 * 
	 * @param quantity number of request threads to initiate
	 */
	ThreadPool(int quantity) {
		threads = new Thread[quantity];
		
		for(int i = 0; i < quantity; i++) {
			threads[i] = new TaskThread();
			threads[i].start();
		}
	}
	
	/**
	 * Adds a request to the request queue.
	 * 
	 * @param newRequest
	 */
	void add(Task newTask) {
		taskQueue.add(newTask);
	}
	
	/**
	 * Ends the execution of the thread pool.
	 */
	void stop() {
		for(Thread t : threads) {
			t.interrupt();
		}
	}
}
