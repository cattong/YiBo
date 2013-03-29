package com.shejiaomao.weibo;

import java.io.File;
import java.util.List;
import java.util.Locale;

import com.shejiaomao.maobo.R;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.cattong.commons.Logger;
import com.cattong.commons.http.HttpRequestHelper;
import com.cattong.commons.util.StringUtil;
import com.cattong.commons.util.TimeSpanUtil;
import com.shejiaomao.common.CompatibilityUtil;
import com.shejiaomao.common.ImageQuality;
import com.shejiaomao.common.NetType;
import com.shejiaomao.common.NetUtil;
import com.shejiaomao.common.NetUtil.NetworkOperator;
import com.shejiaomao.weibo.common.Constants;
import com.shejiaomao.weibo.common.EmotionLoader;
import com.shejiaomao.weibo.common.GlobalResource;
import com.shejiaomao.weibo.common.GlobalVars;
import com.shejiaomao.weibo.db.LocalAccount;
import com.umeng.analytics.MobclickAgent;

public class SheJiaoMaoApplication extends Application {

	private static final String TAG = SheJiaoMaoApplication.class.getSimpleName();

	private static String innerCachePath;        //手机内部存储缓存(由于手机内部存储有限，考虑废弃)
	private static String sdcardCachePath;       //sdcard外部存储缓存;
	
	private static int smallAvatarSize;
	private static int normalAvatarSize;
	private static int displayWidth;
	private static int displayHeight;
	private static int densityDpi;
	private static float density;

	private SharedPreferences prefs = null;
	private LocalAccount currentAccount = null;

	public static int getDisplayWidth() {
		return displayWidth;
	}

	public static int getDisplayHeight() {
		return displayHeight;
	}

	public static int getDensityDpi() {
		return densityDpi;
	}
	
	public static float getDensity() {
		return density;
	}

	public static String getSdcardCachePath() {
		return sdcardCachePath;
	}
	
	public static String getInnerCachePath() {
		return innerCachePath;
	}

	public static int getSmallAvatarSize() {
		return smallAvatarSize;
	}

	public static int getNormalAvatarSize() {
		return normalAvatarSize;
	}

	public static Locale changeLocale(Context context) {
		Locale locale = Locale.getDefault();
		if (context == null) {
			return locale;
		}
		
		SharedPreferences prefs = context.getSharedPreferences(
			Constants.PREFS_NAME_APP_SETTING, MODE_PRIVATE);
		String value = prefs.getString(Constants.PREFS_KEY_LOCALE, "auto");
		if (!value.equals("auto")) {		
			String[] values = value.split("_");
            if (values.length == 2) {
				locale = new Locale(values[0], values[1]);
			} else {
				locale = new Locale(values[0]);
			}
		}
		
		Configuration config = new Configuration();
		Resources res = context.getResources();
		DisplayMetrics dm = res.getDisplayMetrics();
	    config.locale = locale;
	    res.updateConfiguration(config, dm);
	    
	    return locale;
	}
	
	public int getFontSize() {
		return Integer.parseInt(prefs.getString(Constants.PREFS_KEY_FONT_SIZE, "17"));
	}

	/**
	 * @return 是否在列表中显示用户头像
	 */
	public boolean isShowHead() {
		return prefs.getBoolean(Constants.PREFS_KEY_SHOW_HEAD, true);
	}

	/**
	 * @return 是否在列表中显示图片缩略图
	 */
	public boolean isShowThumbnail() {
		String policy = prefs.getString(Constants.PREFS_KEY_SHOW_THUMBNAIL, "2");

		return NetUtil.isPolicyPositive(Integer.parseInt(policy));
	}

	public int getUpdateCount() {
		String updateCount = prefs.getString(Constants.PREFS_KEY_UPDATE_COUNT, "" + Constants.PAGING_DEFAULT_COUNT);

		return Integer.valueOf(updateCount);
	}

	/**
	 * @return 自动更新间隔
	 */
	public int getUpdateInterval() {
		return Integer.valueOf(
			prefs.getString(
			    Constants.PREFS_KEY_UPDATE_INTERVAL,
			    String.valueOf(Constants.DEFAULT_UPDATE_INTERVAL)
			)
		);
	}

	public boolean isAutoLoadMore() {
		return prefs.getBoolean(Constants.PREFS_KEY_AUTO_LOAD_MORE, true);
	}

	/**
	 * @return 是否自动标注地理位置
	 */
	public boolean isAutoLocate() {
		return prefs.getBoolean(Constants.PREFS_KEY_AUTO_LOCATE, false);
	}

	/**
	 * @return 是否允许在首次进入帐号时刷新
	 */
	public boolean isRefreshOnFirstEnter() {
		return prefs.getBoolean(Constants.PREFS_KEY_REFRESH_ON_FIRST_ENTER, true);
	}

	/**
	 * @return 是否允许晃动刷新
	 */
	public boolean isRefreshOnShake() {
		return prefs.getBoolean(Constants.PREFS_KEY_REFRESH_ON_SHAKE, false);
	}

	/**
	 * @return 是否启用手势操作
	 */
	public boolean isGestureEnabled() {
		return prefs.getBoolean(Constants.PREFS_KEY_ENABLE_GESTURE, true);
	}

	/**
	 * @return 是否默认同步到所有帐号
	 */
	public boolean isSyncToAllAsDefault() {
		return prefs.getBoolean(Constants.PREFS_KEY_SYNC_TO_ALL, false);
	}

	/**
	 * @return 是否启用列表滑块
	 */
	public boolean isSliderEnabled() {
		return prefs.getBoolean(Constants.PREFS_KEY_USE_SLIDER, true);
	}

	/**
	 * @return 是否自动旋转屏幕
	 */
	public boolean isAutoScreenOrientation() {
		return prefs.getBoolean(Constants.PREFS_KEY_AUTO_SCREEN_ORIENTATION, true);
	}

	/**
	 * @return 是否返回时退出
	 */
	public boolean isKeyBackExit(){
		return prefs.getBoolean(Constants.PREFS_KEY_EXIT_ON_BACK, false);
	}

	/**
	 * @return 是否启用自动更新
	 */
	public boolean isUpdatesEnabled() {
		return prefs.getBoolean(Constants.PREFS_KEY_ENABLE_UPDATES, true);
	}

	/**
	 * @return 是否震动提醒
	 */
	public boolean isVibrateNotification() {
		return prefs.getBoolean(Constants.PREFS_KEY_VIBRATE, true);
	}

	/**
	 * @return 是否铃声提醒
	 */
	public boolean isRingtoneNotification() {
		return prefs.getBoolean(Constants.PREFS_KEY_RINGTONE, true);
	}

	/**
	 * @return 是否闪烁LED灯提醒
	 */
	public boolean isFlashingLEDNotification() {
		return prefs.getBoolean(Constants.PREFS_KEY_LED, true);
	}

	/**
	 * @return 是否检查微博更新
	 */
	public boolean isCheckStatuses() {
		return isUpdatesEnabled() && prefs.getBoolean(Constants.PREFS_KEY_CHECK_STATUSES, true);
	}

	/**
	 * @return 是否检查“提到我的”更新
	 */
	public boolean isCheckMentions() {
		return isUpdatesEnabled() && prefs.getBoolean(Constants.PREFS_KEY_CHECK_MENTIONS, true);
	}

	/**
	 * @return 是否检查评论更新
	 */
	public boolean isCheckComments() {
		return isUpdatesEnabled() && prefs.getBoolean(Constants.PREFS_KEY_CHECK_COMMENTS, true);
	}

	/**
	 * @return 是否检查私信更新
	 */
	public boolean isCheckDirectMesages() {
		return isUpdatesEnabled() && prefs.getBoolean(Constants.PREFS_KEY_CHECK_MESSAGES, true);
	}

	/**
	 * @return 是否检查关注者更新
	 */
	public boolean isCheckFollowers() {
		return isUpdatesEnabled() && prefs.getBoolean(Constants.PREFS_KEY_CHECK_FOLLOWERS, true);
	}

	/**
	 * @return 是否显示提醒栏图标
	 */
	public boolean isShowStatusIcon() {
		return prefs.getBoolean(Constants.PREFS_KEY_SHOW_STATUS_ICON, true);
	}

	/**
	 * @return 自定义的提醒铃声
	 */
	public String getRingtoneUri() {
		return prefs.getString(Constants.PREFS_KEY_RINGTONE_URI, null);
	}

	/**
	 * @return 自定义的图片存储位置
	 */
	public String getImageFolder() {
		return prefs.getString(Constants.PREFS_KEY_IMAGE_FOLDER, Constants.DCIM_PATH);
	}

	/**
	 * @return 上传图片质量
	 */
	public ImageQuality getImageUploadQuality() {
		String qualityString =  prefs.getString(Constants.PREFS_KEY_IMAGE_UPLOAD_QUALITY, ImageQuality.Middle.toString());
		return ImageQuality.valueOf(qualityString);
	}

	public ImageQuality getImageDownloadQuality() {
		String qualityString =  prefs.getString(Constants.PREFS_KEY_IMAGE_DOWNLOAD_QUALITY, ImageQuality.Middle.toString());
		return ImageQuality.valueOf(qualityString);
	}

	//获取缓存策略，默认为缓存5天内的数据
	public int getCacheStrategy() {
		return Integer.valueOf(prefs.getString(Constants.PREFS_KEY_CACHE_STRATEGY, "5"));
	}

	public boolean isCheckNewVersionOnStartup() {
		return prefs.getBoolean(Constants.PREFS_KEY_VERSION_CHECK_ON_STARTUP, true);
	}

	public boolean isDetectImageInfo() {
		return prefs.getBoolean(Constants.PREFS_KEY_DETECT_IMAGE_INFO, true);
	}

	public boolean isAutoLoadComments() {
		String policy = "2";
		try {
		    policy = prefs.getString(Constants.PREFS_KEY_AUTO_LOAD_COMMENTS, "2");
		} catch (Exception e) {
			Editor editor = prefs.edit();
			editor.putString(Constants.PREFS_KEY_AUTO_LOAD_COMMENTS, "2");
			editor.commit();
		}
		
		if (!StringUtil.isNumeric(policy)) {
			policy = "2";
		}
		
		return NetUtil.isPolicyPositive(Integer.parseInt(policy));
	}

	/**
	 * 获取当前帐号
	 *
	 * @return 当前帐号
	 */
	public LocalAccount getCurrentAccount() {
		if (Logger.isDebug()) {
			Log.d(TAG, "Get Current Account : " + currentAccount);
		}
		return currentAccount;
	}

	/**
	 * 设置当前帐号
	 *
	 * @param currentAccount 当前帐号
	 */
	public void setCurrentAccount(LocalAccount currentAccount) {
		if (Logger.isDebug()) {
			Log.d(TAG, "Set Current Account : " + currentAccount);
		}
		this.currentAccount = currentAccount;
	}

    private AlarmManager alarmManager;
    private static final String CONNECTIONS_EVICT_ACTION = "com.shejiaomao.weibo.CONNECTIONS_EVICT";

    @Override
	public void onCreate() {
		prefs = getSharedPreferences(Constants.PREFS_NAME_APP_SETTING, MODE_PRIVATE);

		NetUtil.updateNetworkConfig(this);
		initPrefs();
		
		this.registerReceiver(globalReceiver, new IntentFilter(CONNECTIONS_EVICT_ACTION));
		Intent updateIntent = new Intent(CONNECTIONS_EVICT_ACTION);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, updateIntent, 0);
        alarmManager = (AlarmManager)this.getSystemService(ALARM_SERVICE);
		alarmManager.setRepeating(
			AlarmManager.RTC, System.currentTimeMillis() + 1000,
			Constants.CONNECTION_EVICT_INTERVAL, pi
		);

		//m9时，设置成返回退出
		if (!prefs.contains(Constants.PREFS_KEY_EXIT_ON_BACK)) {
			String model = CompatibilityUtil.getModel();
			if (StringUtil.isNotEmpty(model)
				&& model.toLowerCase().indexOf("m9") > -1) {
				SharedPreferences.Editor editor = prefs.edit();
				editor.putBoolean(Constants.PREFS_KEY_EXIT_ON_BACK, true);
				editor.commit();
			}
		}
		
		initAvatarSize();
		initLocalization(this);
		initGlobalVars();
		initCurrentAccount();
		initCachePath();
		
		// 初始化表情
		EmotionLoader.init(this);

		super.onCreate();
	}

	private void initPrefs() {

	}
	
    private void initGlobalVars() {
    	if (Logger.isDebug()) {
    		Log.d(TAG, "initGlobalVars Start : " + System.currentTimeMillis() / 1000);
    	}
        
    	GlobalVars.LOCALE = changeLocale(this);
    	GlobalVars.NET_OPERATOR = NetUtil.getNetworkOperator(this);
    	GlobalVars.NET_TYPE = NetUtil.getCurrentNetType(this);
		GlobalVars.IS_SHOW_HEAD = isShowHead();
		GlobalVars.IS_SHOW_THUMBNAIL = isShowThumbnail();
		GlobalVars.UPDATE_COUNT = getUpdateCount();
		GlobalVars.IMAGE_DOWNLOAD_QUALITY = getImageDownloadQuality();
		GlobalVars.IS_ENABLE_GESTURE = isGestureEnabled();
		GlobalVars.FONT_SIZE_HOME_BLOG = getFontSize();
		GlobalVars.FONT_SIZE_HOME_RETWEET = getFontSize();
		GlobalVars.IS_DETECT_IAMGE_INFO = isDetectImageInfo();
		GlobalVars.IS_AUTO_LOAD_COMMENTS = isAutoLoadComments();
	    
		GlobalVars.reloadAccounts(this);

		if (GlobalVars.NET_TYPE == NetType.WIFI) {
			GlobalVars.NET_OPERATOR = NetworkOperator.UNKOWN;
		}

    	String isObeySinaAgreement = MobclickAgent.getConfigParams(this, "IS_OBEY_SINA_AGREEMENT");
    	String isMobileNetUpdateVersion = MobclickAgent.getConfigParams(this, "IS_MOBILE_NET_UPDATE_VERSION");
        GlobalVars.IS_OBEY_SINA_AGREEMENT = Boolean.parseBoolean(isObeySinaAgreement);
        GlobalVars.IS_MOBILE_NET_UPDATE_VERSION = Boolean.parseBoolean(isMobileNetUpdateVersion);
        if (Logger.isDebug()) Log.d(TAG, "IS_OBEY_SINA_AGREEMENT=" + isObeySinaAgreement);

		if (Logger.isDebug()) {
    		Log.d(TAG, "initGlobalVars Finish : " + System.currentTimeMillis() / 1000);
    	}
    }

    private void initCurrentAccount() {
    	List<LocalAccount> accounts = GlobalVars.getAccountList(this, false);
    	for (LocalAccount account : accounts) {
			if (account.isDefault()) {
				currentAccount = account;
			}
		}
    	if (currentAccount == null && accounts.size() > 0) {
    		currentAccount = accounts.get(0);
    	}
    }

    public static void initLocalization(Context context) {
    	if (context == null) {
    		return;
    	}
		//时间表示;
		TimeSpanUtil.timeFormatWithinSeconds = " " +  context.getString(R.string.label_time_format_within_seconds);
		TimeSpanUtil.timeFormatHalfMinuteAgo = " " + context.getString(R.string.label_time_format_half_minute_ago);
		TimeSpanUtil.timeFormatWithinOneMinute = " " + context.getString(R.string.label_time_format_within_one_minute);
		TimeSpanUtil.timeFormatOneMinuteAgo = " " + context.getString(R.string.label_time_format_one_minute_ago);
		TimeSpanUtil.timeFormatMinutesAgo = " " + context.getString(R.string.label_time_format_minutes_ago);
		TimeSpanUtil.timeFormatToday = " " + context.getString(R.string.label_time_format_today);
		TimeSpanUtil.timeFormatOneHourAgo = " " + context.getString(R.string.label_time_format_one_hour_ago);
		TimeSpanUtil.timeFormatHoursAgo = " " + context.getString(R.string.label_time_format_hours_ago);
		TimeSpanUtil.timeFormatOneDayAgo = " " + context.getString(R.string.label_time_format_one_day_ago);
		TimeSpanUtil.timeFormatDaysAgo = " " + context.getString(R.string.label_time_format_days_ago);
		TimeSpanUtil.timeFormatWeeksAgo = " " + context.getString(R.string.label_time_format_weeks_ago);
	    
		GlobalResource.clearResource();
    }

    private void initAvatarSize() {
    	if (Logger.isDebug()) {
    		Log.d(TAG, "initAvatarSize Start : " + System.currentTimeMillis() / 1000);
    	}
    	WindowManager windowManager = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
		Display display = windowManager.getDefaultDisplay();
		displayWidth = display.getWidth();
	    displayHeight = display.getHeight();
	    //使用display.getOrientation() 判断横竖屏不准确
		if (displayWidth > displayHeight) {
		    displayWidth = display.getHeight();
		    displayHeight = display.getWidth();
		}
		DisplayMetrics metrics = new DisplayMetrics();
		display.getMetrics(metrics);
		density = metrics.density;
		densityDpi = metrics.densityDpi;
		if (densityDpi <= DisplayMetrics.DENSITY_LOW) {
			smallAvatarSize = Constants.IMAGE_HEAD_MINI_SIZE_LDPI;
			normalAvatarSize = Constants.IMAGE_HEAD_NORMAL_SIZE_LDPI;
		} else if (densityDpi <= DisplayMetrics.DENSITY_MEDIUM) {
			smallAvatarSize = Constants.IMAGE_HEAD_MINI_SIZE_MDPI;
			normalAvatarSize = Constants.IMAGE_HEAD_NORMAL_SIZE_MDPI;
		} else if (densityDpi <= DisplayMetrics.DENSITY_HIGH
			&& displayWidth <= Constants.DISPLAY_HDPI_WIDTH) {
			smallAvatarSize = Constants.IMAGE_HEAD_MINI_SIZE_HDPI;
			normalAvatarSize = Constants.IMAGE_HEAD_NORMAL_SIZE_HDPI;
		} else {
			smallAvatarSize = Constants.IMAGE_HEAD_MINI_SIZE_XHDPI;
			normalAvatarSize = Constants.IMAGE_HEAD_NORMAL_SIZE_XHDPI;
		}
		if (Logger.isDebug()) {
			Log.v("Display Width: ", " " + displayWidth);
			Log.v("Display Height: ", " " + displayHeight);
			Log.v("Display Density: ", " " + densityDpi);
			Log.d(TAG, "initAvatarSize Finish : " + System.currentTimeMillis() / 1000);
		}
    }

    private void initCachePath() {
    	innerCachePath = getCacheDir().getAbsolutePath();
		File sdcardPath = android.os.Environment.getExternalStorageDirectory();
		sdcardCachePath = sdcardPath.getAbsolutePath() + File.separator + ".yibo";
    }
    
	@Override
	public void onTerminate() {
		HttpRequestHelper.shutdown();

		Intent updateIntent = new Intent(CONNECTIONS_EVICT_ACTION);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, updateIntent, 0);
        if (alarmManager != null) {
        	alarmManager.cancel(pi);
        }
		super.onTerminate();
	}

	private BroadcastReceiver globalReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			HttpRequestHelper.evictConnections();
			if (Logger.isDebug()) Log.v("globalReceiver", "connection evict!");
		}
	};

}
