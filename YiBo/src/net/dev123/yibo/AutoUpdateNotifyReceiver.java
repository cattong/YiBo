package net.dev123.yibo;

import net.dev123.commons.util.StringUtil;
import net.dev123.yibo.common.Constants;
import net.dev123.yibo.common.NotificationEntity;
import net.dev123.yibo.db.LocalAccount;
import net.dev123.yibo.service.task.SocialGraphTask;
import net.dev123.yibo.widget.Skeleton;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

public class AutoUpdateNotifyReceiver extends BroadcastReceiver {
	private static final String TAG = "AutoUpdateNotifyReceiver";
    private YiBoApplication yibo;
	private LocalAccount account;
    private NotificationEntity entity;
	@Override
	public void onReceive(Context context, Intent intent) {
        if (intent == null) {
        	return;
        }
        yibo = (YiBoApplication)context.getApplicationContext();

        Bundle bundle = intent.getExtras();
        account = (LocalAccount)bundle.getSerializable("ACCOUNT");
        entity = (NotificationEntity)bundle.getSerializable("NOTIFICATION_ENTITY");

        noticeNewBlog(context);
        if(Constants.DEBUG) Log.v(TAG, entity.toString());
	}

	private void noticeNewBlog(Context context) {
		NotificationManager notiManager = (NotificationManager)
		    context.getSystemService(Context.NOTIFICATION_SERVICE);
		notiManager.cancel(Constants.NOTIFICATION_NEW_MICRO_BLOG);//先清除上一次提醒;

		Intent intent = new Intent();
		//粉丝
		if (entity.getContentType() == Skeleton.TYPE_MORE) {
			intent.setAction("net.dev123.yibo.SOCIAL_GRAPH");
		    intent.addCategory("android.intent.category.DEFAULT");
            intent.putExtra("SOCIAL_GRAPH_TYPE", SocialGraphTask.TYPE_FOLLOWERS);
            intent.putExtra("USER", account.getUser());
		} else {
		    intent.setAction("net.dev123.yibo.MAIN");
		    intent.addCategory("android.intent.category.DEFAULT");
		    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		}
		intent.putExtra("CONTENT_TYPE", entity.getContentType());
		intent.putExtra("ACCOUNT", account);

	    Notification notification = new Notification();
	    notification.icon = R.drawable.icon_notification;
	    notification.flags = Notification.FLAG_AUTO_CANCEL;
	    notification.tickerText = entity.getTickerText();

	    if (yibo.isVibrateNotification()) {
	    	notification.defaults |= Notification.DEFAULT_VIBRATE;
	    }
        if (yibo.isRingtoneNotification()) {
        	if (StringUtil.isNotEmpty(yibo.getRingtoneUri())) {
            	notification.sound = Uri.parse(yibo.getRingtoneUri());
            } else {
               	notification.defaults |= Notification.DEFAULT_SOUND;
            }
        }

        if (yibo.isFlashingLEDNotification()) {
        	notification.ledARGB = Color.GREEN;
        	notification.ledOffMS = 1000;
        	notification.ledOnMS = 1000;
        	notification.flags |= Notification.FLAG_SHOW_LIGHTS;
        }

	    int requestCode = account.getAccountId().intValue() * 100 + entity.getContentType();
	    PendingIntent pendingIntent = PendingIntent.getActivity(
	    	context,  requestCode,
	    	intent,   PendingIntent.FLAG_UPDATE_CURRENT
	    );

	    notification.setLatestEventInfo(
	    	context, entity.getContentTitle(),
	    	entity.getContentText(), pendingIntent
	    );

	    notiManager.notify(requestCode, notification);
	}
}
