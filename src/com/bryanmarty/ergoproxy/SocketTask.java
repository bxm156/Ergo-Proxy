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
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(socket_.getInputStream()));
			//BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket_.getOutputStream()));
			
			StringBuffer dataBuffer = new StringBuffer();
			String line;
			while ((line = reader.readLine()) != null) {
				//HTTP Response are terminated by a blank line
				if(line.isEmpty()) {
					break;
				}
				dataBuffer.append(line);
				dataBuffer.append(NEW_LINE);
			}
			socket_.shutdownInput();
			
			String data = dataBuffer.toString();
			HttpRequest request = HttpParser.parse(data);
			/*
			//First look for the file
			File f = null;
			if( (f = DATA_CACHE.retrieve(request.getConnection(), request.getGet())) != null) {
				//We have a file in the cache to return
			} else {
				//We must request the file from the server
				//Socket sRequest = new So
			}*/
			
			Socket sRequest = new Socket(request.getHost(), 80);
			
			BufferedWriter toServer = new BufferedWriter(new OutputStreamWriter(sRequest.getOutputStream()));
			HttpIO.send(data, toServer);
			sRequest.shutdownOutput();
			
			int d;
			byte[] buffer = new byte[4098];
			while(true) {
				d = sRequest.getInputStream().read(buffer,0,buffer.length);
				if(d == -1) {
					break;
				}
				System.out.println(new String(buffer,0,d));
				socket_.getOutputStream().write(buffer,0,d);
				socket_.getOutputStream().flush();

			}
			sRequest.close();
			socket_.getOutputStream().flush();
			socket_.shutdownOutput();
			toServer.close();
			
			
			
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
