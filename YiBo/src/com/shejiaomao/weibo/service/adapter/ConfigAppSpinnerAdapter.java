package com.shejiaomao.weibo.service.adapter;

import java.util.List;

import com.shejiaomao.maobo.R;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cattong.commons.ServiceProvider;
import com.cattong.commons.http.auth.Authorization;
import com.cattong.commons.util.ListUtil;
import com.cattong.entity.ConfigApp;
import com.shejiaomao.weibo.activity.AddAccountActivity;
import com.shejiaomao.weibo.db.ConfigAppDao;
import com.shejiaomao.weibo.service.BaseListAdapter;

public class ConfigAppSpinnerAdapter extends BaseListAdapter<ConfigApp> {
	private ConfigAppDao configAppDao;

	
	public ConfigAppSpinnerAdapter(AddAccountActivity context) {
		super(context);
		
		this.configAppDao = new ConfigAppDao(context);
		setServiceProvider(context.getSp());
	}
	
	public void setServiceProvider(ServiceProvider sp) {
		if (sp == null) {
			return;
		}
		
		dataList.clear();
		
		Authorization auth = new Authorization(sp);
		ConfigApp configApp = new ConfigApp();
		configApp.setAppId(-1l);
		configApp.setServiceProvider(sp);
		configApp.setAppName("默认尾巴");
		configApp.setAppKey(auth.getoAuthConfig().getConsumerKey());
		configApp.setAppSecret(auth.getoAuthConfig().getConsumerSecret());
		configApp.setCallbackUrl(auth.getoAuthConfig().getCallbackUrl());
		dataList.add(configApp);
		
		List<ConfigApp> tempList = configAppDao.findApps(sp);
		if (ListUtil.isNotEmpty(tempList)) {
			dataList.addAll(tempList);
		}
		
		configApp = new ConfigApp();
		configApp.setAppId(-2l);
		configApp.setServiceProvider(sp);
		configApp.setAppName("添加自定义尾巴");
		configApp.setAppKey(auth.getoAuthConfig().getConsumerKey());
		configApp.setAppSecret(auth.getoAuthConfig().getConsumerSecret());
		configApp.setCallbackUrl(auth.getoAuthConfig().getCallbackUrl());
		dataList.add(configApp);
		
		this.notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.list_item_app_key, null);
		}
		
		ConfigApp configApp = (ConfigApp)getItem(position);
		if (configApp == null) {
			return convertView;
		}
		
		TextView tvAppKeyName = (TextView) convertView.findViewById(R.id.tvAppKeyName);
		tvAppKeyName.setText(configApp.getAppName());
		
		return convertView;
	}

	@Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView, parent);
    }

}
