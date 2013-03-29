package com.shejiaomao.weibo.service.listener;

import java.util.regex.Pattern;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.cattong.commons.LibException;
import com.cattong.commons.Logger;
import com.cattong.commons.ServiceProvider;
import com.cattong.commons.http.auth.Authorization;
import com.cattong.commons.http.auth.OAuth2AuthorizeHelper;
import com.cattong.commons.oauth.OAuth2.DisplayType;
import com.cattong.commons.oauth.OAuth2.GrantType;
import com.cattong.commons.oauth.config.OAuthConfig;
import com.shejiaomao.maobo.R;
import com.shejiaomao.weibo.activity.AddAccountActivity;
import com.shejiaomao.weibo.activity.AuthorizeActivity;
import com.shejiaomao.weibo.common.Constants;
import com.shejiaomao.weibo.service.task.OAuthRetrieveRequestTokenTask;
import com.shejiaomao.weibo.service.task.TwitterProxyAuthTask;
import com.shejiaomao.weibo.service.task.XAuthTask;

public class AddAccountAuthorizeClickListener implements OnClickListener {

	private AddAccountActivity context;
	public AddAccountAuthorizeClickListener(AddAccountActivity context) {
		this.context = context;
	}
	
	@Override
	public void onClick(View v) {
		ServiceProvider sp = context.getSp();
		CheckBox cbMakeDefault = (CheckBox)context.findViewById(R.id.cbDefault);
		CheckBox cbFollowOffical = (CheckBox)context.findViewById(R.id.cbFollowOffical);
		boolean isMakeDefault = cbMakeDefault.isChecked();
		boolean isFollowOffical = cbFollowOffical.isChecked();
		
		if (sp == null) {
			Toast.makeText(context, R.string.msg_accounts_add_spSelect, 
				Toast.LENGTH_LONG).show();
			return;
		}
		
		//hide input method
		InputMethodManager inputMethodManager = (InputMethodManager)v.getContext().
		    getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(cbMakeDefault.getWindowToken(), 0);
		
		Authorization auth = context.getAuth();
		OAuthConfig oauthConfig = auth.getoAuthConfig();
		
		LinearLayout llXAuthForm = (LinearLayout) context.findViewById(R.id.llXAuthForm);
		if (llXAuthForm.getVisibility() == View.VISIBLE) {
			xauthAuthorize(sp, isMakeDefault, isFollowOffical);
			return;
		}
		
		if (oauthConfig.getAuthVersion() == Authorization.AUTH_VERSION_OAUTH_1) {
			new OAuthRetrieveRequestTokenTask(context, sp, isMakeDefault, 
				isFollowOffical).execute();
			return;
		}
		
		try {			
			OAuth2AuthorizeHelper authHelper = new OAuth2AuthorizeHelper();
			
			Intent intent = new Intent();
			intent.setClass(context, AuthorizeActivity.class);
			intent.putExtra("Authorization", auth);
			intent.putExtra("ServiceProvider", sp.toString());
			String authorizeUrl = authHelper.getAuthorizeUrl(auth, 
				GrantType.AUTHORIZATION_CODE, DisplayType.MOBILE);
			intent.putExtra("Authorize_Url", authorizeUrl);
			intent.putExtra("Callback_Url", oauthConfig.getCallbackUrl());
			
			context.startActivityForResult(intent,
				Constants.REQUEST_CODE_OAUTH_AUTHORIZE);
		} catch (LibException e) {
			Logger.debug("error", e);
		}

	}

	private static final Pattern SCHEME_HOST_PATH_PATTERN =
		Pattern.compile("http[s]?://[a-z0-9-]+(\\.[a-z0-9-]+)+(/[\\w-]+)*[/]?");
	private static final Pattern HOST_PATH_PATTERN =
		Pattern.compile("[a-z0-9-]+(\\.[a-z0-9-]+)+(/[\\w-]+)*[/]?");
	
	public void xauthAuthorize(ServiceProvider sp, boolean isMakeDefault, boolean isFollowOffical) {
		EditText etUsername = (EditText) context.findViewById(R.id.etUsername);
		EditText etPassword = (EditText) context.findViewById(R.id.etPassword);
		String userName = etUsername.getText().toString().trim();
		String password = etPassword.getText().toString().trim();

		CheckBox cbUseProxy = (CheckBox) context.findViewById(R.id.cbUseApiProxy);
		EditText etRestProxy = (EditText) context.findViewById(R.id.etRestProxy);
		EditText etSearchProxy = (EditText) context.findViewById(R.id.etSearchProxy);
		boolean isUseProxy = cbUseProxy.isChecked();
		
		Authorization auth = context.getAuth();
		auth.setAccessToken(userName);
		auth.setAccessSecret(password);
		
		switch (sp) {
		case Sina:
		case NetEase:
		case Sohu:
		case Fanfou:
			new XAuthTask(context, auth, isMakeDefault,	isFollowOffical).execute();
			break;
		case Twitter:
			if (isUseProxy) {
				String restApi = etRestProxy.getText().toString().trim().toLowerCase();
				if (!restApi.matches(SCHEME_HOST_PATH_PATTERN.toString())) {
					if (restApi.matches(HOST_PATH_PATTERN.toString())) {
						restApi = "http://" + restApi;
					} else {
						Toast.makeText(context,
							R.string.msg_accounts_add_invalid_proxy_url,
							Toast.LENGTH_SHORT).show();
						etRestProxy.requestFocus();
						break;
					}
				}
				String searchApi = etSearchProxy.getText().toString().trim();
				new TwitterProxyAuthTask(
					context, userName,  password,
					restApi, searchApi, isMakeDefault
				).execute();
			} else {
				new XAuthTask(context, auth, isMakeDefault, isFollowOffical).execute();
			}
			break;
		default:
			break;
		}		
	}
}
