package com.bryanmarty.ergoproxy;

public class ErgoProxy {

	private static final int LISTEN_PORT = 8080;

	public static void main(String[] args) {
		
		final ThreadPoolServer server = new ThreadPoolServer(LISTEN_PORT);
		
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

			@Override
			public void run() {
				System.out.println("Shutting Down...");
				server.stop();
			}
			
		}));

		server.start();
		
	}
}
