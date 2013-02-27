package net.dev123.yibo;

import net.dev123.yibo.common.Constants;
import net.dev123.yibo.common.GlobalVars;
import net.dev123.yibo.common.NetType;
import net.dev123.yibo.common.NetUtil;
import net.dev123.yibo.common.NetUtil.NetworkOperator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ConnectionChangeReceiver extends BroadcastReceiver {

	private static final String TAG = "ConnectionChangeReceiver";

	private YiBoApplication yibo;

	@Override
	public void onReceive(Context context, Intent intent) {

		yibo = (YiBoApplication) context.getApplicationContext();

		NetType type = NetUtil.getCurrentNetType(context);

        //网络变化，需要更新的缓冲
		GlobalVars.NET_OPERATOR = NetUtil.getNetworkOperator(context);
		GlobalVars.NET_TYPE = type;
		if (type == NetType.WIFI) {
			GlobalVars.NET_OPERATOR = NetworkOperator.UNKOWN;
		}
		GlobalVars.IS_SHOW_THUMBNAIL = yibo.isShowThumbnail();
		GlobalVars.IS_AUTO_LOAD_COMMENTS = yibo.isAutoLoadComments();
		
		if (type != NetType.NONE) {
			NetUtil.updateNetworkConfig(context);
		}

		if (Constants.DEBUG) {
			Log.d(TAG, "Network switch to " + type);
		}
	}

}
