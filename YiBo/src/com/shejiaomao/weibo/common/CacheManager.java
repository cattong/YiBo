package com.shejiaomao.weibo.common;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.shejiaomao.weibo.SheJiaoMaoApplication;
import com.shejiaomao.weibo.db.LocalAccount;
import com.shejiaomao.weibo.service.cache.AdapterCollectionCache;
import com.shejiaomao.weibo.service.cache.Cache;
import com.shejiaomao.weibo.service.cache.ImageCache;
import com.shejiaomao.weibo.service.cache.ReclaimLevel;


public class CacheManager {
	private Map<Object, Cache> cacheMap = null;

    private static volatile CacheManager instance  = new CacheManager();
	private static byte[] lock = new byte[0];

	private CacheManager() {
		cacheMap = new ConcurrentHashMap<Object, Cache>();
		//图片缓冲Cache;
		ImageCache imageCache = new ImageCache(SheJiaoMaoApplication.getSdcardCachePath(), 
			SheJiaoMaoApplication.getInnerCachePath());
		putCache(ImageCache.class, imageCache);
	}

	public static CacheManager getInstance() {
		if (instance == null) {
			synchronized (lock) {
				if (instance == null)
			        instance = new CacheManager();
			}
		}

		return instance;
	}

	public boolean containsKey(String key) {
		return cacheMap.containsKey(key);
	}

//	public void putCache(Class<?> clazz, String identifyID, Cache cache) {
//	    cacheMap.put(clazz.getName() + "_" + identifyID, cache);
//	}

	public void putCache(Class<?> clazz, Cache cache) {
		if (clazz == null || cache == null) {
			return;
		}
	    cacheMap.put(clazz.getName(), cache);
	}

	public void putCache(LocalAccount account, AdapterCollectionCache cache) {
	    cacheMap.put(account, cache);
	}

	public Cache getCache(Class<?> clazz) {
		Cache cache = getCache(clazz.getName());
		if (cache == null && clazz.equals(ImageCache.class)) {
			String sdcardCachePath = SheJiaoMaoApplication.getSdcardCachePath();
			ImageCache imageCache = new ImageCache(sdcardCachePath, SheJiaoMaoApplication.getInnerCachePath());
			cacheMap.put(ImageCache.class.getName(), imageCache);
		}

		return getCache(clazz.getName());
	}

	public Cache getCache(LocalAccount account) {
		if (account == null) {
			return null;
		}
		return cacheMap.get(account);
	}

//	public Cache getCache(Class<?> clazz, String identifyID) {
//		return getCache(clazz.getName() + "_" + identifyID);
//	}

	public Cache getCache(String key) {
		return cacheMap.get(key);
	}

	public void reclaim(ReclaimLevel level) {
		switch (level) {
		case LIGHT:
		case MODERATE:
			ImageCache imageCache = (ImageCache)getCache(ImageCache.class);
			if (imageCache != null) {
				imageCache.reclaim(level);
			}
			break;
		case WEIGHT:
			Set<Object> set = cacheMap.keySet();
			for (Object key : set) {
				Cache cache = cacheMap.get(key);
				cache.reclaim(level);
			}
			break;
		default:
			break;
		}
	}

	public void flush() {
		Set<Object> set = cacheMap.keySet();
		Cache cache = null;
		for (Object key : set) {
			cache = cacheMap.get(key);
			if (cache != null) {
				cache.flush();
			}
		}
	}

	public void clear() {
		flush();

		cacheMap.clear();
		instance = null;
	}
}
