package com.shejiaomao.weibo.service.task;

import com.shejiaomao.maobo.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.webkit.URLUtil;
import android.widget.Toast;

import com.cattong.commons.LibException;
import com.cattong.commons.Logger;
import com.cattong.commons.ServiceProvider;
import com.cattong.commons.http.auth.Authorization;
import com.cattong.commons.http.auth.OAuthAuthorizeHelper;
import com.cattong.commons.oauth.config.OAuthConfig;
import com.shejiaomao.common.NetUtil;
import com.shejiaomao.common.ResourceBook;
import com.shejiaomao.weibo.activity.AddAccountActivity;
import com.shejiaomao.weibo.activity.AuthorizeActivity;
import com.shejiaomao.weibo.common.Constants;
import com.shejiaomao.weibo.common.SheJiaoMaoException;

public class OAuthRetrieveRequestTokenTask extends AsyncTask<Void, Void, Boolean> {

	private AddAccountActivity context;
	
	private ServiceProvider spSelected;
	private boolean isMakeDefault;
	private boolean isFollowOffical;

	private OAuthConfig oauthConfig;
    private ProgressDialog progressDialog;

    private String message;
    public OAuthRetrieveRequestTokenTask(AddAccountActivity context,ServiceProvider sp,
    	boolean isMakeDefault, boolean isFollowOffical) {
    	this.context = context;
    	this.spSelected = sp;
    	this.isMakeDefault = isMakeDefault;
    	this.isFollowOffical = isFollowOffical;
    }

    @Override
    protected void onPreExecute() {
    	progressDialog = ProgressDialog.show(
    		context, null, context.getString(R.string.msg_retrieving_request_token),
    		true, false
    	);
    }

    @Override
    protected Boolean doInBackground(Void... arg) {
    	boolean isSuccess = false;
    	
    	Authorization auth = context.getAuth();
    	oauthConfig = auth.getoAuthConfig();
        if (oauthConfig == null) {
        	return isSuccess;
        }

		try {
			if (NetUtil.isNETWAP()
				&& URLUtil.isHttpsUrl(oauthConfig.getRequestTokenUrl())) {
				throw new SheJiaoMaoException(SheJiaoMaoException.NET_HTTPS_UNDER_CMWAP);
			}

			// 获取RequestToken和RequestTokenSecret
			OAuthAuthorizeHelper authorizeHelper = new OAuthAuthorizeHelper();
			
			Authorization resultAuth = authorizeHelper.retrieveRequestToken(auth);
            
			//网站认证跳转前保存获取的RequestToken 和 RequestTokenSecret 以及是否设为默认帐号 数据
			SharedPreferences.Editor editor = context.getSharedPreferences(
				Constants.PREFS_NAME_APP_TEMP, Activity.MODE_PRIVATE).edit();
			editor.putString(Constants.PREFS_KEY_OAUTH_TOKEN, resultAuth.getAccessToken());
			editor.putString(Constants.PREFS_KEY_OAUTH_TOKEN_SECRET, resultAuth.getAccessSecret());
			editor.putBoolean(Constants.PREFS_KEY_MAKE_DEFAULT, isMakeDefault);
			editor.putBoolean(Constants.PREFS_KEY_FOLLOW_OFFICAL, isFollowOffical);
			editor.commit();

			Intent intent = new Intent();
			intent.setClass(context, AuthorizeActivity.class);
			intent.putExtra("Authorization", auth);
			intent.putExtra("ServiceProvider", spSelected.toString());
			intent.putExtra("Authorize_Url", authorizeHelper.getAuthorizeUrl(resultAuth));
			intent.putExtra("Callback_Url", oauthConfig.getCallbackUrl());
			context.startActivityForResult(intent, Constants.REQUEST_CODE_OAUTH_AUTHORIZE);

			isSuccess = true;
		} catch (LibException e) {
			message = ResourceBook.getResultCodeValue(e.getErrorCode(), context);
			Logger.debug("ErrorCode:" + e.getErrorCode(), e);
		} catch (SheJiaoMaoException e) {
			message = ResourceBook.getResultCodeValue(e.getStatusCode(), context);
			Logger.debug("ErrorCode:" + e.getStatusCode(), e);
		}

        return isSuccess;
    }

    protected void onPostExecute(Boolean result) {
    	if (progressDialog != null) {
    		try {
                progressDialog.dismiss();
    		} catch (Exception e){}
    	}

        if (!result) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        }
    }

}

