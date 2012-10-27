package com.bryanmarty.ergoproxy;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpParser {
	
	//Request
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
	
	public static final Pattern pResponseCode = Pattern.compile("^HTTP/1\\.(0|1)[\\W]+([0-9]+)[\\W]+(.*)$",Pattern.MULTILINE);
	public static final Pattern pBody = Pattern.compile("^\\s*$^(.*)");
	
	
	//Response
	public static final Pattern pContentLength = Pattern.compile("^Content-Length:[\\W]+([0-9]+)$",Pattern.MULTILINE);
	
	public static HttpRequest parse(String info) {
		HttpRequest request = new HttpRequest();
		
		//GET and HTTP version number
		Matcher m = pGetVersion.matcher(info);
		if(m.find()) {
			request.setGet(m.group(1).trim());
			request.setVersion(m.group(2).trim());
		}
		
		m = pResponseCode.matcher(info);
		if (m.find()) {
			request.setResponseCode(Integer.valueOf(m.group(2)));
		}
		
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
		
		m = pContentLength.matcher(info);
		if(m.find()) {
			request.setContentLength(Integer.parseInt(m.group(1)));
		}
		
		Scanner sc = new Scanner(info);
		sc.useDelimiter("\r\n");
		boolean parseBody = false;
		int sum = 0;
		while (sc.hasNextLine()) {
			String line = sc.nextLine();
			System.out.println(line);
			if(line.isEmpty()) {
				parseBody = true;
			}
			if(parseBody) {
				sum += line.length() + "\r\n".getBytes().length;
			}
		}
		request.setBodyLength(sum);
		return request;
	}
	
	public static boolean hasNewLine(String data) {
		Scanner sc = new Scanner(data);
		sc.useDelimiter("\r\n");
		while (sc.hasNextLine()) {
			String line = sc.nextLine();
			if(line.isEmpty()) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean validate(String data) {
		try {
			HttpRequest request = parse(data);
			
			//(such as the 1xx, 204, and 304 responses and any response to a HEAD request)
			System.out.println(request.getResponseCode());
			if (request.getResponseCode() < 200 || request.getResponseCode() == 204 || request.getResponseCode() == 304) {
				return hasNewLine(data);
			}
			
			if(request.getResponseCode() == 301 || request.getResponseCode() == 302) {
				return hasNewLine(data);
			}
			
			if (request.getContentLength() > 0) {
			
				//System.out.println("Body:" + request.getBody());
				System.out.println("Content-Length:" + request.getContentLength());
				System.out.println("SUM: " + request.getBodyLength());
				if (request.getContentLength() <= request.getBodyLength()) {
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}
 