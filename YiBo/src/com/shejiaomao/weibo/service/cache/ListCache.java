package com.shejiaomao.weibo.service.cache;

import java.util.List;

import com.cattong.commons.Paging;

public interface ListCache<T, W> extends Cache {
	
	public T get(int i); 
	
	public void add(int i, T value); 
	
	public void add(T value);
	
	public void addAll(int i, List<T> value); 
	
	public void addAll(List<T> value);
	
	public void remove(int i); 
	
	public void remove(T value);
	
	/**读取本地缓存**/
	public List<T> read(Paging<W> page);
	
	/**写入数据库**/
	public void write(T value);
	
	public int indexOf(T value);
	
	public int size();
}
