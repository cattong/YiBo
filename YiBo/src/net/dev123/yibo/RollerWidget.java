package net.dev123.yibo;

import java.util.Date;

import net.dev123.commons.util.TimeSpanUtil;
import net.dev123.yibo.common.Constants;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

public class RollerWidget extends AppWidgetProvider {
	private static final String TAG = "RollerWidget";
	
	private static RollerWidgetWrap widgetWrap;
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		
		initDefaultWidget(context);
		
		Intent serviceIntent = new Intent(context, RollerWidgetService.class);
		context.startService(serviceIntent);
		if (Constants.DEBUG) Log.d(TAG, "onUpdate");
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		
		String action = intent.getAction();
		if (RollerWidgetWrap.ALART_ACTION.equals(action)) {
			if (widgetWrap == null) {
				widgetWrap = new RollerWidgetWrap();
				Intent serviceIntent = new Intent(context, RollerWidgetService.class);
				context.startService(serviceIntent);
			}
			widgetWrap.onReceive(context, intent);
		}
		
		if (Constants.DEBUG) Log.d(TAG, "onReceive");
	}

	@Override
	public void onDisabled(Context context) {
		super.onDisabled(context);
		if (Constants.DEBUG) Log.d(TAG, "onDisabled");
	}

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		super.onDeleted(context, appWidgetIds);
		
		Intent serviceIntent = new Intent(context, RollerWidgetService.class);
		context.stopService(serviceIntent);
		if (Constants.DEBUG) Log.d(TAG, "onDeleted");
	}

	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);
		if (Constants.DEBUG) Log.d(TAG, "onEnabled");
	}
	
	private void initDefaultWidget(Context context) {
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_roller);		
		remoteViews.setImageViewResource(R.id.ivProfilePicture, R.drawable.icon_header_default_min);
		remoteViews.setTextViewText(R.id.tvCreateAt, TimeSpanUtil.toTimeSpanString(new Date()));
		String page = "0/0";
		remoteViews.setTextViewText(R.id.tvPage, page);		
        
		ComponentName thisWidget = new ComponentName(context, RollerWidget.class);
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        manager.updateAppWidget(thisWidget, remoteViews);
	}
	
}
