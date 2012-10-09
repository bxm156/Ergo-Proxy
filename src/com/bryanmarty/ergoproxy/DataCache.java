package com.bryanmarty.ergoproxy;

import java.io.File;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class DataCache {
	
	private static DataCache instance_;
	private ConcurrentHashMap<String,File> hashMap;
	private Boolean valid = false;
	
	public static DataCache getInstance() {
		if (instance_ == null) {
			instance_ = new DataCache();
		}
		return instance_;
	}
	
	public DataCache() {
		hashMap = new ConcurrentHashMap<String,File>();
		synchronized(valid) {
			valid = true;
		}
	}
	
	private static String hash(String domain, String fileName) {
		return (domain + fileName);
	}

	public void store(String connection, String request, File newFile) {
		synchronized(valid) {
			if(valid != true) {
				//Throw exception
			}
				
			
			//hashMap.put(key, value)
			
		}
	}
	
	public File retrieve(String connection, String request) {
		synchronized(valid) {
			if(valid != true) {
				//Throw exception
			}
				
			//File.createTempFile(prefix, suffix)
			//hashMap.put(key, value)
			return null;
		}
	}
	
	public void clear() {
		synchronized(valid) {
			valid = false;
		}
		Set<Entry<String, File>> items = hashMap.entrySet();
		Iterator<Entry<String, File>> i = items.iterator();
		while(i.hasNext()) {
			Entry<String,File> entry = i.next();
			entry.getValue().delete();
		}
		hashMap.clear();
	}

}
