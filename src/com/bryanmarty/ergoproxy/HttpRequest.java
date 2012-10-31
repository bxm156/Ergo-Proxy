package com.bryanmarty.ergoproxy;

/**
 * The Class HttpRequest stores the HTTP request from the client and a few extra fields.
 */
public class HttpRequest {
	private String host;
	private int port = 80;
	private String request;
	
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getRequest() {
		return request;
	}
	public void setRequest(String request) {
		this.request = request;
	}
}
