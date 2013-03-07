package com.shejiaomao.weibo.service.cache;

public interface Cache {

	/** 数据缓冲到本地 **/
	public void flush();

	/** 适当地减少内存，压缩队列，回收已读或过期的数据 **/
	public boolean reclaim(ReclaimLevel level);

	/** 清空数据 **/
	public void clear();
}
