package net.dev123.yibo.widget;

import net.dev123.yibo.YiBoApplication;
import net.dev123.yibo.common.Constants;
import net.dev123.yibome.conf.YiBoMeApiConfigImpl;
import android.net.Uri;
import android.text.style.URLSpan;
import android.util.Log;

/**
 * @author Weiping Ye
 * @version 创建时间：2011-10-19 下午3:21:16
 **/
public class YiBoUrlSpan extends URLSpan {
	private static final String TAG = "YiBoUrlSpan";
	private static String serviceUrl = null;
	private static boolean isUseYiBoMeUrl = true;

	public YiBoUrlSpan(String url) {
		super(url);
	}

	@Override
	public String getURL() {
		String target = super.getURL();
		if (isUseYiBoMeUrl
	        && target.indexOf("http") != -1) {
			String yiboQuery = "target=" + Uri.encode(target);
        	if (serviceUrl == null) {
	    		YiBoMeApiConfigImpl conf = new YiBoMeApiConfigImpl();
	    		serviceUrl = conf.getUrlServiceURL();
	    	}
			String yiboUrl = serviceUrl + "?" + yiboQuery
        		+ "&displayWith=" + YiBoApplication.getDisplayWidth()
        		+ "&displayHeight=" + YiBoApplication.getDisplayHeight()
        		+ "&density=" + YiBoApplication.getDensity();
			if (Constants.DEBUG) {
        		Log.d(TAG, yiboUrl);
        	}
			return yiboUrl;
		}
		return target;
	}
//    @Override
//    public void onClick(View widget) {
//        Uri uri = Uri.parse(getURL());
//        if (isUseYiBoMeUrl
//        	&& uri.getScheme().equalsIgnoreCase("http")) {
//        	if (serviceUrl == null) {
//        		YiBoMeApiConfigImpl conf = new YiBoMeApiConfigImpl();
//        		serviceUrl = conf.getUrlServerURL();
//        	}
//	        String yiboQuery = "target=" + Uri.encode(uri.toString());
//        	String yiboUrl = serviceUrl + "?" + yiboQuery
//        		+ "&displayWith=" + YiBoApplication.getDisplayWidth()
//        		+ "&displayHeight=" + YiBoApplication.getDisplayHeight();
//        	if (Constants.DEBUG) {
//        		Log.d(TAG, yiboUrl);
//        	}
//        	uri = Uri.parse(yiboUrl);
//        }
//        Context context = widget.getContext();
//        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//        intent.putExtra(Browser.EXTRA_APPLICATION_ID, context.getPackageName());
//        context.startActivity(intent);
//    }
    
	public static boolean isUseYiBoMeUrl() {
		return isUseYiBoMeUrl;
	}

	public static void setUseYiBoMeUrl(boolean isUseYiBoMeUrl) {
		YiBoUrlSpan.isUseYiBoMeUrl = isUseYiBoMeUrl;
	}
}
