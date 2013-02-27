package net.dev123.yibo.service.adapter;

import net.dev123.yibo.R;
import net.dev123.yibo.common.theme.Theme;
import net.dev123.yibo.common.theme.ThemeUtil;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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
