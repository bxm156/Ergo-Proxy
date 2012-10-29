package com.bryanmarty.ergoproxy;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpParser {
	
	//Request
	public static final Pattern pHost = Pattern.compile("^Host:[\\W]+(.*)$",Pattern.MULTILINE);
	public static final Pattern pConnection = Pattern.compile("^Connection:[\\W]+(.*)$",Pattern.MULTILINE);
	public static final Pattern pPConnection = Pattern.compile("^Proxy-Connection:[\\W]+(.*)$",Pattern.MULTILINE);
	public static HttpRequest parse(String info) {
		HttpRequest request = new HttpRequest();

		Matcher m = pHost.matcher(info);
		if(m.find()) {
			request.setHost(m.group(1).trim());
		}
		
		m = pConnection.matcher(info);
		if(m.find()) {
			info = m.replaceFirst("Connection: close\r\n");
		}
		
		m = pPConnection.matcher(info);
		if(m.find()) {
			info = m.replaceFirst("Proxy-Connection: close\r\n");
		}
		
		System.out.println(info);
		request.setRequest(info);
		return request;
	}

}
 