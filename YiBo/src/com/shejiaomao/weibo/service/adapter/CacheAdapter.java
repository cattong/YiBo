package com.shejiaomao.weibo.service.adapter;

import java.util.List;

import com.cattong.commons.Paging;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;

import com.shejiaomao.weibo.db.LocalAccount;
import com.shejiaomao.weibo.service.cache.ReclaimLevel;

public abstract class CacheAdapter<T> extends BaseAdapter {
	public static final int ITEM_VIEW_TYPE_DATA = 0;
	public static final int ITEM_VIEW_TYPE_REMOTE_DIVIDER = 1;
	public static final int ITEM_VIEW_TYPE_LOCAL_DIVIDER = 2;

	protected Context context;
	protected LocalAccount account;
	protected LayoutInflater inflater;

	protected Paging<T> paging  = null;

    public CacheAdapter(Context context, LocalAccount account) {
		this.context = context;
		this.account = account;
		this.inflater = LayoutInflater.from(context);

		paging = new Paging<T>();
    }

    public abstract boolean addCacheToFirst(List<T> list);

    /**
     * 加到分隔里，若value为空，直接加到最后.
     * @param value
     * @param list
     * @return
     */
    public abstract boolean addCacheToDivider(T value, List<T> list);

    public abstract boolean addCacheToLast(List<T> list);

    public boolean remove(int position) {
    	return false;
    }

    public boolean remove(T t) {
    	return false;
    }

    public abstract T getMax();

    public abstract T getMin();

    public boolean refresh() {
    	return false;
    }

    public abstract void clear();

    public void reclaim(ReclaimLevel level) {}

	public Context getContext() {
		return context;
	}

	public LocalAccount getAccount() {
		return account;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public LayoutInflater getInflater() {
		return inflater;
	}
	public void setInflater(LayoutInflater inflater) {
		this.inflater = inflater;
	}

	public Paging<T> getPaging() {
		return paging;
	}

	public void setPaging(Paging<? extends T> paging) {
		this.paging = (Paging<T>) paging;
	}

}
