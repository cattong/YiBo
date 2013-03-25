package com.shejiaomao.weibo.activity;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.cattong.commons.Logger;
import com.cattong.commons.util.ListUtil;
import com.shejiaomao.weibo.SheJiaoMaoApplication;
import com.shejiaomao.weibo.common.CacheManager;
import com.shejiaomao.weibo.db.LocalAccount;
import com.shejiaomao.weibo.service.cache.AdapterCollectionCache;
import com.shejiaomao.weibo.service.cache.Cache;
import com.shejiaomao.weibo.service.task.QueryRemindCountTask;

public class AutoUpdateReceiver extends BroadcastReceiver {
	private static final String TAG = "AutoUpdateReceiver";
	private SheJiaoMaoApplication sheJiaoMao;
    private List<LocalAccount> accountList;
    public AutoUpdateReceiver(List<LocalAccount> accountList) {
    	this.accountList = accountList;
    }
    
	@Override
	public void onReceive(Context context, Intent intent) {
		sheJiaoMao = (SheJiaoMaoApplication)context.getApplicationContext();
		if (!sheJiaoMao.isUpdatesEnabled()) {
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
		if(Logger.isDebug()) Log.v(TAG, "auto update receiver");
	}

}
