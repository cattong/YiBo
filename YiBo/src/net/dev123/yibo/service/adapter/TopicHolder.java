package net.dev123.yibo.service.adapter;

import net.dev123.yibo.R;
import net.dev123.yibo.common.theme.Theme;
import net.dev123.yibo.common.theme.ThemeUtil;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class TopicHolder {
	TextView tvTopic;
	ImageView ivMoreDetail;
	
	public TopicHolder(View convertView) {
		if (convertView == null) {
			throw new IllegalArgumentException("convertView is null!");
		}
		tvTopic = (TextView)convertView.findViewById(R.id.tvTopic);
		ivMoreDetail = (ImageView)convertView.findViewById(R.id.ivMoreDetail);
		
		Theme theme = ThemeUtil.createTheme(convertView.getContext());
		tvTopic.setTextColor(theme.getColor("content"));
		if (ivMoreDetail != null) {
			ivMoreDetail.setBackgroundDrawable(theme.getDrawable("icon_more_detail"));
		}
	}
	
	public void reset() {
		if (tvTopic == null) {
			tvTopic.setText("");
		}
	}
}
