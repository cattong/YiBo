package net.dev123.yibo.service.task;

import net.dev123.commons.ServiceProvider;
import net.dev123.exception.LibException;
import net.dev123.mblog.entity.User;
import net.dev123.yibo.ProfileActivity;
import net.dev123.yibo.common.Constants;
import net.dev123.yibo.common.YiBoMeUtil;
import net.dev123.yibome.YiBoMe;
import net.dev123.yibome.entity.UserExtInfo;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class QueryUserExtInfoTask extends AsyncTask<Void, Void, UserExtInfo> {
	private static final String TAG = "QueryUserExtInfoTask";
	private Context context;

	private User user;
	public QueryUserExtInfoTask(Context context, User user) {
		this.context = context;
		this.user = user;
	}

	@Override
	protected void onPreExecute() {
	}

	@Override
	protected UserExtInfo doInBackground(Void... params) {
		UserExtInfo userExtInfo = null;
		if (user == null
			|| !user.isVerified()
		    || user.getServiceProvider() == ServiceProvider.Fanfou
		    || user.getServiceProvider() == ServiceProvider.Twitter) {
			return userExtInfo;
		}
		
		YiBoMe yiboMe = YiBoMeUtil.getYiBoMeNullAuth();
		
		try {
			userExtInfo = yiboMe.getUserExtInfo(user.getId(), user.getServiceProvider());
		} catch (LibException e) {
			if (Constants.DEBUG) Log.e(TAG, "Task", e);
			//String resultMsg = ResourceBook.getStatusCodeValue(e.getExceptionCode(), context);
		}

		return userExtInfo;
	}

	protected void onPostExecute(UserExtInfo userExtInfo) {
		if (context instanceof ProfileActivity) {
			((ProfileActivity)context).setVerifyInfo(userExtInfo);
		}
	}
}
