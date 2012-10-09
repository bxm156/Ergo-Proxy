package com.bryanmarty.ergoproxy;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolServer {
	private int port_;
	private ServerSocket myServer_;
	private BlockingQueue<Runnable> blockingQueue_;
	private ThreadPoolExecutor workerPool_;
	Thread serverHandler_;
	
	public ThreadPoolServer(int port) {
		port_ = port;
		blockingQueue_ = new LinkedBlockingQueue<Runnable>();
		workerPool_ = new ThreadPoolExecutor(5, 100, 1, TimeUnit.MINUTES, blockingQueue_);
	}
	
	public synchronized void start() {
		
		serverHandler_ = new Thread(new Runnable() {

			@Override
			public void run() {
				
					try {
						myServer_ = new ServerSocket(port_);
					} catch (IOException io) {
						System.out.println("Failed to open socket: " + io.getMessage());
						return;
					}
					System.out.println("Listening on port: " + port_);
					
					
					while (true) {
						if( Thread.currentThread().isInterrupted() ) {
							System.out.println("Closing Thread");
							return;
						}
						Socket mySocket = null;
						try {
							mySocket = myServer_.accept();
						} catch (SocketException e) {
							continue;
						} catch (IOException e) {
							e.printStackTrace();
						}
						SocketTask task = new SocketTask(mySocket);
						workerPool_.execute(task);
					}
				}
			});
		serverHandler_.start();
	}
	
	public synchronized void stop() {
		System.out.println("Stopping Server Thread..");
		try {
			serverHandler_.interrupt();
			if(myServer_ != null) {
				myServer_.close();
			}
			serverHandler_.join();
			workerPool_.shutdown();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Complete");
	}
}
