package net.dev123.yibo.service.task;

import java.util.Date;

import net.dev123.exception.LibException;
import net.dev123.yibo.R;
import net.dev123.yibo.common.Constants;
import net.dev123.yibome.YiBoMe;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class VerifyTimeTask extends AsyncTask<Void, Void, Date> {
	private static final String TAG = VerifyTimeTask.class.getSimpleName();

	private Context context;

	public VerifyTimeTask(Context context) {
		this.context = context;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected Date doInBackground(Void... params) {
		Date serverTime = null;
		try {
			serverTime = YiBoMe.getTimeNow();
		} catch (LibException e) {
			if (Constants.DEBUG) {
				Log.e(TAG, e.getMessage(), e);
			}
		}
		return serverTime;
	}

	@Override
	protected void onPostExecute(Date result) {
		if (result != null) {
			Date now = new Date();
			if (Math.abs(result.getTime() - now.getTime()) > 5 * 60 * 1000) {
				Toast.makeText(context, R.string.msg_accounts_add_time_inaccurate,
					Toast.LENGTH_LONG).show();
			}
		}
	}

}
