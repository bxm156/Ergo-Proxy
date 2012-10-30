package com.bryanmarty.ergoproxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

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
			System.out.println(request_.getRequest());
			sos.flush();
			while(( d = sis.read(buffer,0,buffer.length)) != -1) {
				client_.write(buffer, 0, d);
				client_.flush();
			}
		} catch(IOException e) {
		}
	}
}
