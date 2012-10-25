package com.bryanmarty.ergoproxy;

public class HttpRequest {
	private String get;
	private String version;
	private String host;
	private String connection;
	private String cacheControl;
	private String useragent;
	private String accept;
	private String acceptencoding;
	private String acceptlanguage;
	private String acceptcharset;
	private String cookie;
	private String dnt;
	
	private int responseCode;
	private int contentLength;
	private int bodyLength;
	private String body;
	
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
	public String getConnection() {
		return connection;
	}
	public void setConnection(String proxyconnection) {
		this.connection = proxyconnection;
	}
	public String getCacheControl() {
		return cacheControl;
	}
	public void setCacheControl(String cacheControl) {
		this.cacheControl = cacheControl;
	}
	public String getUserAgent() {
		return useragent;
	}
	public void setUserAgent(String useragent) {
		this.useragent = useragent;
	}
	public String getAccept() {
		return accept;
	}
	public void setAccept(String accept) {
		this.accept = accept;
	}
	public String getAcceptEncoding() {
		return acceptencoding;
	}
	public void setAcceptEncoding(String acceptencoding) {
		this.acceptencoding = acceptencoding;
	}
	public String getAcceptLanguage() {
		return acceptlanguage;
	}
	public void setAcceptLanguage(String acceptlanguage) {
		this.acceptlanguage = acceptlanguage;
	}
	public String getAcceptCharset() {
		return acceptcharset;
	}
	public void setAcceptCharset(String acceptcharset) {
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
	public int getContentLength() {
		return contentLength;
	}
	public void setContentLength(int contentLength) {
		this.contentLength = contentLength;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public int getBodyLength() {
		return bodyLength;
	}
	public void setBodyLength(int bodyLength) {
		this.bodyLength = bodyLength;
	}
	public int getResponseCode() {
		return responseCode;
	}
	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}
}
