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
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket_.getOutputStream()));
			
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
			//reader.close();
			socket_.shutdownInput();
			
			String data = dataBuffer.toString();
			//System.out.println(data);
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
			//BufferedReader fromServer = new BufferedReader(new InputStreamReader(sRequest.getInputStream()));
			BufferedReader fromServer = new BufferedReader(new InputStreamReader(sRequest.getInputStream()));
			HttpIO.send(data, toServer);
			
			StringBuffer sb = new StringBuffer();
			char[] buffer = new char[1024];
			int d;
			long startTime = System.currentTimeMillis();
			while(!sRequest.isInputShutdown()) {
				if(startTime - System.currentTimeMillis() > 7000) {
					break;
				}
				if(fromServer.ready()) {
					while((d = fromServer.read(buffer,0,buffer.length)) != -1) {
						String s = new String(buffer, 0, d);
						sb.append(s);
						if(!fromServer.ready()) {
							break;
						}
					}
					if(sb.length() > 0 && HttpParser.validate(sb.toString())) {
						break;
					}
				}				
			}
			writer.write(sb.toString());
			writer.flush();
			
			//fromServer.close();
			sRequest.shutdownInput();
			socket_.shutdownOutput();

			
			sRequest.close();
			socket_.close();
			
			
			
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
