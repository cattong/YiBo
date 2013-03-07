package com.shejiaomao.weibo.service.task;

import android.content.Context;
import android.os.AsyncTask;

import com.cattong.commons.ServiceProvider;
import com.cattong.commons.util.StringUtil;
import com.cattong.entity.User;
import com.cattong.entity.UserExtInfo;
import com.shejiaomao.weibo.activity.ProfileActivity;

public class QueryUserExtInfoTask extends AsyncTask<Void, Void, UserExtInfo> {
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
		
		if (StringUtil.isNotEmpty(user.getVerifyInfo())) {
			userExtInfo = new UserExtInfo();
			userExtInfo.setServiceProvider(user.getServiceProvider());
			userExtInfo.setUserId(user.getUserId());
			userExtInfo.setVerifyInfo(user.getVerifyInfo());
			return userExtInfo;
		}
		
//		SocialCat socialCat = Util.getSocialCat(context);
//		
//		try {
//			userExtInfo = socialCat.getUserExtInfo(user.getServiceProvider(), user.getUserId());
//		} catch (LibException e) {
//			String resultMsg = ResourceBook.getResultCodeValue(e.getErrorCode(), context);
//			Logger.debug(resultMsg, e);
//		}

		return userExtInfo;
	}

	protected void onPostExecute(UserExtInfo userExtInfo) {
		if (context instanceof ProfileActivity) {
			((ProfileActivity)context).setVerifyInfo(userExtInfo);
		}
	}
}
