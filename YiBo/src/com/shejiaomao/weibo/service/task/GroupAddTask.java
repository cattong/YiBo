package com.shejiaomao.weibo.service.task;

import java.util.ArrayList;
import java.util.List;

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
import com.shejiaomao.weibo.db.GroupDao;
import com.shejiaomao.weibo.db.LocalAccount;
import com.shejiaomao.weibo.service.adapter.GroupListAdapter;

public class GroupAddTask extends AsyncTask<Void, Void, Group> {
	private static final String TAG = "GroupMemberAddTask";

	private Context context;
    private GroupListAdapter adapter;
    private String groupName;

    private LocalAccount account;
    private Weibo microBlog;

    private ProgressDialog dialog;
    private String resultMsg;
	public GroupAddTask(GroupListAdapter adapter, String groupName) {
		this.adapter = adapter;
		this.groupName = groupName;
		this.context = adapter.getContext();
		this.account = adapter.getAccount();
		this.microBlog = GlobalVars.getMicroBlog(account);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();

		dialog = ProgressDialog.show(context, null, 
			context.getString(R.string.msg_group_add));
	    dialog.setCancelable(true);
	    dialog.setOnCancelListener(onCancelListener);
	    dialog.setOwnerActivity((Activity)context);
	}

	@Override
	protected Group doInBackground(Void... params) {
		if (microBlog == null) {
			return null;
		}

		Group newGroup = null;
        boolean isPublicList = false;
		try {
		    newGroup = microBlog.createGroup(groupName, isPublicList, "");
		} catch (LibException e) {
			if (Logger.isDebug()) Log.e(TAG, "Task", e);
			resultMsg = ResourceBook.getResultCodeValue(e.getErrorCode(), context);
		}


		return newGroup;
	}

	@Override
	protected void onPostExecute(Group result) {
		super.onPostExecute(result);
		try {
		     dialog.dismiss();
		} catch (Exception e) {}

		if (result != null) {           
			GroupDao dao = new GroupDao(context);
	        dao.save(account, result);
	        List<Group> groupList = new ArrayList<Group>();
	        groupList.add(result);
			adapter.addCacheToLast(groupList);
			
			resultMsg = context.getString(R.string.msg_group_add_success);
		}
        
		if (StringUtil.isNotEmpty(resultMsg)) {
		    Toast.makeText(context, resultMsg, Toast.LENGTH_LONG).show();
		}
	}

	private OnCancelListener onCancelListener = new OnCancelListener() {
		public void onCancel(DialogInterface dialog) {
			GroupAddTask.this.cancel(true);
		}
	};
}
