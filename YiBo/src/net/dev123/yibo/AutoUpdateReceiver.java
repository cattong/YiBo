package net.dev123.yibo;

import java.util.List;

import net.dev123.commons.util.ListUtil;
import net.dev123.yibo.common.CacheManager;
import net.dev123.yibo.common.Constants;
import net.dev123.yibo.db.LocalAccount;
import net.dev123.yibo.service.cache.AdapterCollectionCache;
import net.dev123.yibo.service.cache.Cache;
import net.dev123.yibo.service.task.QueryRemindCountTask;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AutoUpdateReceiver extends BroadcastReceiver {
	private static final String TAG = "AutoUpdateReceiver";
	private YiBoApplication yibo;
    private List<LocalAccount> accountList;
    public AutoUpdateReceiver(List<LocalAccount> accountList) {
    	this.accountList = accountList;
    }
    
	@Override
	public void onReceive(Context context, Intent intent) {
		yibo = (YiBoApplication)context.getApplicationContext();
		if (!yibo.isUpdatesEnabled()) {
			return;
		}
		if (ListUtil.isEmpty(accountList)) {
			return;
		}
		
		for (LocalAccount account : accountList) {
			Cache cache = CacheManager.getInstance().getCache(account);			
			AdapterCollectionCache adapterCache = (AdapterCollectionCache)cache;
			if (adapterCache != null) {
			    QueryRemindCountTask remindCountTask = new QueryRemindCountTask(adapterCache);
			    remindCountTask.execute();
			}
		}
		if(Constants.DEBUG) Log.v(TAG, "auto update receiver");
	}

}
