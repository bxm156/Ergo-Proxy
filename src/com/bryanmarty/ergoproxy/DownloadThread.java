package com.bryanmarty.ergoproxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * The Class DownloadThread is responsible for sending the remote servers output
 * to the client.
 */
public class DownloadThread extends Thread {

	HttpRequest request_;
	OutputStream client_;
	Socket server_;
	
	public DownloadThread(OutputStream client, Socket server, HttpRequest request) {
		request_ = request;
		client_ = client;
		server_ = server;
	}
	
	@Override
	public void run() {
		int d;
		byte[] buffer = new byte[1024*1024];
		try {
			InputStream sis = server_.getInputStream();
			OutputStream sos = server_.getOutputStream();
			sos.write(request_.getRequest().getBytes("US-ASCII"));
			System.out.println(request_.getRequest()); //Debugging output
			sos.flush();
			while(( d = sis.read(buffer,0,buffer.length)) != -1) {
				//Transfer the data and flush it to the client
				client_.write(buffer, 0, d);
				client_.flush();
			}
		} catch(IOException e) {
			//The SocketTask closes the server socket when the client issues a new HTTP request
		}
	}
}
