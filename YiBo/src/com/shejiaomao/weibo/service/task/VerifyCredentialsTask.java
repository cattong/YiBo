package com.shejiaomao.weibo.service.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.cattong.commons.LibException;
import com.cattong.commons.Logger;
import com.cattong.entity.User;
import com.cattong.weibo.Weibo;
import com.shejiaomao.common.ResourceBook;
import com.shejiaomao.weibo.common.GlobalVars;
import com.shejiaomao.weibo.db.LocalAccount;
import com.shejiaomao.weibo.db.LocalAccountDao;

public class VerifyCredentialsTask extends AsyncTask<Void, Void, User> {
	private static final String TAG = "VerifyCredentialsTask";

    private Context context;
    private LocalAccount account;
	private Weibo microBlog;

	private String resultMsg = null;
	public VerifyCredentialsTask(Context context, LocalAccount account) {
		this.context = context;
		this.account = account;
		microBlog = GlobalVars.getMicroBlog(account);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected User doInBackground(Void... params) {
		if (microBlog == null) {
			return null;
		}
		Log.d(TAG, "start:" + microBlog.getAuthorization().toString() + account.toString());

		User user = null;
		try {
			user = microBlog.verifyCredentials();
		} catch (LibException e) {
			if (Logger.isDebug()) Log.e(TAG, "Task", e);
			resultMsg = ResourceBook.getResultCodeValue(e.getErrorCode(), context);
		}

		if (user != null) {
			account.setUser(user);
		    LocalAccountDao accountDao = new LocalAccountDao(context);
    	    accountDao.update(account);
		}
		return user;
	}

	@Override
	protected void onPostExecute(User result) {
        if (result == null) {
        	Toast.makeText(context, resultMsg, Toast.LENGTH_SHORT).show();
        } else {
        	account.setUser(result);

        	Log.d(TAG, "after:" + account.toString());
        }
	}

}
