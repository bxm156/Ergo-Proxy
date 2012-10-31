package com.bryanmarty.ergoproxy;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ConcurrentHashMap;

public class DNSCache {
	
	private static DNSCache instance_;
	private ConcurrentHashMap<String,DNSEntry> hashMap;
	private static long TTL = 300000; //30 seconds (in milliseconds)
	
	private class DNSEntry {
		public InetAddress ip;
		public long timestamp;
	}
	
	/**
	 * Gets the single instance of DNSCache
	 *
	 * @return single instance of DNSCache
	 */
	public static synchronized DNSCache getInstance() {
		if (instance_ == null) {
			instance_ = new DNSCache();
		}
		return instance_;
	}
	
	public DNSCache() {
		hashMap = new ConcurrentHashMap<String,DNSEntry>();
	}
	
	/**
	 * Store the ip in the hashmap with the key as the host.
	 *
	 * @param host the host
	 * @param ip the ip
	 * @return the inet address (the same as the ip that was given)
	 */
	private synchronized InetAddress store(String host, InetAddress ip) {
		DNSEntry entry = new DNSEntry();
		entry.ip = ip;
		entry.timestamp = System.currentTimeMillis();
		hashMap.put(host, entry);
		return ip;
	}
	
	/**
	 * Retrieve the IP Address for the host.
	 * If the value exists in the cache and < 30 seconds old, the IP will be returned.
	 * 
	 * Otherwise, we will query the DNS for the ip and store the result in the cache.
	 *
	 * @param host the host
	 * @return the inet address
	 * @throws UnknownHostException the unknown host exception
	 * @throws NullPointerException the null pointer exception
	 */
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
