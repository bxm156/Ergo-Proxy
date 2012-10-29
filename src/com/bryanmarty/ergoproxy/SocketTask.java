package com.bryanmarty.ergoproxy;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class SocketTask implements Runnable {

	private static final String NEW_LINE = "\r\n";
	private static final DataCache DATA_CACHE = DataCache.getInstance();
	private final Socket socket_;
	
	public SocketTask(Socket clientSocket) {
		socket_ = clientSocket;
	}

	@Override
	public void run() {
		if(socket_ == null) {
			return;
		}
		int count = 1;
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(socket_.getInputStream()));
			//BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket_.getOutputStream()));
			
			StringBuffer dataBuffer = new StringBuffer();
			String line;
			while(socket_.isConnected()) {
				while ((line = reader.readLine()) != null) {
					System.out.println("Line: " + line);
					//HTTP Response are terminated by a blank line
					if(line.isEmpty()) {
						break;
					}
					dataBuffer.append(line);
					dataBuffer.append(NEW_LINE);
				}
				if(line == null) {
					break;
				}
				System.out.println("Finished Reading");
				System.out.println("Count: " + count);
				count++;
				String data = dataBuffer.toString();
				HttpRequest request = HttpParser.parse(data);
				Socket sRequest = new Socket(request.getHost(), 80);
				BufferedWriter toServer = new BufferedWriter(new OutputStreamWriter(sRequest.getOutputStream()));
				HttpIO.send(data, toServer);
				
				int d;
				byte[] buffer = new byte[4098];
				while(socket_.isConnected() && sRequest.isConnected()) {
					d = sRequest.getInputStream().read(buffer,0,buffer.length);
					if(d == -1) {	
						break;
					}
					socket_.getOutputStream().write(buffer, 0, d);
					socket_.getOutputStream().flush();
				}
				System.out.println("Server responded and terminated");
				sRequest.close();
			}	
			
		} catch (IOException e) {
			e.printStackTrace();
		//} catch (InterruptedException e) {
		//	// TODO Auto-generated catch block
		//	e.printStackTrace();
		} finally {
			try {
				socket_.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
