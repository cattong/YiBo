package net.dev123.yibo.service.adapter;

import net.dev123.yibo.R;
import net.dev123.yibo.common.theme.Theme;
import net.dev123.yibo.common.theme.ThemeUtil;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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
