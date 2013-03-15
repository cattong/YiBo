package com.shejiaomao.weibo.service.listener;

import android.content.Intent;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;

import com.cattong.commons.Logger;
import com.cattong.commons.http.auth.Authorization;
import com.cattong.commons.oauth.config.OAuthConfig;
import com.cattong.entity.ConfigApp;
import com.shejiaomao.weibo.activity.AddAccountActivity;
import com.shejiaomao.weibo.activity.AddConfigAppActivity;
import com.shejiaomao.weibo.common.Constants;

public class AddAccountConfigAppItemSelectedListener implements
		OnItemSelectedListener {
    private AddAccountActivity context;
    
	public AddAccountConfigAppItemSelectedListener(AddAccountActivity context) {
		this.context = context;
	}
	
	@Override
	public void onItemSelected(AdapterView<?> parent, View view,
			int position, long id) {
		Adapter adapter = parent.getAdapter();
		Authorization auth = context.getAuth();
		if (auth == null) {
			Logger.error("auth can't be null");
			return;
		}
		
		ConfigApp configApp = (ConfigApp)adapter.getItem(position);		
		if (configApp.getAppId() == -2l) {
			Intent intent = new Intent();
			intent.setClass(context, AddConfigAppActivity.class);
			intent.putExtra("spNo", auth.getServiceProvider().getSpNo());
			context.startActivityForResult(intent, Constants.REQUEST_CODE_CONFIG_APP_ADD);
			return;
		}
		
		OAuthConfig oauthConfig = auth.getoAuthConfig();
		oauthConfig.setConsumerKey(configApp.getAppKey());
		oauthConfig.setConsumerSecret(configApp.getAppSecret());
		oauthConfig.setCallbackUrl(configApp.getCallbackUrl());
		Logger.debug("callback:{}", oauthConfig.getCallbackUrl());
	}
	
	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		context.resetAuthConfigApp();
	}

}
