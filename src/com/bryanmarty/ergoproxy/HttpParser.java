package com.bryanmarty.ergoproxy;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpParser {
	
	public static Pattern pGetVersion = Pattern.compile("^GET[\\W]{1}(.*)[\\W]{1}HTTP/(1.1).*$",Pattern.MULTILINE);
	public static Pattern pHost = Pattern.compile("^Host:[\\W]+(.*)$",Pattern.MULTILINE & Pattern.UNIX_LINES);
	
	public static HttpRequest parse(String info) {
		HttpRequest request = new HttpRequest();
		
		//GET and HTTP version number
		Matcher m = pGetVersion.matcher(info);
		if(m.find()) {
			request.setGet(m.group(1).trim());
			request.setVersion(m.group(2).trim());
		}
		
		/*
		m = pHost.matcher(info);
		if(m.find()) {
			System.out.println("Host Found");
			request.setHost(m.group(1).trim());
		}
		*/
		
		
		
		return request;
	}

}
 