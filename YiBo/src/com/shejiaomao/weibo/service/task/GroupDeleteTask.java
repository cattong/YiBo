package com.shejiaomao.weibo.service.task;

import com.shejiaomao.maobo.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.cattong.commons.LibException;
import com.cattong.commons.Logger;
import com.cattong.commons.util.StringUtil;
import com.cattong.weibo.Weibo;
import com.cattong.weibo.entity.Group;
import com.shejiaomao.common.ResourceBook;
import com.shejiaomao.weibo.common.GlobalVars;
import com.shejiaomao.weibo.db.LocalAccount;
import com.shejiaomao.weibo.service.adapter.GroupListAdapter;

public class GroupDeleteTask extends AsyncTask<Void, Void, Group> {
	private static final String TAG = "GroupMemberAddTask";

	private Context context;
    private GroupListAdapter adapter;
    private String groupId;

    private LocalAccount account;
    private Weibo microBlog;

    private ProgressDialog dialog;
    private String resultMsg;
	public GroupDeleteTask(GroupListAdapter adapter, String groupId) {
		this.adapter = adapter;
		this.groupId = groupId;
		this.context = adapter.getContext();
		this.account = adapter.getAccount();
		this.microBlog = GlobalVars.getMicroBlog(account);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();

		dialog = ProgressDialog.show(context, null, 
			context.getString(R.string.msg_group_delete));
	    dialog.setCancelable(true);
	    dialog.setOnCancelListener(onCancelListener);
	    dialog.setOwnerActivity((Activity)context);
	}

	@Override
	protected Group doInBackground(Void... params) {
		if (microBlog == null) {
			return null;
		}

		Group deletedGroup = null;
		try {
		    deletedGroup = microBlog.destroyGroup(groupId);
		} catch (LibException e) {
			if (Logger.isDebug()) Log.e(TAG, "Task", e);
			resultMsg = ResourceBook.getResultCodeValue(e.getErrorCode(), context);
		}

		return deletedGroup;
	}

	@Override
	protected void onPostExecute(Group result) {
		super.onPostExecute(result);
		try {
		     dialog.dismiss();
		} catch (Exception e) {}

		if (result != null) {           
			adapter.remove(result);
			
			resultMsg = context.getString(R.string.msg_group_delete_success);
		}
        
		if (StringUtil.isNotEmpty(resultMsg)) {
		    Toast.makeText(context, resultMsg, Toast.LENGTH_LONG).show();
		}
	}

	private OnCancelListener onCancelListener = new OnCancelListener() {
		public void onCancel(DialogInterface dialog) {
			GroupDeleteTask.this.cancel(true);
		}
	};
}
