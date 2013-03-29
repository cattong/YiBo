package com.shejiaomao.weibo.service.adapter;

import com.cattong.commons.ServiceProvider;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.shejiaomao.maobo.R;
import com.shejiaomao.weibo.common.theme.Theme;
import com.shejiaomao.weibo.common.theme.ThemeUtil;
import com.shejiaomao.weibo.service.task.ImageLoad4HeadTask;

public class AccountHolder {
	ImageView ivProfilePicture;
	TextView tvScreenName;
	TextView tvSPName;
	TextView tvExpiredHint;
	ImageView ivLineSeperator;
	ImageView ivDelAccount;
	
	ImageLoad4HeadTask headTask;
	private Theme theme;
	public AccountHolder(View convertView) {
		if (convertView == null) {
			throw new IllegalArgumentException("convertView is null!");
		}
		ivProfilePicture = (ImageView)convertView.findViewById(R.id.ivProfileImage);
		tvScreenName = (TextView)convertView.findViewById(R.id.tvScreenName);
		tvSPName = (TextView)convertView.findViewById(R.id.tvSPName);
		tvExpiredHint = (TextView)convertView.findViewById(R.id.tvExpiredHint);
		ivLineSeperator = (ImageView)convertView.findViewById(R.id.ivLineSeperator);
		ivDelAccount = (ImageView)convertView.findViewById(R.id.ivDelAccount);
		
		//设置主题 
		theme = ThemeUtil.createTheme(convertView.getContext());
		tvScreenName.setTextColor(theme.getColor("content"));
		tvSPName.setTextColor(theme.getColor("quote"));
		tvExpiredHint.setTextColor(theme.getColor("red"));
		ivLineSeperator.setBackgroundDrawable(theme.getDrawable("line_seperator"));
		ivDelAccount.setImageDrawable(theme.getDrawable("icon_account_delete_normal"));
	}
	
	public void reset(ServiceProvider sp) {
		if (ivProfilePicture != null) {
			Drawable drawable = null;
			switch (sp) {
			case Sina:
				drawable = theme.getDrawable("icon_logo_sina_min");
				break;
			case Sohu:
				drawable = theme.getDrawable("icon_logo_sohu_min");
				break;
			case NetEase:
				drawable = theme.getDrawable("icon_logo_netease_min");
				break;
			case Tencent:
				drawable = theme.getDrawable("icon_logo_tencent_min");
				break;
			case Twitter:
				drawable = theme.getDrawable("icon_logo_twitter_min");
				break;
			case Fanfou:
				drawable = theme.getDrawable("icon_logo_fanfou_min");
				break;
			case RenRen:
				drawable = theme.getDrawable("icon_logo_renren_min");
				break;
			case KaiXin:
				drawable = theme.getDrawable("icon_logo_kaixin_min");
				break;
			case QQZone:
				drawable = theme.getDrawable("icon_logo_qqzone_min");
				break;
			default:
				drawable = theme.getDrawable("icon_header_default_min");
				break;
			}
			ivProfilePicture.setImageDrawable(drawable);
		}
		
		if (tvExpiredHint != null) {
			tvExpiredHint.setVisibility(View.GONE);
		}
	}
}
