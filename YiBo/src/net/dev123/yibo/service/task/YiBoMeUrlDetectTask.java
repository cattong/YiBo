package net.dev123.yibo.service.task;

import net.dev123.yibo.common.Constants;
import net.dev123.yibo.widget.YiBoUrlSpan;
import net.dev123.yibome.YiBoMeImpl;
import android.os.AsyncTask;
import android.util.Log;

/**
 * @author Weiping Ye
 * @version 创建时间：2011-10-19 下午3:23:50
 **/
public class YiBoMeUrlDetectTask extends AsyncTask<Void, Void, Boolean> {
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if (Constants.DEBUG) Log.v("YiBoMeUrlDetectTask", "execute YiBoMeUrlDetectTask");
	}
	
	@Override
	protected Boolean doInBackground(Void... params) {
		return YiBoMeImpl.detectUrlServer();
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
		if (YiBoUrlSpan.isUseYiBoMeUrl() != result) {
			YiBoUrlSpan.setUseYiBoMeUrl(result);
		}
	}
}
