package com.shejiaomao.weibo.service.adapter;

import com.shejiaomao.maobo.R;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.cattong.commons.Logger;
import com.shejiaomao.weibo.common.GlobalResource;
import com.shejiaomao.weibo.common.theme.Theme;
import com.shejiaomao.weibo.common.theme.ThemeUtil;
import com.shejiaomao.weibo.service.task.ImageLoad4HeadTask;

public class UserSelectorHolder {
	private static final String TAG = "UserSelectorHolder";
	private Context context;
	ImageView ivProfilePicture;
	TextView tvScreenName;
	TextView tvImpress;
	ImageView ivVerify;
	CheckBox cbUser;

	ImageLoad4HeadTask headTask;
	public UserSelectorHolder(View convertView) {
		if (convertView == null) {
			throw new IllegalArgumentException("convertView is null!");
		}
		context = convertView.getContext();
		ivProfilePicture = (ImageView) convertView.findViewById(R.id.ivProfilePicture);
		tvScreenName = (TextView) convertView.findViewById(R.id.tvScreenName);
		ivVerify = (ImageView) convertView.findViewById(R.id.ivVerify);
		tvImpress = (TextView) convertView.findViewById(R.id.tvImpress);
		cbUser = (CheckBox)convertView.findViewById(R.id.cbUser);

		//主题
		Theme theme = ThemeUtil.createTheme(context);
		tvScreenName.setTextColor(theme.getColor("content"));
		ivVerify.setImageDrawable(theme.getDrawable("icon_verification"));
		tvImpress.setTextColor(theme.getColor("remark"));
		cbUser.setButtonDrawable(theme.getDrawable("selector_checkbox"));
		
		reset();
	}

	public void reset() {
		if (ivProfilePicture != null) {
			ivProfilePicture.setImageDrawable(GlobalResource.getDefaultMinHeader(context));
		}

		if (tvScreenName != null) {
			tvScreenName.setText("");
		}

		if (ivVerify != null) {
			ivVerify.setVisibility(View.GONE);
		}

		if (tvImpress != null) {
			tvImpress.setText("");
		}

	    if (cbUser != null) {
	    	cbUser.setChecked(false);
	    }

	    headTask = null;
	}

	public void recycle() {
		if (headTask != null) {
			headTask.cancel(true);
		}
		if (Logger.isDebug()) Log.d(TAG, "userselector convertView recycle");
	}
}
