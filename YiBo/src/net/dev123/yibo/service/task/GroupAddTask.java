package net.dev123.yibo.service.task;

import java.util.ArrayList;
import java.util.List;

import net.dev123.commons.util.StringUtil;
import net.dev123.exception.LibException;
import net.dev123.mblog.MicroBlog;
import net.dev123.mblog.entity.Group;
import net.dev123.yibo.R;
import net.dev123.yibo.common.Constants;
import net.dev123.yibo.common.GlobalVars;
import net.dev123.yibo.common.ResourceBook;
import net.dev123.yibo.db.GroupDao;
import net.dev123.yibo.db.LocalAccount;
import net.dev123.yibo.service.adapter.GroupListAdapter;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class GroupAddTask extends AsyncTask<Void, Void, Group> {
	private static final String TAG = "GroupMemberAddTask";

	private Context context;
    private GroupListAdapter adapter;
    private String groupName;

    private LocalAccount account;
    private MicroBlog microBlog;

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
			if (Constants.DEBUG) Log.e(TAG, "Task", e);
			resultMsg = ResourceBook.getStatusCodeValue(e.getExceptionCode(), context);
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
