package com.bryanmarty.ergoproxy;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ErgoProxy {

	private static final int LISTEN_PORT = 8080;
	private static BlockingQueue<Runnable> blockingQueue;
	private static ThreadPoolExecutor workerPool;

	public static void main(String[] args) {
		blockingQueue = new LinkedBlockingQueue<Runnable>();
		workerPool = new ThreadPoolExecutor(5, 100, 1, TimeUnit.MINUTES,
				blockingQueue);
		try {
			run();
		} catch (InterruptedException e) {

		}
	}

	public static void run() throws InterruptedException {
		Thread listener = new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					try {
						ServerSocket myServer = new ServerSocket(LISTEN_PORT);
						Socket mySocket = myServer.accept();

						SocketTask task = new SocketTask(mySocket);
						workerPool.execute(task);
					} catch (IOException io) {

					}
				}

			}

		});

		listener.start();
		listener.join();
	}
}
