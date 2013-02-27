package net.dev123.yibo.service.task;

import net.dev123.exception.LibException;
import net.dev123.mblog.MicroBlog;
import net.dev123.mblog.entity.UnreadType;
import net.dev123.yibo.common.Constants;
import net.dev123.yibo.common.GlobalVars;
import net.dev123.yibo.db.LocalAccount;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class ResetUnreadCountTask extends AsyncTask<Void, Void, Boolean> {
    private static final String TAG = "ResetRemindCountTask";
	private Context context;
	private UnreadType type;
	private MicroBlog microBlog;
	public ResetUnreadCountTask(Context context, LocalAccount account, UnreadType type) {
		this.context = context;
		this.type = type;
		
		microBlog = GlobalVars.getMicroBlog(account);
	}
	
	@Override
	protected Boolean doInBackground(Void... params) {
		boolean isSuccess = false;
		if (microBlog == null || type == null) {
			return isSuccess;
		}
		
		try {
			isSuccess = microBlog.resetUnreadCount(type);
		} catch (LibException e) {
			if (Constants.DEBUG) e.printStackTrace();
		}
		
		return isSuccess;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
		
		if (result) {
			if (Constants.DEBUG) Toast.makeText(context, "reset remind successfully!", Toast.LENGTH_SHORT).show();
		}
		if(Constants.DEBUG) Log.v(TAG, "reset " + type + " remind count!" + result);
	}

	
}
