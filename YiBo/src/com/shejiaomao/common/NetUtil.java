package com.shejiaomao.common;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.cattong.commons.Logger;
import com.cattong.commons.http.HttpRequestHelper;
import com.cattong.commons.util.StringUtil;
import com.shejiaomao.weibo.common.Constants;
import com.shejiaomao.weibo.common.GlobalVars;

public class NetUtil {

	private static final String TAG = NetUtil.class.getSimpleName();

	/** 查询所有当前可用的APN的CONTENT_URI */
	// private static final Uri CURRENT_APNS_CONTENT_URI = Uri.parse("content://telephony/carriers/current");
	/** 查询所有当前正在使用的APN的CONTENT_URI */
	private static final Uri PREFER_APN_CONTENT_URI = Uri.parse("content://telephony/carriers/preferapn");

	private static boolean isNETWAP = false;

	public enum NetworkOperator {
		CHINA_MOBILE,
		CHINA_UNION,
		CHINA_NET,
		UNKOWN,
		NONE,
	}

	/**
	 * 检测网络是否连接（注：需要在配置文件即AndroidManifest.xml加入权限）
	 *
	 * @param context
	 * @return true : 网络连接成功
	 * @return false : 网络连接失败
	 **/
	public static boolean isConnect(Context context) {
		NetworkInfo info = getCurrentActiveNetworkInfo(context);

		return info != null && info.getState() == NetworkInfo.State.CONNECTED;
	}

	public static void getAllNetWorkInfo(Context context) {
		ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connManager != null) {
			NetworkInfo[] allNetworkInfo = connManager.getAllNetworkInfo();
			if (Logger.level <= Logger.DEBUG) {
				for (int i = 0; i < allNetworkInfo.length; i++) {
					Log.d(TAG, allNetworkInfo[i].toString());
				}
			}
		}
	}

	public static NetType getCurrentNetType(Context context) {
		NetType type = NetType.NONE;

		// 获取当前活动的网络
		NetworkInfo info = getCurrentActiveNetworkInfo(context);
		if (info == null) {
			return type;
		}

		// 判断当前网络是否已经连接
		if (info.getState() == NetworkInfo.State.CONNECTED) {
			if (info.getType() == ConnectivityManager.TYPE_WIFI) {
				type = NetType.WIFI;
			} else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
				String subTypeName = info.getSubtypeName().toUpperCase();

				if (subTypeName.indexOf("GPRS") > -1) {
					type = NetType.MOBILE_GPRS;
				} else if (subTypeName.indexOf("EDGE") > -1) {
					type = NetType.MOBILE_EDGE;
				} else {
					type = NetType.MOBILE_3G;
				}
			} else {
				type = NetType.UNKNOW;
			}
		} else if (info.getState() == NetworkInfo.State.CONNECTING) {
			type = NetType.UNKNOW;
			System.out.println("connecting " + info.getType());
		}

		return type;
	}

	private static NetworkInfo getCurrentActiveNetworkInfo(Context context) {
		NetworkInfo networkInfo = null;
		// 获取手机所有连接管理对象（包括对wi-fi,net,gsm,cdma等连接的管理）
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		if (connectivityManager != null) {
			networkInfo = connectivityManager.getActiveNetworkInfo();
			Logger.debug("Current Active Network :{}", networkInfo);
		}

		return networkInfo;
	}

	/**
	 * 更新网络配置，主要是获取代理配置更新
	 *
	 * @param context
	 */
	public static void updateNetworkConfig(Context context) {
		boolean isNetWap = false;
		NetType type = getCurrentNetType(context);
		if (type == NetType.NONE) {
			return;
		}

		String proxyHost = null;
		int proxyPort = 0;
		String proxyUser = null;
		String proxyPassword = null;

		NetworkInfo networkInfo = getCurrentActiveNetworkInfo(context);
		if (networkInfo == null) {
			return;
		}
		
		if (type == NetType.MOBILE_EDGE 
			|| type == NetType.MOBILE_GPRS
			|| type == NetType.MOBILE_3G) {

			String apnName = networkInfo.getExtraInfo();
			Logger.debug("extraInfo:{}", apnName);

			// 获取当前移动网络下，APN接入点名称，查询并获取接入点代理配置信息
			if (StringUtil.isNotEmpty(apnName) && !(Build.VERSION.SDK_INT >= 17)) {

				String[] projection = {"apn", "proxy", "port", "user", "password"};
				Cursor cursor = context.getContentResolver().query(PREFER_APN_CONTENT_URI, 
					projection, null, null, null);
				if (cursor != null && cursor.moveToFirst()) {
					int apnIndex = cursor.getColumnIndex("apn");
					int proxyIndex = cursor.getColumnIndex("proxy");
					int portIndex = cursor.getColumnIndex("port");
					int userIndex = cursor.getColumnIndex("user");
					int passwordIndex = cursor.getColumnIndex("password");
					while (!cursor.isAfterLast()) {
						if (apnName.equals(cursor.getString(apnIndex))) {
							proxyHost = cursor.getString(proxyIndex);
							proxyPort = cursor.getInt(portIndex);
							proxyUser = cursor.getString(userIndex);
							proxyPassword = cursor.getString(passwordIndex);
                            
							//移动联通wap（代理相同：10.0.0.172：80），电信wap（代理：10.0.0.200：80）
							isNetWap = "10.0.0.172".equals(proxyHost) || "10.0.0.200".equals(proxyHost);
						}

						cursor.moveToNext();
					}
				}
			}
		}

		if (isNETWAP ^ isNetWap) {
		   HttpRequestHelper.setGlobalProxy(proxyHost, proxyPort, proxyUser, proxyPassword);
		   isNETWAP = isNetWap;
		}
		if (Logger.isDebug()) {
			Toast.makeText(context,
				"Network switch to " + type + " , CMWAP = " + isNETWAP + ", Proxy = " + proxyHost,
				Toast.LENGTH_SHORT
			).show();
		}
	}

	public static NetworkOperator getNetworkOperator(Context context) {
		NetworkOperator operator = NetworkOperator.NONE;
		String imsi = getIMSI(context);
		if (imsi != null) {
		    if (imsi.startsWith("46000") || imsi.startsWith("46002")) {
		    	//因为移动网络编号46000下的IMSI已经用完，所以虚拟了一个46002编号，
		    	//134/159号段使用了此编号
		        //中国移动
		    	operator = NetworkOperator.CHINA_MOBILE;
		    } else if (imsi.startsWith("46001")) {
		        //中国联通
		    	operator = NetworkOperator.CHINA_UNION;
		    } else if (imsi.startsWith("46003")) {
		        //中国电信
		    	operator = NetworkOperator.CHINA_NET;
		    } else {
		    	operator = NetworkOperator.UNKOWN;
		    }
		} else {
			operator = NetworkOperator.NONE;
		}

		return operator;
	}
	public static String getIMSI(Context context) {
		TelephonyManager telManager = (TelephonyManager)context.getSystemService(
			Context.TELEPHONY_SERVICE);
		String imsi = telManager.getSubscriberId();

	    return imsi;
	}

	public static boolean isNETWAP() {
		return isNETWAP;
	}
	
	/*
	 * 判断设置策略是否是肯定的
	 */
	public static boolean isPolicyPositive(int policy) {
		boolean isShow = false;
		if (policy == Constants.SETTING_POLICY_NO) {
			isShow = false;
		} else if (policy == Constants.SETTING_POLICY_YES) {
			isShow = true;
		} else if (policy == Constants.SETTING_POLICY_ADAPTIVE) {
			if (GlobalVars.NET_TYPE == NetType.MOBILE_3G || GlobalVars.NET_TYPE == NetType.WIFI) {
				isShow = true;
			}
		}
		return isShow;
	}
}
