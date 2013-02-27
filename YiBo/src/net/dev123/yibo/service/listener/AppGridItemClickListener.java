package net.dev123.yibo.service.listener;

import net.dev123.commons.util.StringUtil;
import net.dev123.yibo.HotStatusesActivity;
import net.dev123.yibo.HotTopicsActivity;
import net.dev123.yibo.PublicTimelineActivity;
import net.dev123.yibo.R;
import net.dev123.yibo.SearchActivity;
import net.dev123.yibo.StatusSubscribeActivity;
import net.dev123.yibo.common.Constants;
import net.dev123.yibo.db.ConfigSystemDao;
import net.dev123.yibome.conf.YiBoMeApiConfig;
import net.dev123.yibome.conf.YiBoMeApiConfigImpl;
import net.dev123.yibome.entity.SubscribeCatalog;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.waps.AppConnect;

public class AppGridItemClickListener implements OnItemClickListener {
    private static String URL_CARTOON;
    
    static {
    	YiBoMeApiConfig yiboMeApiConfig = new YiBoMeApiConfigImpl();
    	URL_CARTOON = String.format(yiboMeApiConfig.getUrlRedirectServiceURL(), "cartoon", "YiBo.Android");
    }
    
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Adapter adapter = parent.getAdapter();
        long appImageId = adapter.getItemId(position);

        Activity context = (Activity)parent.getContext();
        Intent intent = new Intent();
        if (appImageId == R.drawable.icon_app_search) {
        	intent.setClass(context, SearchActivity.class);
        } else if (appImageId == R.drawable.icon_app_public_timeline) {
        	intent.setClass(context, PublicTimelineActivity.class);
        } else if (appImageId == R.drawable.icon_app_hot_topic) {
        	intent.setClass(context, HotTopicsActivity.class);
        } else if (appImageId == R.drawable.icon_app_hot_retweet) {
        	intent.setClass(context, HotStatusesActivity.class);
        	intent.putExtra("TYPE", HotStatusesActivity.HOT_RETWEET);
        } else if (appImageId == R.drawable.icon_app_hot_comment) {
        	intent.setClass(context, HotStatusesActivity.class); 
        	intent.putExtra("TYPE", HotStatusesActivity.HOT_COMMENT);
        } else if (appImageId == R.drawable.icon_app_daily) {
        	intent.setClass(context, StatusSubscribeActivity.class);
        	intent.putExtra("SUBSCRIBE_CATALOG", SubscribeCatalog.DAILY_NEWS.getSubscribeCatalogNo());
        	intent.putExtra("TITLE_ID", R.string.label_app_daily);
        } else if (appImageId == R.drawable.icon_app_image) {
        	intent.setClass(context, StatusSubscribeActivity.class);
        	intent.putExtra("SUBSCRIBE_CATALOG", SubscribeCatalog.IMAGE.getSubscribeCatalogNo());
        	intent.putExtra("TITLE_ID", R.string.label_app_image);
        } else if (appImageId == R.drawable.icon_app_jokes) {
        	intent.setClass(context, StatusSubscribeActivity.class);
        	intent.putExtra("SUBSCRIBE_CATALOG", SubscribeCatalog.JOKE.getSubscribeCatalogNo());
        	intent.putExtra("TITLE_ID", R.string.label_app_jokes);
        } else if (appImageId == R.drawable.icon_app_exchange) {
        	ConfigSystemDao configDao = new ConfigSystemDao(context);
		    String username = configDao.getString(Constants.PASSPORT_USERNAME);
		    if (StringUtil.isEmpty(username)) {
      	          AppConnect.getInstance(context).showOffers(context);
      	    } else {
      		  AppConnect.getInstance(context).showOffers(context, username);
            }
      	    return;
        } else {
        	Toast.makeText(context, "抱歉，此功能正在开发中..", Toast.LENGTH_LONG).show();
        	return;
        }

        context.startActivity(intent);
	}

}
