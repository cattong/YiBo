package com.shejiaomao.weibo.service.adapter;

import com.shejiaomao.maobo.R;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cattong.commons.Logger;
import com.shejiaomao.weibo.common.GlobalResource;
import com.shejiaomao.weibo.common.GlobalVars;
import com.shejiaomao.weibo.common.theme.Theme;
import com.shejiaomao.weibo.common.theme.ThemeUtil;
import com.shejiaomao.weibo.service.task.ImageLoad4HeadTask;

public class DirectMessageHolder {
	private static final String TAG = "DirectMessageHolder";
	private Context context;
	ImageView ivProfilePicture;
	ImageView ivMyProfilePicture;
	TextView tvScreenName;
	TextView tvCreateAt;
	TextView tvMessageText;

	ImageLoad4HeadTask headTask;
	public DirectMessageHolder(View convertView) {
		if (convertView == null) {
			throw new IllegalArgumentException("convertView is null!");
		}
		context = convertView.getContext();
		ivProfilePicture = (ImageView)convertView.findViewById(R.id.ivProfilePicture);
		ivMyProfilePicture = (ImageView)convertView.findViewById(R.id.ivMyProfilePicture);
		tvScreenName = (TextView)convertView.findViewById(R.id.tvScreenName);
		tvCreateAt = (TextView)convertView.findViewById(R.id.tvCreateAt);
		tvMessageText = (TextView)convertView.findViewById(R.id.tvMessageText);
		
		//设置主题 
        Theme theme = ThemeUtil.createTheme(context);
        tvScreenName.setTextColor(theme.getColor("highlight"));
        tvMessageText = (TextView)convertView.findViewById(R.id.tvMessageText);
        tvMessageText.setTextColor(theme.getColor("content"));
        tvMessageText.setLinkTextColor(theme.getColorStateList("selector_text_link"));
        
		reset();
	}

	public void reset() {
		if (ivProfilePicture != null) {
			ivProfilePicture.setVisibility(View.GONE);
		    ivProfilePicture.setImageDrawable(GlobalResource.getDefaultMinHeader(context));
		}
		if (ivMyProfilePicture != null) {
			ivMyProfilePicture.setVisibility(View.GONE);
			ivMyProfilePicture.setImageDrawable(GlobalResource.getDefaultMinHeader(context));
		}
		if (tvCreateAt != null) {
			tvCreateAt.setText("");
    		tvCreateAt.setTextColor(GlobalResource.getStatusTimelineReadColor(context));
		}
		if (tvMessageText != null) {
    		if (tvMessageText.getTextSize() != GlobalVars.FONT_SIZE_HOME_BLOG) {
    			tvMessageText.setTextSize(GlobalVars.FONT_SIZE_HOME_BLOG);
    		    if (Logger.isDebug()) Log.d(TAG, "tweet FontSize: " + GlobalVars.FONT_SIZE_HOME_BLOG);
    	    }
			tvMessageText.setText("");
		}

		headTask = null;
	}

	public void recycle() {
		if (headTask != null) {
			headTask.cancel(true);
		}
		if (Logger.isDebug()) Log.d(TAG, "message convertView recycle");
	}
}
