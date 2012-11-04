package com.bryanmarty.ergoproxy;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// TODO: Auto-generated Javadoc
/**
 * The Class HttpParser.
 */
public class HttpParser {
	
	//Request
	/** The Constant pGetVersion. */
	public static final Pattern pGetVersion = Pattern.compile("^(GET|POST|HEAD)[\\W]{1}(.*)[\\W]{1}HTTP/(.*)$",Pattern.MULTILINE);
	
	/** The Constant pHost. */
	public static final Pattern pHost = Pattern.compile("^Host:[\\W]+(.*)$",Pattern.MULTILINE);
	
	
	/**
	 * Parses the HTTP Request for specific values.
	 *
	 * @param info the HTTP request string
	 * @return the HttpRequest object for that request.
	 */
	public static HttpRequest parse(String info) {
		HttpRequest request = new HttpRequest();
		URL url = null;
		Matcher m = pGetVersion.matcher(info);
		if(m.find()) {
			try{
				//Try and get the relative url
				String file = m.group(2).trim();
				url = new URL(file);
				info = m.replaceFirst(Matcher.quoteReplacement(m.group(1).trim() + " " + url.getFile() + " HTTP/" + m.group(3)));
			} catch (MalformedURLException mue) {
				// We will use the original request
			}
		}
		
		m = pHost.matcher(info);
		if(m.find()) {
			//Try and handle if the Host field contains a port number
			String host = m.group(1).trim();
			String[] pieces = host.split(":");
			if(pieces.length == 1) {
				request.setHost(host);
			}
			if(pieces.length == 2) {
				request.setHost(pieces[0]);
				request.setPort(Integer.valueOf(pieces[1]));
			}
		}
		
		//Just in case the host is blank, but we have the requested file...
		if ((request.getHost() == null || request.getHost().isEmpty()) && url != null) {
			request.setHost(url.getHost());
		}
		
		request.setRequest(info);
		return request;
	}

}
 