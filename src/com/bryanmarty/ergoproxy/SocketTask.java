package com.bryanmarty.ergoproxy;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class SocketTask implements Runnable {

	private static final String NEW_LINE = "\r\n";
	private static final DNSCache DNS_CACHE = DNSCache.getInstance();
	private final Socket client_;
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
		
		try {
			while(client_.isConnected()) {
				BufferedReader clientInput = new BufferedReader(new InputStreamReader(client_.getInputStream(),"US-ASCII"));
				OutputStream cos = client_.getOutputStream();
				
				String line = null;
				StringBuffer bufferData = new StringBuffer();
				
				while((line = clientInput.readLine()) != null) {
					if(line.isEmpty()) {
						bufferData.append(NEW_LINE);
						break;
					}
					bufferData.append(line + NEW_LINE);
				}
				
				String data = bufferData.toString();
				HttpRequest request = HttpParser.parse(data);
				
				closeDownloadThread();
				
				//EECS 425 DNS Cache
				InetAddress hostIP = null;
				try {
					hostIP = DNS_CACHE.retrieve(request.getHost());
				} catch (NullPointerException npe) {
					continue;
				}
				
				server_ = new Socket(request.getHost(),request.getPort());
				if(server_.isConnected()) {
					download_ = new DownloadThread(cos,server_,request);
					download_.start();
				}
				
				
			}
		} catch (IOException e) {
			
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
