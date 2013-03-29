package com.shejiaomao.weibo.service.listener;

import com.shejiaomao.maobo.R;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.cattong.entity.StatusCatalog;
import com.shejiaomao.weibo.activity.HotStatusesActivity;
import com.shejiaomao.weibo.activity.PublicTimelineActivity;
import com.shejiaomao.weibo.activity.SearchActivity;
import com.shejiaomao.weibo.activity.StatusSubscribeActivity;
import com.shejiaomao.weibo.common.Constants;
import com.shejiaomao.weibo.db.ConfigSystemDao;

public class AppGridItemClickListener implements OnItemClickListener {
    
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
        } else if (appImageId == R.drawable.icon_app_hot_retweet) {
        	intent.setClass(context, HotStatusesActivity.class);
        	intent.putExtra("STATUS_CATALOG", StatusCatalog.Hot_Retweet.getCatalogNo());
        } else if (appImageId == R.drawable.icon_app_hot_comment) {
        	intent.setClass(context, HotStatusesActivity.class); 
        	intent.putExtra("STATUS_CATALOG", StatusCatalog.Hot_Comment.getCatalogNo());
        } else if (appImageId == R.drawable.icon_app_hot_topic) {
        	//intent.setClass(context, HotTopicsActivity.class);
        	intent.setClass(context, StatusSubscribeActivity.class);
        	intent.putExtra("STATUS_CATALOG", StatusCatalog.Picture_Mobile.getCatalogNo());
        	intent.putExtra("TITLE_ID", R.string.label_app_hot_topic);
        } else if (appImageId == R.drawable.icon_app_daily) {
        	intent.setClass(context, StatusSubscribeActivity.class);
        	intent.putExtra("STATUS_CATALOG", StatusCatalog.News.getCatalogNo());
        	intent.putExtra("TITLE_ID", R.string.label_app_daily);
        } else if (appImageId == R.drawable.icon_app_image) {
        	intent.setClass(context, StatusSubscribeActivity.class);
        	intent.putExtra("STATUS_CATALOG", StatusCatalog.Picture.getCatalogNo());
        	intent.putExtra("TITLE_ID", R.string.label_app_image);
        } else if (appImageId == R.drawable.icon_app_jokes) {
        	intent.setClass(context, StatusSubscribeActivity.class);
        	intent.putExtra("STATUS_CATALOG", StatusCatalog.Joke.getCatalogNo());
        	intent.putExtra("TITLE_ID", R.string.label_app_jokes);
        } else if (appImageId == R.drawable.icon_app_exchange) {
        	ConfigSystemDao configDao = new ConfigSystemDao(context);
		    String username = configDao.getString(Constants.PASSPORT_USERNAME);
//		    if (StringUtil.isEmpty(username)) {
//      	          AppConnect.getInstance(context).showOffers(context);
//      	    } else {
//      		  AppConnect.getInstance(context).showOffers(context, username);
//            }
      	    return;
        } else {
        	Toast.makeText(context, "抱歉，此功能正在开发中..", Toast.LENGTH_LONG).show();
        	return;
        }

        context.startActivity(intent);
	}

}
