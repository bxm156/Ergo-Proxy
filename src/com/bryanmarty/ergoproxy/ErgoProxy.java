package com.bryanmarty.ergoproxy;

public class ErgoProxy {

	private static final int LISTEN_PORT = 5511;

	public static void main(String[] args) {
		
		final ThreadPoolServer server = new ThreadPoolServer(LISTEN_PORT);
		
		//If the program ends, try and shut the server down.
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

			@Override
			public void run() {
				System.out.println("Shutting Down...");
				server.stop();
			}
			
		}));

		//Start the server to listen for connections.
		server.start();
		
	}
}
