package com.shejiaomao.weibo.service.task;

import com.shejiaomao.maobo.R;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.cattong.commons.LibException;
import com.cattong.commons.Logger;
import com.cattong.commons.ServiceProvider;
import com.cattong.commons.util.StringUtil;
import com.cattong.entity.User;
import com.cattong.entity.UserExtInfo;
import com.cattong.weibo.Weibo;
import com.shejiaomao.common.ResourceBook;
import com.shejiaomao.weibo.SheJiaoMaoApplication;
import com.shejiaomao.weibo.activity.ProfileActivity;
import com.shejiaomao.weibo.common.GlobalVars;
import com.shejiaomao.weibo.db.LocalAccount;
import com.shejiaomao.weibo.service.listener.ProfileChangeListener;

public class QueryUserTask extends AsyncTask<Void, Void, User> {
	private static final String TAG = "QueryUserTask";
	private Context context;
	private LocalAccount account;
	private Weibo microBlog;
	private ProfileChangeListener profileChangeListener;

	private User user;
    private UserExtInfo userExtInfo;
    
	private ProgressDialog dialog;
	private String resultMsg;
	public QueryUserTask(Context context, User user) {
		this(context, user, null);
	}

	public QueryUserTask(Context context, User user, ProfileChangeListener profileChangeListener) {
		this.context = context;
		this.user = user;
		this.profileChangeListener = profileChangeListener;
		SheJiaoMaoApplication sheJiaoMao = (SheJiaoMaoApplication)context.getApplicationContext();
		this.account = sheJiaoMao.getCurrentAccount();
		this.microBlog = GlobalVars.getMicroBlog(sheJiaoMao.getCurrentAccount());
	}

	@Override
	protected void onPreExecute() {
		dialog = ProgressDialog.show(context, "", context.getString(R.string.msg_personal_loading));
		dialog.setCancelable(true);
	}

	@Override
	protected User doInBackground(Void... params) {
		if (user == null || microBlog == null) {
			return null;
		}

		User resultUser = null;
		//是否来自@DisplayName的链接点击
		boolean isMentionLink = 
			StringUtil.isEquals(user.getUserId(), user.getName())
			&& StringUtil.isEquals(user.getName(), user.getScreenName());
		try {
			if (isMentionLink) {
			    resultUser = microBlog.showUserByDisplayName(user.getDisplayName());
			} else {
				resultUser = microBlog.showUser(user.getUserId());
			}
			
			userExtInfo = queryUserExtInfo(resultUser);
		} catch (LibException e) {
			if (Logger.isDebug()) Log.e(TAG, "Task", e);
			resultMsg = ResourceBook.getResultCodeValue(e.getErrorCode(), context);
		}

		return resultUser;
	}

	protected void onPostExecute(User resultUser) {
		if (dialog.isShowing() && dialog.getContext() != null	) {
			try {
			    dialog.dismiss();
			} catch(Exception e){}
		}

		if (resultUser != null) {
			if (context instanceof ProfileActivity) {
				ProfileActivity profileActivity = (ProfileActivity)context;
				profileActivity.setUser(resultUser);
				profileActivity.setVerifyInfo(userExtInfo);
			} else if (profileChangeListener != null) {
				profileChangeListener.updateContentView(resultUser);
				account.setUser(resultUser);
			}
		} else if(resultMsg != null) {
			Toast.makeText(context, resultMsg, Toast.LENGTH_LONG).show();
		}
	};

	private UserExtInfo queryUserExtInfo(User user) {
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
//			Logger.error(TAG, e);
//		}
		
		return userExtInfo;
	}
}
