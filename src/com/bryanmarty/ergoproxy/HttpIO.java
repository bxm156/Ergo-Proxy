package com.bryanmarty.ergoproxy;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Scanner;

public class HttpIO {

	
	public static void send(String r, BufferedWriter os) throws IOException {
		Scanner s = new Scanner(r);
		String line;
		while(s.hasNextLine()) {
			line = s.nextLine();
			System.out.println("Sending: " + line);
			os.write(line.trim() + "\r\n");
		}
		os.write("\r\n");
		os.flush();
	}
}
