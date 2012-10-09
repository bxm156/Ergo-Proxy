package com.bryanmarty.ergoproxy;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpParser {
	
	public static final Pattern pGetVersion = Pattern.compile("^GET[\\W]{1}(.*)[\\W]{1}HTTP/(1.1).*$",Pattern.MULTILINE);
	public static final Pattern pHost = Pattern.compile("^Host:[\\W]+(.*)$",Pattern.MULTILINE);
	public static final Pattern pConnection = Pattern.compile("^Connection:[\\W]+(.*)$",Pattern.MULTILINE);
	public static final Pattern pCacheControl = Pattern.compile("^Cache-Control:[\\W]+(.*)$",Pattern.MULTILINE);
	public static final Pattern pUserAgent = Pattern.compile("^User-Agent:[\\W]+(.*)$",Pattern.MULTILINE);
	public static final Pattern pAccept = Pattern.compile("^Accept:[\\W]+(.*)$",Pattern.MULTILINE);
	public static final Pattern pAcceptEncoding = Pattern.compile("^Accept-Encoding:[\\W]+(.*)$",Pattern.MULTILINE);
	public static final Pattern pAcceptLanguage = Pattern.compile("^Accept-Language:[\\W]+(.*)$",Pattern.MULTILINE);
	public static final Pattern pAcceptCharset = Pattern.compile("^Accept-Charset:[\\W]+(.*)$",Pattern.MULTILINE);
	public static final Pattern pCookie = Pattern.compile("^Cookie:[\\W]+(.*)$",Pattern.MULTILINE);
	public static final Pattern pDNT = Pattern.compile("^DNT:[\\W]+(.*)$",Pattern.MULTILINE);
	
	public static HttpRequest parse(String info) {
		HttpRequest request = new HttpRequest();
		
		//GET and HTTP version number
		Matcher m = pGetVersion.matcher(info);
		if(m.find()) {
			request.setGet(m.group(1).trim());
			request.setVersion(m.group(2).trim());
		}
		
		request.setHost(info);
		
		m = pHost.matcher(info);
		if(m.find()) {
			request.setHost(m.group(1).trim());
		}
		
		m = pConnection.matcher(info);
		if(m.find()) {
			request.setConnection(m.group(1).trim());
		}
		
		m = pCacheControl.matcher(info);
		if(m.find()) {
			request.setCacheControl(m.group(1).trim());
		}
		
		m = pUserAgent.matcher(info);
		if(m.find()) {
			request.setUserAgent(m.group(1).trim());
		}
		
		m = pAccept.matcher(info);
		if(m.find()) {
			request.setAccept(m.group(1).trim());
		}
		
		m = pAcceptEncoding.matcher(info);
		if(m.find()) {
			request.setAcceptEncoding(m.group(1).trim());
		}
		
		m = pAcceptLanguage.matcher(info);
		if(m.find()) {
			request.setAcceptLanguage(m.group(1).trim());
		}
		
		m = pAcceptCharset.matcher(info);
		if(m.find()) {
			request.setAcceptCharset(m.group(1).trim());
		}
		
		m = pCookie.matcher(info);
		if(m.find()) {
			request.setCookie(m.group(1).trim());
		}
		
		m = pDNT.matcher(info);
		if(m.find()) {
			request.setDnt(m.group(1).trim());
		}

		return request;
	}

}
 