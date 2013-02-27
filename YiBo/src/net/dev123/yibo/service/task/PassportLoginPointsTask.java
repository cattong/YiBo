package net.dev123.yibo.service.task;

import net.dev123.exception.LibException;
import net.dev123.yibo.R;
import net.dev123.yibo.common.Constants;
import net.dev123.yibo.common.YiBoMeUtil;
import net.dev123.yibo.db.ConfigSystemDao;
import net.dev123.yibome.YiBoMe;
import net.dev123.yibome.entity.Passport;
import net.dev123.yibome.entity.PointOrderInfo;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class PassportLoginPointsTask extends AsyncTask<Void, Void, PointOrderInfo> {
	private static final String TAG = PassportLoginPointsTask.class.getSimpleName();

	private Context context;
	public PassportLoginPointsTask(Context context) {
		this.context = context;
	}

	@Override
	protected void onPreExecute() {
	}

	@Override
	protected PointOrderInfo doInBackground(Void... arg) {
		PointOrderInfo orderInfo = null;
		
		ConfigSystemDao configDao = new ConfigSystemDao(context);
		Passport passport = configDao.getPassport();
		if (passport == null) {
			return orderInfo;
		}
		
		try {
			YiBoMe yiboMe = YiBoMeUtil.getYiBoMeOAuth(context);
			if (yiboMe != null) {
                orderInfo = yiboMe.addLoginPoints();
			}
		} catch (LibException e) {
			if (Constants.DEBUG) {
				Log.d(TAG, e.getMessage());
			}
		}
		return orderInfo;
	}

	protected void onPostExecute(PointOrderInfo result) {
		if (result != null) {
			Toast.makeText(context, R.string.msg_passport_login_points, Toast.LENGTH_LONG).show();
		}
	}

}
