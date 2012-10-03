package com.bryanmarty.ergoproxy;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class SocketTask implements Runnable {

	private final Socket socket_;
	
	public SocketTask(Socket clientSocket) {
		socket_ = clientSocket;
	}

	@Override
	public void run() {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(socket_.getInputStream()));
			reader.readLine();
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket_.getOutputStream()));
			writer.write("Hello");
			writer.newLine();
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				socket_.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
