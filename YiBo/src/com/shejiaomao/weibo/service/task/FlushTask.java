package com.shejiaomao.weibo.service.task;

import android.os.AsyncTask;
import android.util.Log;

import com.cattong.commons.Logger;
import com.shejiaomao.weibo.service.cache.Cache;

public class FlushTask extends AsyncTask<Void, Void, Void> {
	private static final String TAG = "FlushTask";
    int dice;

    private Cache cache;
    public FlushTask(Cache cache) {
    	this.cache = cache;
    }
	@Override
	protected void onPreExecute() {
		if (cache == null) {
		    cancel(true);
		}
	}
	
	@Override
	protected Void doInBackground(Void... params) {		
		if (cache == null) {
			return null;
		}
		
		cache.flush();
		if (Logger.isDebug()) Log.v(TAG, "flush data!");
		return null;
	}

}
