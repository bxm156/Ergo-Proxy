package com.bryanmarty.ergoproxy;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpParser {
	
	//Request
	public static final Pattern pHost = Pattern.compile("^Host:[\\W]+(.*)$",Pattern.MULTILINE);
	
	
	public static HttpRequest parse(String info) {
		HttpRequest request = new HttpRequest();
		request.setRequest(info);

		Matcher m = pHost.matcher(info);
		if(m.find()) {
			request.setHost(m.group(1).trim());
		}
		request.setRequest(info);
		return request;
	}

}
 