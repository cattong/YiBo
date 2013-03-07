package com.shejiaomao.weibo.service.cache;

public interface MapCache<K, V> extends Cache {

	public boolean containsKey(K key);
	
	public V get(K key); 
	
	public void put(K key, V value); 
	
	public void remove(K key); 
	
	public V read(K key);
	
	public void write(K key, V value);
}
