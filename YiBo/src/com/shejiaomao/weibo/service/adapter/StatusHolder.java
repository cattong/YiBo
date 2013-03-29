package com.shejiaomao.weibo.service.adapter;

import com.shejiaomao.maobo.R;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cattong.commons.Logger;
import com.cattong.commons.ServiceProvider;
import com.shejiaomao.weibo.common.GlobalResource;
import com.shejiaomao.weibo.common.GlobalVars;
import com.shejiaomao.weibo.common.theme.Theme;
import com.shejiaomao.weibo.common.theme.ThemeUtil;
import com.shejiaomao.weibo.service.listener.ImageHeadClickListener;
import com.shejiaomao.weibo.service.task.ImageLoad4HeadTask;
import com.shejiaomao.weibo.service.task.ImageLoad4ThumbnailTask;
import com.shejiaomao.weibo.service.task.QueryResponseCountTask;
import com.shejiaomao.widget.RichTextView;

public class StatusHolder {
	private static final String TAG = StatusHolder.class.getSimpleName();
	private Context context;
	ImageView ivProfilePicture;
	TextView tvScreenName;
	ImageView ivVerify;
	ImageView ivLocation;
	ImageView ivAttachment;
	ImageView ivFavorite;
	TextView tvCreatedAt;
	TextView tvText;
	TextView tvRetweetText;
    View llRetweet;
    ImageView ivThumbnail;
    ImageView ivRetweetThumbnail;
    TextView tvImageInfo;
    TextView tvRetweetImageInfo;
    TextView tvResponse;
    TextView tvSource;

    ImageHeadClickListener headClickListener;
    ImageLoad4HeadTask headTask;
    public ImageLoad4ThumbnailTask thumbnailTask;
    public QueryResponseCountTask responseCountTask;
    
    public StatusHolder(View convertView, ServiceProvider serviceProvider) {
    	if (convertView == null) {
    		throw new IllegalArgumentException("convertView is null!");
    	}
    	context = convertView.getContext();
    	ivProfilePicture = (ImageView) convertView.findViewById(R.id.ivProfilePicture);
    	tvScreenName = (TextView) convertView.findViewById(R.id.tvScreenName);
    	ivVerify = (ImageView) convertView.findViewById(R.id.ivVerify);
    	ivLocation = (ImageView) convertView.findViewById(R.id.ivLocation);
    	ivFavorite = (ImageView) convertView.findViewById(R.id.ivFavorite);
		ivAttachment = (ImageView) convertView.findViewById(R.id.ivAttachment);
		tvCreatedAt = (TextView) convertView.findViewById(R.id.tvCreatedAt);
		tvText = (TextView) convertView.findViewById(R.id.tvText);
		ivThumbnail = (ImageView) convertView.findViewById(R.id.ivThumbnail);
		ivRetweetThumbnail = (ImageView) convertView.findViewById(R.id.ivRetweetThumbnail);
		tvImageInfo = (TextView) convertView.findViewById(R.id.tvImageInfo);
		tvRetweetImageInfo = (TextView) convertView.findViewById(R.id.tvRetweetImageInfo);
		tvRetweetText = (TextView) convertView.findViewById(R.id.tvRetweetText);
        llRetweet = convertView.findViewById(R.id.llRetweet);
        tvResponse = (TextView) convertView.findViewById(R.id.tvResponse);
        tvSource = (TextView) convertView.findViewById(R.id.tvSource);
        if (tvText instanceof RichTextView) {
        	((RichTextView)tvText).setProvider(serviceProvider);
        }
        if (tvRetweetText instanceof RichTextView) {
        	((RichTextView)tvRetweetText).setProvider(serviceProvider);
        }

        //初始图片资源
        Theme theme = ThemeUtil.createTheme(context);
        ivVerify.setImageDrawable(GlobalResource.getIconVerification(context));
        ivLocation.setImageDrawable(GlobalResource.getIconLocation(context));
        ivFavorite.setImageDrawable(GlobalResource.getIconFavorite(context));
        ivAttachment.setImageDrawable(GlobalResource.getIconAttachment(context));
        llRetweet.setBackgroundDrawable(GlobalResource.getBgRetweetFrame(context));
        llRetweet.setPadding(theme.dip2px(10), theme.dip2px(12), 
        	theme.dip2px(10), theme.dip2px(6));

        headClickListener = new ImageHeadClickListener();
        ivProfilePicture.setOnClickListener(headClickListener);

        //设置主题 
        tvScreenName.setTextColor(theme.getColor("highlight"));
        tvText.setTextColor(theme.getColor("content"));
        tvText.setLinkTextColor(theme.getColorStateList("selector_text_link"));
        int quote = theme.getColor("quote");
        tvRetweetText.setTextColor(quote);
        tvRetweetText.setLinkTextColor(theme.getColorStateList("selector_text_link"));
        tvSource.setTextColor(quote);
        tvResponse.setTextColor(theme.getColor("emphasize"));
        ivThumbnail.setBackgroundDrawable(theme.getDrawable("shape_attachment"));
        ivRetweetThumbnail.setBackgroundDrawable(theme.getDrawable("shape_attachment"));
        tvImageInfo.setTextColor(quote);
        tvRetweetImageInfo.setTextColor(quote);
        
        reset();
    }

    /* 重新初始化 */
    public void reset() {
    	if (ivProfilePicture != null) {
    		ivProfilePicture.setVisibility(View.GONE);
    		ivProfilePicture.setImageDrawable(GlobalResource.getDefaultMinHeader(context));
    	}
    	if (tvScreenName != null) {
    		tvScreenName.setText("");
    	}
    	if (ivVerify != null) {
    		ivVerify.setVisibility(View.GONE);
    	}
    	if (ivLocation != null) {
    		ivLocation.setVisibility(View.GONE);
    	}
    	if (ivFavorite != null) {
		    ivFavorite.setVisibility(View.GONE);
    	}
    	if (ivAttachment != null) {
		    ivAttachment.setVisibility(View.GONE);
    	}
    	if (tvCreatedAt != null) {
    		tvCreatedAt.setText("");
    		tvCreatedAt.setTextColor(GlobalResource.getStatusTimelineReadColor(context));
    	}
    	if (tvText != null) {
    		tvText.setText("");
    		if (tvText.getTextSize() != GlobalVars.FONT_SIZE_HOME_BLOG) {
    		    tvText.setTextSize(GlobalVars.FONT_SIZE_HOME_BLOG);
    		    if (Logger.isDebug()) Log.d(TAG, "tweet FontSize: " + GlobalVars.FONT_SIZE_HOME_BLOG);
    	    }
    	}
    	if (llRetweet != null) {
    		llRetweet.setVisibility(View.GONE);
    	}
    	if (tvRetweetText != null) {
    		tvRetweetText.setVisibility(View.GONE);
    		tvRetweetText.setText("");
    		if (tvRetweetText.getTextSize() != GlobalVars.FONT_SIZE_HOME_RETWEET) {
    		    tvRetweetText.setTextSize(GlobalVars.FONT_SIZE_HOME_RETWEET);
    		    if (Logger.isDebug()) Log.d(TAG, "retweet FontSize: " + GlobalVars.FONT_SIZE_HOME_RETWEET);
    		}
    	}
    	if (ivThumbnail != null) {
    		ivThumbnail.setVisibility(View.GONE);
    		ivThumbnail.setImageDrawable(GlobalResource.getDefaultThumbnail(context));
    	}
    	if (ivRetweetThumbnail != null) {
    		ivRetweetThumbnail.setVisibility(View.GONE);
    		ivRetweetThumbnail.setImageDrawable(GlobalResource.getDefaultThumbnail(context));
    	}
        
    	if (tvImageInfo != null) {
    		tvImageInfo.setVisibility(View.GONE);
    		tvImageInfo.setText("");
    	}
    	if (tvRetweetImageInfo != null) {
    		tvRetweetImageInfo.setVisibility(View.GONE);
    		tvRetweetImageInfo.setText("");
    	}

    	if (headClickListener != null) {
    		headClickListener.setUser(null);
    	}
    	headTask = null;
    	thumbnailTask = null;
    	responseCountTask = null;
    }

    /*
     * 资源回收
     */
    public void recycle() {
    	if (headTask != null) {
    		headTask.cancel(true);
    	}
    	if (thumbnailTask != null) {
    		thumbnailTask.cancel(true);
    	}
    	if (responseCountTask != null) {
    		responseCountTask.cancel(true);
    	}
    	headTask = null;
    	thumbnailTask = null;
    	responseCountTask = null;

    	if (Logger.isDebug()) Log.d(TAG, "status convertView recycle");
    }
}
