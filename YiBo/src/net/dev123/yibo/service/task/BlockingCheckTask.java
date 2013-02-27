package net.dev123.yibo.service.task;

import net.dev123.exception.LibException;
import net.dev123.mblog.MicroBlog;
import net.dev123.mblog.entity.User;
import net.dev123.yibo.ProfileActivity;
import net.dev123.yibo.R;
import net.dev123.yibo.YiBoApplication;
import net.dev123.yibo.common.Constants;
import net.dev123.yibo.common.GlobalVars;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Button;

public class BlockingCheckTask extends AsyncTask<Void, Void, Boolean> {
	private static final String TAG = "BlockingCheckTask";

	private MicroBlog microBlog;
	private YiBoApplication yibo;
	private ProfileActivity context;
	private User user;
	private boolean isBlocking;

	public BlockingCheckTask(ProfileActivity context) {
		this.context = context;
		this.user = context.getUser();
		this.yibo = (YiBoApplication) context.getApplication();
		this.microBlog = GlobalVars.getMicroBlog(yibo.getCurrentAccount());
	}

	@Override
	protected void onPreExecute() {
		Button btnBlock = (Button) context.findViewById(R.id.btnBlock);
		btnBlock.setEnabled(false);
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		if (microBlog == null) {
			return Boolean.FALSE;
		}

		try {
			isBlocking = microBlog.existsBlock(user.getId());
			user.setBlocking(isBlocking);
		} catch (LibException e) {
			if (Constants.DEBUG) {
				Log.e(TAG, "Task", e);
			}
		}

		return isBlocking;
	}

	@Override
	protected void onPostExecute(Boolean isBlocking) {

	}
}
