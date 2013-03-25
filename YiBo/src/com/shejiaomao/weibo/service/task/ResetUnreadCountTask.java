package com.shejiaomao.weibo.service.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.cattong.commons.LibException;
import com.cattong.commons.Logger;
import com.cattong.weibo.Weibo;
import com.cattong.weibo.entity.UnreadType;
import com.shejiaomao.weibo.common.GlobalVars;
import com.shejiaomao.weibo.db.LocalAccount;

public class ResetUnreadCountTask extends AsyncTask<Void, Void, Boolean> {
    private static final String TAG = "ResetRemindCountTask";
	private Context context;
	private UnreadType type;
	private Weibo microBlog;
	public ResetUnreadCountTask(Context context, LocalAccount account, UnreadType type) {
		this.context = context;
		this.type = type;
		
		microBlog = GlobalVars.getMicroBlog(account);
	}
	
	@Override
	protected Boolean doInBackground(Void... params) {
		boolean isSuccess = false;
		if (microBlog == null || type == null) {
			return isSuccess;
		}
		
		try {
			isSuccess = microBlog.resetUnreadCount(type);
		} catch (LibException e) {
			if (Logger.isDebug()) e.printStackTrace();
		}
		
		return isSuccess;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
		
		if (result) {
			if (Logger.isDebug()) Toast.makeText(context, "reset remind successfully!", Toast.LENGTH_SHORT).show();
		}
		if(Logger.isDebug()) Log.v(TAG, "reset " + type + " remind count!" + result);
	}

	
}
