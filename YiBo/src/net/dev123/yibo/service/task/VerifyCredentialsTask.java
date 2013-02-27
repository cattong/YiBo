package net.dev123.yibo.service.task;

import net.dev123.exception.LibException;
import net.dev123.mblog.MicroBlog;
import net.dev123.mblog.entity.User;
import net.dev123.yibo.common.Constants;
import net.dev123.yibo.common.GlobalVars;
import net.dev123.yibo.common.ResourceBook;
import net.dev123.yibo.db.LocalAccount;
import net.dev123.yibo.db.LocalAccountDao;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class VerifyCredentialsTask extends AsyncTask<Void, Void, User> {
	private static final String TAG = "VerifyCredentialsTask";

    private Context context;
    private LocalAccount account;
	private MicroBlog microBlog;

	private String resultMsg = null;
	public VerifyCredentialsTask(Context context, LocalAccount account) {
		this.context = context;
		this.account = account;
		microBlog = GlobalVars.getMicroBlog(account);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected User doInBackground(Void... params) {
		if (microBlog == null) {
			return null;
		}
		Log.d(TAG, "start:" + microBlog.getAuthorization().toString() + account.toString());

		User user = null;
		try {
			user = microBlog.verifyCredentials();
		} catch (LibException e) {
			if (Constants.DEBUG) Log.e(TAG, "Task", e);
			resultMsg = ResourceBook.getStatusCodeValue(e.getExceptionCode(), context);
		}

		if (user != null) {
			account.setUser(user);
		    LocalAccountDao accountDao = new LocalAccountDao(context);
    	    accountDao.update(account);
		}
		return user;
	}

	@Override
	protected void onPostExecute(User result) {
        if (result == null) {
        	Toast.makeText(context, resultMsg, Toast.LENGTH_SHORT).show();
        } else {
        	account.setUser(result);

        	Log.d(TAG, "after:" + account.toString());
        }
	}

}
