package com.shejiaomao.weibo.service.cache.wrap;

public abstract class Wrap<T> {
	
	/** 命中数 **/
	private int hit;

	private boolean isLocalCached = false;
	
	public abstract T getWrap();
	
	public abstract void setWrap(T t);

	public int getHit() {
		return hit;
	}

	public void setHit(int hit) {
		this.hit = hit;
	}
    
	public void hit() {
		hit++;
	}
	
	public boolean isLocalCached() {
		return isLocalCached;
	}

	public void setLocalCached(boolean isLocalCached) {
		this.isLocalCached = isLocalCached;
	}
	
	
}
