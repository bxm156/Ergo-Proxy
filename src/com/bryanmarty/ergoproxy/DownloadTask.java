package com.bryanmarty.ergoproxy;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.util.concurrent.Callable;



public class DownloadTask implements Callable<ByteBuffer> {
	
	private String host_;
	private int port_;
	private String request_;
	private boolean valid = false;
	ByteBuffer data = ByteBuffer.allocate(10*1024*1024);
	
	public DownloadTask(String host, int port, String request) {
		host_ = host;
		port_ = port;
		request_ = request;
	}

	@Override
	public ByteBuffer call() throws Exception {
		Socket sRequest = null;
		while(!valid) {
			try {
				sRequest = new Socket(host_,port_);
				BufferedWriter toServer = new BufferedWriter(new OutputStreamWriter(sRequest.getOutputStream()));
				HttpIO.send(request_, toServer);
				sRequest.shutdownOutput();
				
				int d;
				byte[] buffer = new byte[4098];
				while(sRequest.isConnected()) {
					d = sRequest.getInputStream().read(buffer,0,buffer.length);
					if(d == -1) {
						valid = true;
						sRequest.close();
						break;
					}

					try {
						data.put(buffer, 0, d);
					} catch(BufferOverflowException boe) {
						int newSize = data.capacity()*2;
						ByteBuffer newBuffer = ByteBuffer.allocate(newSize);
						newBuffer.put(data);
						newBuffer.put(buffer, 0, d);
						data = newBuffer;
					}
				}
			} catch (SocketException e) {
				//e.printStackTrace();
				System.out.println("Request was: "+ request_);
				data = ByteBuffer.allocate(data.capacity());
				valid = false;
				if(sRequest != null && !sRequest.isClosed()) {
					sRequest.close();
				}
			}
		}
		return data;
	}

}
