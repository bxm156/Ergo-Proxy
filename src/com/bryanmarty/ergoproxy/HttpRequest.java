package com.bryanmarty.ergoproxy;

public class HttpRequest {
	private String get;
	private String version;
	private String host;
	private String proxyconnection;
	private String useragent;
	private String accept;
	private String acceptencoding;
	private String acceptlanguage;
	private String acceptcharset;
	private String cookie;
	private String dnt;
	
	public String getGet() {
		return get;
	}
	public void setGet(String get) {
		this.get = get;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getProxyconnection() {
		return proxyconnection;
	}
	public void setProxyconnection(String proxyconnection) {
		this.proxyconnection = proxyconnection;
	}
	public String getUseragent() {
		return useragent;
	}
	public void setUseragent(String useragent) {
		this.useragent = useragent;
	}
	public String getAccept() {
		return accept;
	}
	public void setAccept(String accept) {
		this.accept = accept;
	}
	public String getAcceptencoding() {
		return acceptencoding;
	}
	public void setAcceptencoding(String acceptencoding) {
		this.acceptencoding = acceptencoding;
	}
	public String getAcceptlanguage() {
		return acceptlanguage;
	}
	public void setAcceptlanguage(String acceptlanguage) {
		this.acceptlanguage = acceptlanguage;
	}
	public String getAcceptcharset() {
		return acceptcharset;
	}
	public void setAcceptcharset(String acceptcharset) {
		this.acceptcharset = acceptcharset;
	}
	public String getCookie() {
		return cookie;
	}
	public void setCookie(String cookie) {
		this.cookie = cookie;
	}
	public String getDnt() {
		return dnt;
	}
	public void setDnt(String dnt) {
		this.dnt = dnt;
	}
}
