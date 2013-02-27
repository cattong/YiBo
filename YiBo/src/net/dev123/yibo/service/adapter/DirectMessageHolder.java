package net.dev123.yibo.service.adapter;

import net.dev123.yibo.R;
import net.dev123.yibo.common.Constants;
import net.dev123.yibo.common.GlobalVars;
import net.dev123.yibo.common.GlobalResource;
import net.dev123.yibo.common.theme.Theme;
import net.dev123.yibo.common.theme.ThemeUtil;
import net.dev123.yibo.service.task.ImageLoad4HeadTask;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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
    		    if (Constants.DEBUG) Log.d(TAG, "tweet FontSize: " + GlobalVars.FONT_SIZE_HOME_BLOG);
    	    }
			tvMessageText.setText("");
		}

		headTask = null;
	}

	public void recycle() {
		if (headTask != null) {
			headTask.cancel(true);
		}
		if (Constants.DEBUG) Log.d(TAG, "message convertView recycle");
	}
}
