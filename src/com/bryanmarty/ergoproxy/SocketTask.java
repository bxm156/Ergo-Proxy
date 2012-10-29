package com.bryanmarty.ergoproxy;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SocketTask implements Runnable {

	private static final String NEW_LINE = "\r\n";
	private static final DataCache DATA_CACHE = DataCache.getInstance();
	private final Socket socket_;
	private BlockingQueue<Runnable> blockingQueue_;
	private ThreadPoolExecutor workerPool_;
	private LinkedList<Future<ByteBuffer>> futureList = new LinkedList<Future<ByteBuffer>>();
	
	public SocketTask(Socket clientSocket) {
		socket_ = clientSocket;
		blockingQueue_ = new LinkedBlockingQueue<Runnable>();
		workerPool_ = new ThreadPoolExecutor(1, 10, 3L, TimeUnit.SECONDS, blockingQueue_);
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
			
			String line = null;
			while(!socket_.isInputShutdown()) {

				while(reader.ready()) {
					StringBuffer dataBuffer = new StringBuffer();
					Scanner sc = new Scanner(socket_.getInputStream());
					
					sc.useDelimiter("\r\n");
					while (sc.hasNextLine()) {
						line = sc.nextLine();
						if(line.isEmpty()) {
							dataBuffer.append(NEW_LINE);
							break;
						}
						dataBuffer.append(line);
						dataBuffer.append(NEW_LINE);
					}
	
					if(line == null) {
						socket_.shutdownOutput();
						break;
					}
					System.out.println("Finished Reading");
					System.out.println("Count: " + count);
					count++;
					String data = dataBuffer.toString();
					if(data.length() <= 0) {
						continue;
					}
					HttpRequest request = HttpParser.parse(data);
					
					DownloadTask d = new DownloadTask(request.getHost(),request.getPort(),data);
					Future<ByteBuffer> futureResult = workerPool_.submit(d);
					futureList.add(futureResult);
				}
				
				for(Future<ByteBuffer> future : futureList) {
					try {
						ByteBuffer download = future.get();
						socket_.getOutputStream().write(download.array(), 0, download.position());
						socket_.getOutputStream().flush();
						String output = new String(download.array(),0,download.position());
						System.out.println(output);
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (ExecutionException e) {
						e.printStackTrace();
					}
				}
				
				
				
				System.out.println("Server responded and terminated");
				if(!reader.ready()) {
					break;
				}
			}
			
			workerPool_.shutdownNow();
			
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
