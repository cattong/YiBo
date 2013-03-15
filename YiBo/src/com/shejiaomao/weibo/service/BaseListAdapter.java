package com.shejiaomao.weibo.service;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;

import com.cattong.commons.Paging;

public abstract class BaseListAdapter<T> extends BaseAdapter {
	public static final int ITEM_VIEW_TYPE_DATA = 0;
	public static final int ITEM_VIEW_TYPE_REMOTE_DIVIDER = 1;
	public static final int ITEM_VIEW_TYPE_LOCAL_DIVIDER = 2;

	protected Context context;
	protected LayoutInflater inflater;

	protected List<T> dataList = null;
	protected Paging<T> paging  = null;

    public BaseListAdapter(Context context) {
		this.context = context;
		this.inflater = LayoutInflater.from(context);

		this.dataList = new ArrayList<T>();
		this.paging = new Paging<T>();
    }

	@Override
	public int getCount() {
		return dataList.size();
	}
	
	@Override
	public Object getItem(int position) {
		if (position < 0 || position > getCount() - 1) {
			return null;
		}
		
		Object obj = dataList.get(position);
		return obj;
	}
	
	@Override
	public long getItemId(int position) {
		return position;
	}
	
    public boolean addToFirst(List<T> list) {
    	if (list == null || list.size() == 0) {
			return false;
		}
    	
    	dataList.addAll(0, list);
    	this.notifyDataSetChanged();
    	
    	return true;
    }

    public boolean addToDivider(T value, List<T> list) {
    	if (list == null || list.size() == 0) {
			return false;
		}
    	
    	int i = dataList.indexOf(value);
    	if (i == -1) {
    		return false;
    	}
    	
    	dataList.addAll(i, list);
    	this.notifyDataSetChanged();
    	
    	return true;
    }

    public boolean addToLast(List<T> list) {
    	if (list == null || list.size() == 0) {
			return false;
		}
    	
    	dataList.addAll(list);
    	this.notifyDataSetChanged();
    	
    	return true;
    }

    public boolean remove(int position) {
    	if (position < 0 || position >= getCount()) {
			return false;
		}
    	
		dataList.remove(position);
		this.notifyDataSetChanged();

		return true;
    }

    public boolean remove(T t) {
    	if (t == null) {
    		return false;
    	}
    	
    	dataList.remove(t);
		this.notifyDataSetChanged();
		
    	return false;
    }

    public void clear() {
    	dataList.clear();
    	this.notifyDataSetChanged();
    }

	public Context getContext() {
		return context;
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

	public void setPaging(Paging<T> paging) {
		this.paging = (Paging<T>) paging;
	}

	public List<T> getDataList() {
		return dataList;
	}

	public void setDataList(List<T> dataList) {
		this.dataList = dataList;
	}

}
