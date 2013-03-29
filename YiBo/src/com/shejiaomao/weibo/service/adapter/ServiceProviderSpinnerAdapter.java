package com.shejiaomao.weibo.service.adapter;

import com.cattong.commons.ServiceProvider;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.shejiaomao.maobo.R;
import com.shejiaomao.weibo.common.theme.Theme;
import com.shejiaomao.weibo.common.theme.ThemeUtil;

public class ServiceProviderSpinnerAdapter extends BaseAdapter {

	private ServiceProvider[] serviceProviders = {
		ServiceProvider.Sina,
		ServiceProvider.Tencent,
		ServiceProvider.Sohu,
		ServiceProvider.NetEase,
		ServiceProvider.Fanfou,
		ServiceProvider.Twitter,
		ServiceProvider.RenRen,
		ServiceProvider.KaiXin,
		ServiceProvider.QQZone
	};

	private String[] icons = {
		"icon_logo_sina_min",
		"icon_logo_tencent_min",
		"icon_logo_sohu_min",
		"icon_logo_netease_min",
		"icon_logo_fanfou_min",
		"icon_logo_twitter_min",
		"icon_logo_renren_min",
		"icon_logo_kaixin_min",
		"icon_logo_qqzone_min"
	};

	private String[] spNames;

	private Context context;
	private LayoutInflater layoutInflater;
	public ServiceProviderSpinnerAdapter(Context context) {
		this.context = context;
		this.spNames = context.getResources().getStringArray(R.array.service_provider);
		this.layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return serviceProviders.length;
	}

	@Override
	public ServiceProvider getItem(int position) {
		return serviceProviders[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ServiceProviderHolder holder = null;
		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.list_item_sp, null);
			holder = new ServiceProviderHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (ServiceProviderHolder)convertView.getTag();
		}
		
		holder.reset();
		
		Theme theme = ThemeUtil.createTheme(convertView.getContext());
		holder.ivSpIcon.setImageDrawable(theme.getDrawable(icons[position]));		
		holder.tvSpName.setText(spNames[position]);
		
		return convertView;
	}

	@Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView, parent);
    }

}
