package com.shejiaomao.weibo.service.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.shejiaomao.maobo.R;
import com.shejiaomao.weibo.common.theme.Theme;
import com.shejiaomao.weibo.common.theme.ThemeUtil;

public class ServiceProviderHolder {
	ImageView ivSpIcon;
	TextView tvSpName;
	
	public ServiceProviderHolder(View convertView) {
		if (convertView == null) {
			throw new IllegalArgumentException("convertView is null!");
		}
		
		ivSpIcon = (ImageView) convertView.findViewById(R.id.ivSpIcon);
		tvSpName = (TextView) convertView.findViewById(R.id.tvSpName);
		
		//设置主题
		Theme theme = ThemeUtil.createTheme(convertView.getContext());
		tvSpName.setTextColor(theme.getColor("content"));
	}
	
	public void reset() {
		
	}
}
