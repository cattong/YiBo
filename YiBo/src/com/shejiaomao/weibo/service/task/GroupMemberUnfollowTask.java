package com.shejiaomao.weibo.service.task;

import com.shejiaomao.maobo.R;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.widget.Toast;

import com.cattong.commons.LibException;
import com.cattong.commons.Logger;
import com.cattong.entity.User;
import com.cattong.weibo.Weibo;
import com.shejiaomao.common.ResourceBook;
import com.shejiaomao.weibo.common.GlobalVars;
import com.shejiaomao.weibo.service.adapter.CacheAdapter;

public class GroupMemberUnfollowTask extends AsyncTask<Void, Void, User> {
	private static final String TAG = "GroupMemberUnfollowTask";
	
    private Context context;
    private CacheAdapter<User> adapter;
    private User user;
    private Weibo microBlog;
    
    private ProgressDialog progressDialog;
    private String resultMsg;
	public GroupMemberUnfollowTask(CacheAdapter<User> adapter, User user) {
		this.context = adapter.getContext();
		this.adapter = adapter;
		this.user = user;
		this.microBlog = GlobalVars.getMicroBlog(adapter.getAccount());
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		
		progressDialog = ProgressDialog.show(context, null, 
			context.getString(R.string.msg_personal_unfollowing));
		progressDialog.setCancelable(true);
		progressDialog.setOnCancelListener(onCancelListener);
	}

	@Override
	protected User doInBackground(Void... params) {
		if (user == null || microBlog == null) {
			return null;
		}
		
		User result = null;
		try {
			result = microBlog.destroyFriendship(user.getUserId());
		} catch (LibException e) {
			Logger.debug(TAG, e);
			resultMsg = ResourceBook.getResultCodeValue(e.getErrorCode(), context);
		}
		
		return result;
	}

	@Override
	protected void onPostExecute(User result) {
		super.onPostExecute(result);
		try {
			progressDialog.dismiss();
	    } catch(Exception e){}

		if (result == null) {
			Toast.makeText(context, resultMsg, Toast.LENGTH_SHORT);
			return;
		}
		
		boolean isSuccess = adapter.remove(user);
		if (isSuccess) {
			Toast.makeText(context, R.string.msg_personal_unfollow_success, Toast.LENGTH_SHORT);
		}
	}

	private OnCancelListener onCancelListener = new OnCancelListener() {
		public void onCancel(DialogInterface dialog) {
			GroupMemberUnfollowTask.this.cancel(true);
		}
	};
}
