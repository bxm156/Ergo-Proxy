package com.bryanmarty.ergoproxy;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ConcurrentHashMap;

public class DNSCache {
	
	private static DNSCache instance_;
	private ConcurrentHashMap<String,DNSEntry> hashMap;
	private static long TTL = 30*10^3;
	
	private class DNSEntry {
		public InetAddress ip;
		public long timestamp;
	}
	
	public static synchronized DNSCache getInstance() {
		if (instance_ == null) {
			instance_ = new DNSCache();
		}
		return instance_;
	}
	
	public DNSCache() {
		hashMap = new ConcurrentHashMap<String,DNSEntry>();
	}

	private synchronized InetAddress store(String host, InetAddress ip) {
		DNSEntry entry = new DNSEntry();
		entry.ip = ip;
		entry.timestamp = System.currentTimeMillis();
		hashMap.put(host, entry);
		return ip;
	}
	
	public synchronized InetAddress retrieve(String host) throws UnknownHostException, NullPointerException {
		DNSEntry entry = null;
		if((entry = hashMap.get(host)) != null) {
			if (System.currentTimeMillis() - entry.timestamp < TTL) {
				return entry.ip;
			}
		}
		return store(host,InetAddress.getByName(host));
	}

}
