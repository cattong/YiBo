package com.shejiaomao.weibo.service.listener;

import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;

import com.cattong.commons.Logger;
import com.cattong.commons.http.auth.Authorization;
import com.cattong.commons.oauth.config.OAuthConfig;
import com.cattong.entity.ConfigApp;
import com.shejiaomao.weibo.activity.AddAccountActivity;

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
		ConfigApp configApp = (ConfigApp)adapter.getItem(position);
		
		Authorization auth = context.getAuth();
		if (auth == null) {
			Logger.error("auth can't be null");
			return;
		}
		
		OAuthConfig oauthConfig = auth.getoAuthConfig();
		oauthConfig.setConsumerKey(configApp.getAppKey());
		oauthConfig.setConsumerSecret(configApp.getAppSecret());
		
		//context.setConfigApp(configApp);
	}
	
	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		context.resetAuthConfigApp();
	}

}
