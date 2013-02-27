package net.dev123.yibo.service.task;

import net.dev123.commons.util.StringUtil;
import net.dev123.exception.LibException;
import net.dev123.mblog.MicroBlog;
import net.dev123.mblog.entity.Group;
import net.dev123.yibo.R;
import net.dev123.yibo.common.Constants;
import net.dev123.yibo.common.GlobalVars;
import net.dev123.yibo.common.ResourceBook;
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

public class GroupDeleteTask extends AsyncTask<Void, Void, Group> {
	private static final String TAG = "GroupMemberAddTask";

	private Context context;
    private GroupListAdapter adapter;
    private String groupId;

    private LocalAccount account;
    private MicroBlog microBlog;

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
			if (Constants.DEBUG) Log.e(TAG, "Task", e);
			resultMsg = ResourceBook.getStatusCodeValue(e.getExceptionCode(), context);
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
