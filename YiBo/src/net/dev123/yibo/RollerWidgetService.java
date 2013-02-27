package net.dev123.yibo;

import net.dev123.yibo.common.Constants;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class RollerWidgetService extends Service {
    private static final String TAG = "RollerWidgetService";    
    private static final int PAGE_TIME_INTERVAL = 10 * 1000;
    
	@Override
	public void onCreate() {
		super.onCreate();
		
        Intent updateIntent = new Intent(RollerWidgetWrap.ALART_ACTION); 
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, updateIntent, 0);  
        AlarmManager am = (AlarmManager) getSystemService(Activity.ALARM_SERVICE);
        //定时设置  
        am.setRepeating(AlarmManager.RTC, System.currentTimeMillis() + 1000, PAGE_TIME_INTERVAL, pi);
	
        if (Constants.DEBUG) Log.d(TAG, "widget service onCreate.");
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);        

        if (Constants.DEBUG) Log.d(TAG, "widget service onStart.");
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		
        Intent updateIntent = new Intent(RollerWidgetWrap.ALART_ACTION);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, updateIntent, 0);  
        AlarmManager am = (AlarmManager) getSystemService(Activity.ALARM_SERVICE);
        am.cancel(pi);
	}

}
