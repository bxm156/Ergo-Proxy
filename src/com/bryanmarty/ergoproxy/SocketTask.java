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
		if(socket_ == null) {
			return;
		}
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(socket_.getInputStream()));
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket_.getOutputStream()));
			
			StringBuffer dataBuffer = new StringBuffer();
			
			String line;
			while ((line = reader.readLine()) != null) {
				//HTTP Response are terminated by a blank line
				if(line.isEmpty()) {
					break;
				}
				dataBuffer.append(line);
			}
			HttpRequest request = HttpParser.parse(dataBuffer.toString());
			writer.write("GET: " + request.getGet());
			writer.newLine();
			writer.write("Ver: " +request.getVersion());
			writer.newLine();
			writer.write("Host: " +request.getHost());
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
