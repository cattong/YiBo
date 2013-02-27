package net.dev123.yibo.service.task;

import net.dev123.yibo.common.Constants;
import net.dev123.yibo.service.cache.Cache;
import android.os.AsyncTask;
import android.util.Log;

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
		if (Constants.DEBUG) Log.v(TAG, "flush data!");
		return null;
	}

}
