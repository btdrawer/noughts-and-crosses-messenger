package server;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Thread pool for threads handling requests.
 * 
 * @author Ben Drawer
 * @version 20 September 2018
 *
 */
class ThreadPool {
	private static LinkedBlockingQueue<Request> requestQueue = Main.getRequestQueue();
	private Thread[] threads;
	
	/**
	 * Constructor.
	 * 
	 * @param quantity number of request threads to initiate
	 */
	ThreadPool(int quantity) {
		threads = new Thread[quantity];
		
		for(int i = 0; i < quantity; i++) {
			threads[i] = new RequestThread();
			threads[i].start();
		}
	}
	
	/**
	 * Adds a request to the request queue.
	 * 
	 * @param newRequest
	 */
	void add(Request newRequest) {
		requestQueue.add(newRequest);
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
