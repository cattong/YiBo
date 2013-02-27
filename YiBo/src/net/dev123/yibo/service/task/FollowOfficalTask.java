package net.dev123.yibo.service.task;

import net.dev123.commons.ServiceProvider;
import net.dev123.exception.LibException;
import net.dev123.mblog.MicroBlog;
import net.dev123.mblog.entity.Relationship;
import net.dev123.mblog.entity.User;
import net.dev123.yibo.common.Constants;
import net.dev123.yibo.common.GlobalVars;
import net.dev123.yibo.db.LocalAccount;
import android.os.AsyncTask;
import android.util.Log;

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
		MicroBlog microBlog = GlobalVars.getMicroBlog(account);
		if (microBlog == null) {
			return null;
		}

		try {
			User offical = null;
			if (account.getServiceProvider() == ServiceProvider.Fanfou) {
				//饭否的官方帐号暂先特殊处理
				offical = microBlog.showUser(
					net.dev123.commons.Constants.FANFOU_OFFICAL_USER_ID);
			} else {
				offical = microBlog.showUserByDisplayName(
						account.getServiceProvider().getOfficalName());
			}

			Relationship relationship = microBlog.showRelationship(
				account.getUser().getId(), offical.getId());
			if (!relationship.isFollowing()) {
				microBlog.createFriendship(offical.getId());
			}
		} catch (LibException e) {
			if (Constants.DEBUG) {
				Log.e(TAG, e.getMessage(), e);
			}
		}
		return null;
	}

	@Override
	protected void onPostExecute(Void v) {
	}

}
