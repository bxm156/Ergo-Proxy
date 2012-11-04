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
		//We were given a null client, so ignore and quit the thread.
		if(client_ == null) {
			return;
		}
		
		//Setup the input and output stream to the client.
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
		//Now we read data from the client and launch download threads...
		try {
			StringBuffer bufferData = new StringBuffer();
			/*We set to the client timeout. This allows us to to check for various
			conditions while reading. Timing out does not imply that we will close the socket,
			or that we are done with the socket */
			client_.setSoTimeout(1000);
			while(client_.isConnected()) {
				String line = null;
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
					/* Thus just means we have not had a new request yet, we will check the current
					 * status of things and then keep waiting for more requests from the browser.
					 */
					if(!client_.isConnected() || client_.isOutputShutdown() || !download_.isAlive()) {
						//We have may have downloaded the last item. The client may have left the connection to our proxy open,
						//but has not given us any new requests, there are no connections to remote servers at this time.
						break;
						
					}
					//We will continue accepting more data.
					continue;
					
				}
				
				//The client has closed the socket...
				if(line == null) {
					//End the task
					break;
				}
				
				//Take the HTTP Request, create a string of it, and clear the buffer.
				String data = bufferData.toString();
				bufferData.setLength(0);
				if(data.isEmpty()) {
					//We dont want to act upon empty data
					continue;
				}
				
				//Parse the request
				HttpRequest request = HttpParser.parse(data);
				
				//EECS 425 DNS Cache
				InetAddress hostIP = null;
				try {
					//Try and retrieve the IP Address
					hostIP = DNS_CACHE.retrieve(request.getHost());
				} catch (NullPointerException npe) {
					continue;
				}
				
				/* Before we open a new request to a server, close the existing
				 * connection to the previous remote server, which we are expecting
				 * no new data.
				 */
				closeDownloadThread();
				
				//Connect to the server
				server_ = new Socket(hostIP,request.getPort());
				server_.setSoTimeout(5000); /* In case of the last item to be downloaded, we may never receive another HTTP request
				from the client, and the client may leave our connection open, so we have this to terminate the connection to server.
				This value is not a timeout of the entire socket, but the amount of time that we will wait for new data to be read. */
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
