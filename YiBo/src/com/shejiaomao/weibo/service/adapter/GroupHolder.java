package com.shejiaomao.weibo.service.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.shejiaomao.maobo.R;
import com.shejiaomao.weibo.common.theme.Theme;
import com.shejiaomao.weibo.common.theme.ThemeUtil;

public class GroupHolder {
	ImageView ivGroup;
	TextView tvGroupName;
	TextView tvImpress;
	ImageView ivMoreDetail;
	
	public GroupHolder(View convertView) {
		if (convertView == null) {
			throw new IllegalArgumentException("convertView is null!");
		}
		ivGroup = (ImageView)convertView.findViewById(R.id.ivGroup);
		tvGroupName = (TextView)convertView.findViewById(R.id.tvGroupName);
		tvImpress = (TextView)convertView.findViewById(R.id.tvImpress);
		ivMoreDetail = (ImageView)convertView.findViewById(R.id.ivMoreDetail);
		
		Theme theme = ThemeUtil.createTheme(convertView.getContext());
		ivGroup.setImageDrawable(theme.getDrawable("icon_group"));
		tvGroupName.setTextColor(theme.getColor("content"));
		if (tvImpress != null) {
		    tvImpress.setTextColor(theme.getColor("remark"));
		}
		if (ivMoreDetail != null) {
			ivMoreDetail.setBackgroundDrawable(theme.getDrawable("icon_more_detail"));
		}
	}
	
	public void reset() {
		
	}
}
