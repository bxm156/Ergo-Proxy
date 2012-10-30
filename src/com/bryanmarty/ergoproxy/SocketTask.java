package com.bryanmarty.ergoproxy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class SocketTask implements Runnable {

	private static final String NEW_LINE = "\r\n";
	private static DNSCache DNS_CACHE = DNSCache.getInstance();
	private Socket client_;
	private Socket server_;
	private DownloadThread download_;
	
	public SocketTask(Socket clientSocket) {
		client_ = clientSocket;
	}

	@Override
	public void run() {
		if(client_ == null) {
			return;
		}
		BufferedReader clientInput = null;
		OutputStream cos = null;
		try {
			clientInput = new BufferedReader(new InputStreamReader(client_.getInputStream(),"US-ASCII"));;
			cos = client_.getOutputStream();
		} catch (IOException ioe) {
			ioe.printStackTrace();
			closeClient();
			return;
		}
		try {
			while(client_.isConnected()) {
				
				
				String line = null;
				StringBuffer bufferData = new StringBuffer();
				
				while((line = clientInput.readLine()) != null) {
					if(line.isEmpty() && bufferData.length() > 0) {
						bufferData.append(NEW_LINE);
						break;
					}
					bufferData.append(line + NEW_LINE);
				}
				if(line == null) {
					break;
				}
				
				String data = bufferData.toString();
				if(data.isEmpty()) {
					continue;
				}
				HttpRequest request = HttpParser.parse(data);
				
				//EECS 425 DNS Cache
				InetAddress hostIP = null;
				try {
					hostIP = DNS_CACHE.retrieve(request.getHost());
				} catch (NullPointerException npe) {
					continue;
				}
				
				closeDownloadThread();
				
				server_ = new Socket(hostIP,request.getPort());
				if(server_.isConnected()) {
					download_ = new DownloadThread(cos,server_,request);
					download_.start();
				} else {
					throw new Exception("Unable to connect to server!");
				}
				
				
			}
		} catch (Exception e) {

		} finally {
			closeDownloadThread();
			closeClient();
		}
	}
	
	public void closeDownloadThread() {
		try {
			if(server_ != null) {
				server_.close();
				if(download_ != null) {
					download_.join();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void closeClient() {
		if(!client_.isClosed()) {
			try {
				client_.close();
			} catch (IOException io) {
				
			}
		}
	}

}
