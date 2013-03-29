package com.shejiaomao.weibo.service.listener;

import com.cattong.commons.http.HttpRequestHelper;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;

import com.shejiaomao.maobo.R;
import com.shejiaomao.weibo.activity.AutoUpdateService;
import com.shejiaomao.weibo.common.CacheManager;
import com.shejiaomao.weibo.common.GlobalVars;
import com.shejiaomao.weibo.common.theme.Theme;
import com.shejiaomao.weibo.common.theme.ThemeEntry;

public class ThemeOperateClickListener implements OnClickListener {
	private ThemeEntry entry;
    
	public ThemeOperateClickListener() {		
	}
	
	public ThemeOperateClickListener(ThemeEntry entry) {
		this.entry = entry;
	}
	
	@Override
	public void onClick(View v) {
		if (entry == null) {
			return;
		}
		
        switch(entry.getState()) {
        case ThemeEntry.STATE_UNINSTALLED:
        	Uri uri = Uri.parse("market://search?q=pname:" + entry.getPackageName());
        	Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        	try {
        	    v.getContext().startActivity(intent); 
        	} catch(ActivityNotFoundException e) {
        		uri = Uri.parse(entry.getFileUrl());
                intent.setData(uri);
        	    v.getContext().startActivity(intent); 
        	}
        	break;
        case ThemeEntry.STATE_INSTALLED:
        	useTheme(v.getContext());
        	break;
        case ThemeEntry.STATE_USING:
        	break;
        }
	}

	private void useTheme(final Context context) {
		SharedPreferences preferences = context.getSharedPreferences(
            Theme.PREFS_NAME_THEME_SETTING, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(Theme.PREFS_KEY_PACKAGE_NAME, entry.getPackageName());
		editor.putInt(Theme.PREFS_KEY_RESOURCE_TYPE, Theme.RESOURCES_FROM_APK);
		editor.putString(Theme.PREFS_KEY_THEME_NAME, entry.getName());
		editor.commit();
		
		Dialog dialog =
			new AlertDialog.Builder(context)
			    .setTitle(R.string.title_dialog_alert)
				.setMessage("重启应用皮肤才能生效，确认要退出应用吗？")
				.setPositiveButton(R.string.btn_confirm, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog,	int which) {
						dialog.dismiss();
						exitApp(context);
					}
				})
				.setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				})
				.create();
		dialog.show();
	}
	
	private void exitApp(Context context) {

		Intent serviceIntent = new Intent(context, AutoUpdateService.class);
		context.stopService(serviceIntent);

		// 清除通知;
		NotificationManager notiManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		notiManager.cancelAll();

		CacheManager.getInstance().clear();

		GlobalVars.clear();

		HttpRequestHelper.shutdown();

		android.os.Process.killProcess(android.os.Process.myPid());
	}

	public ThemeEntry getEntry() {
		return entry;
	}

	public void setEntry(ThemeEntry entry) {
		this.entry = entry;
	}
}
