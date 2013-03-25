package com.shejiaomao.weibo.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.cattong.commons.Logger;
import com.shejiaomao.common.NetType;
import com.shejiaomao.common.NetUtil;
import com.shejiaomao.common.NetUtil.NetworkOperator;
import com.shejiaomao.weibo.SheJiaoMaoApplication;
import com.shejiaomao.weibo.common.GlobalVars;

public class ConnectionChangeReceiver extends BroadcastReceiver {

	private static final String TAG = "ConnectionChangeReceiver";

	private SheJiaoMaoApplication sheJiaoMao;

	@Override
	public void onReceive(Context context, Intent intent) {

		sheJiaoMao = (SheJiaoMaoApplication) context.getApplicationContext();

		NetType type = NetUtil.getCurrentNetType(context);

        //网络变化，需要更新的缓冲
		GlobalVars.NET_OPERATOR = NetUtil.getNetworkOperator(context);
		GlobalVars.NET_TYPE = type;
		if (type == NetType.WIFI) {
			GlobalVars.NET_OPERATOR = NetworkOperator.UNKOWN;
		}
		GlobalVars.IS_SHOW_THUMBNAIL = sheJiaoMao.isShowThumbnail();
		GlobalVars.IS_AUTO_LOAD_COMMENTS = sheJiaoMao.isAutoLoadComments();
		
		if (type != NetType.NONE) {
			NetUtil.updateNetworkConfig(context);
		}

		if (Logger.isDebug()) {
			Log.d(TAG, "Network switch to " + type);
		}
	}

}
