package com.shejiaomao.weibo.service.task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.os.AsyncTask;

import com.cattong.entity.ConfigApp;
import com.shejiaomao.weibo.db.ConfigAppDao;

public class ConfigAppSyncTask extends AsyncTask<Void, Void, Boolean> {
	private static final String TAG = ConfigAppSyncTask.class.getSimpleName();

	private Activity context;

	public ConfigAppSyncTask(Activity context) {
		this.context = context;
	}

	@Override
	protected void onPreExecute() {

	}

	@Override
	protected Boolean doInBackground(Void... arg) {
		boolean isSuccess = false;
		ConfigAppDao configAppDao = new ConfigAppDao(context);
		List<ConfigApp> localApps = configAppDao.findAll();
		Map<String, ConfigApp> appMap = new HashMap<String, ConfigApp>();
		if (localApps != null) {
			for (ConfigApp app : localApps) {
				appMap.put(app.getAppKey() + app.getServiceProviderNo(), app);
			}
		}
		
//		SocialCat socialCat = Util.getSocialCat(context);
//		if (socialCat == null) {
//			return isSuccess;
//		}
		
//		try {
//			List<ConfigApp> remoteApps = socialCat.getMyConfigApps();
//			ConfigApp tempApp = null;
//			String mapKey = null;
//			// 更新和新增AppKey
//			for (ConfigApp app : remoteApps) {
//				mapKey = app.getAppKey() + app.getServiceProviderNo();
//				tempApp = appMap.get(mapKey);
//				if (tempApp != null) {
//					tempApp.setAppName(app.getAppName());
//					tempApp.setAppSecret(app.getAppSecret());
//					tempApp.setAuthFlow(app.getAuthFlow());
//					tempApp.setAuthVersion(app.getAuthVersion());
//					configAppDao.update(tempApp);
//					appMap.remove(mapKey);
//				} else {
//					configAppDao.save(app);
//				}
//			}
//			// 删除剩余的AppKey
//			for (Map.Entry<String, ConfigApp> entry : appMap.entrySet()) {
//				configAppDao.delete(entry.getValue());
//			}
//		} catch (LibException e) {
//			if (Constants.DEBUG) {
//				Log.d(TAG, e.getMessage());
//			}
//		}
		
		return isSuccess;
	}

	protected void onPostExecute(Boolean result) {

	}

}
