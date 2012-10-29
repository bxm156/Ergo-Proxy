package com.bryanmarty.ergoproxy;

public class HttpRequest {
	private String host;
	private int port = 80;
	private String request;
	private String version = "";
	
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
	public void setVersion(String trim) {
		this.version = trim;
	}
	public String getVersion() {
		return this.version;
	}
}
