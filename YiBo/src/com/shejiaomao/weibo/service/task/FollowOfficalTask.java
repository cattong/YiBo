package com.shejiaomao.weibo.service.task;

import android.os.AsyncTask;
import android.util.Log;

import com.cattong.commons.LibException;
import com.cattong.commons.Logger;
import com.cattong.commons.ServiceProvider;
import com.cattong.entity.Relationship;
import com.cattong.entity.User;
import com.cattong.weibo.Weibo;
import com.shejiaomao.weibo.common.GlobalVars;
import com.shejiaomao.weibo.db.LocalAccount;

public class FollowOfficalTask extends AsyncTask<Void, Void, Void> {
	private static final String TAG = FollowOfficalTask.class.getSimpleName();

	private LocalAccount account;

	public FollowOfficalTask(LocalAccount account) {
		this.account = account;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected Void doInBackground(Void... params) {
		Weibo microBlog = GlobalVars.getMicroBlog(account);
		if (microBlog == null) {
			return null;
		}

		try {
			User offical = null;
			if (account.getServiceProvider() == ServiceProvider.Fanfou) {
				//饭否的官方帐号暂先特殊处理
				offical = microBlog.showUser(
					com.cattong.commons.Constants.FANFOU_OFFICAL_USER_ID);
			} else {
				offical = microBlog.showUserByDisplayName(
						account.getServiceProvider().getOfficalName());
			}

			Relationship relationship = microBlog.showRelationship(
				account.getUser().getUserId(), offical.getUserId());
			if (!relationship.isSourceFollowingTarget()) {
				microBlog.createFriendship(offical.getUserId());
			}
		} catch (LibException e) {
			if (Logger.isDebug()) {
				Log.e(TAG, e.getMessage(), e);
			}
		}
		return null;
	}

	@Override
	protected void onPostExecute(Void v) {
	}

}
