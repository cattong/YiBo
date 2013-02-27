package net.dev123.yibo.service.task;

import net.dev123.commons.ServiceProvider;
import net.dev123.commons.util.StringUtil;
import net.dev123.exception.LibException;
import net.dev123.mblog.MicroBlog;
import net.dev123.mblog.entity.User;
import net.dev123.yibo.ProfileActivity;
import net.dev123.yibo.R;
import net.dev123.yibo.YiBoApplication;
import net.dev123.yibo.common.Constants;
import net.dev123.yibo.common.GlobalVars;
import net.dev123.yibo.common.ResourceBook;
import net.dev123.yibo.common.YiBoMeUtil;
import net.dev123.yibo.db.LocalAccount;
import net.dev123.yibo.service.listener.ProfileChangeListener;
import net.dev123.yibome.YiBoMe;
import net.dev123.yibome.entity.UserExtInfo;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class QueryUserTask extends AsyncTask<Void, Void, User> {
	private static final String TAG = "QueryUserTask";
	private Context context;
	private LocalAccount account;
	private MicroBlog microBlog;
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
		YiBoApplication yibo = (YiBoApplication)context.getApplicationContext();
		this.account = yibo.getCurrentAccount();
		this.microBlog = GlobalVars.getMicroBlog(yibo.getCurrentAccount());
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
			StringUtil.isEquals(user.getId(), user.getName())
			&& StringUtil.isEquals(user.getName(), user.getScreenName());
		try {
			if (isMentionLink) {
			    resultUser = microBlog.showUserByDisplayName(user.getDisplayName());
			} else {
				resultUser = microBlog.showUser(user.getId());
			}
			
			userExtInfo = queryUserExtInfo(resultUser);
		} catch (LibException e) {
			if (Constants.DEBUG) Log.e(TAG, "Task", e);
			resultMsg = ResourceBook.getStatusCodeValue(e.getExceptionCode(), context);
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
		
		YiBoMe yiboMe = YiBoMeUtil.getYiBoMeNullAuth();
		
		try {
			userExtInfo = yiboMe.getUserExtInfo(user.getId(), user.getServiceProvider());
		} catch (LibException e) {
			if (Constants.DEBUG) Log.e(TAG, "Task", e);
		}
		return userExtInfo;
	}
}
