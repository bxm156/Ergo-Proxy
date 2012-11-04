package com.bryanmarty.ergoproxy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

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
		//Setup the input and output stream to the client.
		try {
			clientInput = new BufferedReader(new InputStreamReader(client_.getInputStream(),"US-ASCII"));;
			cos = client_.getOutputStream();
		} catch (IOException ioe) {
			ioe.printStackTrace();
			closeClient();
			return;
		}
		try {
			StringBuffer bufferData = new StringBuffer();
			while(client_.isConnected()) {
				String line = null;
				client_.setSoTimeout(100);
				//Read HTTP Requests
				try {
					while((line = clientInput.readLine()) != null) {
						if(line.isEmpty() && bufferData.length() > 0) {
							//We have read an entire request terminated by a empty line.
							bufferData.append(NEW_LINE);
							break;
						}
						bufferData.append(line + NEW_LINE);
					}
				} catch (SocketTimeoutException e) {
					if(!client_.isConnected() || client_.isOutputShutdown() || !download_.isAlive()) {
					
						break;
						
					}
					continue;
					
				}
				
				if(line == null) {
					//End the task
					break;
				}
				
				String data = bufferData.toString();
				bufferData.setLength(0);
				if(data.isEmpty()) {
					continue;
				}
				HttpRequest request = HttpParser.parse(data);
				
				//EECS 425 DNS Cache
				InetAddress hostIP = null;
				try {
					//Try and retrieve the IP Address
					hostIP = DNS_CACHE.retrieve(request.getHost());
				} catch (NullPointerException npe) {
					continue; //If the host was empty, ignore the request.
				}
				
				//Before we open a new request to a server, close the existing
				//connection to the previous remote server
				closeDownloadThread();
				
				//Connect to the server
				server_ = new Socket(hostIP,request.getPort());
				server_.setSoTimeout(5000);
				if(server_.isConnected()) {
					//Start the thread to transfer data to the client
					download_ = new DownloadThread(cos,server_,request);
					download_.start();
				} else {
					throw new Exception("Unable to connect to server!");
				}
			}
		} catch (Exception e) {

		} finally {
			//Stop the download thread and close the socket to the client.
			closeDownloadThread();
			closeClient();
		}
	}
	
	/**
	 * Close the download thread by closing the 
	 * remote server connection and waiting for the thread to terminate.
	 */
	public void closeDownloadThread() {
		try {
			if(server_ != null) {
				server_.close();
				if(download_ != null) {
					download_.join();
				}
			}
		} catch (Exception e) {

		}
	}
	
	/**
	 * Close the socket to the client.
	 */
	public void closeClient() {
		if(!client_.isClosed()) {
			try {
				client_.close();
			} catch (IOException io) {
				
			}
		}
	}

}
