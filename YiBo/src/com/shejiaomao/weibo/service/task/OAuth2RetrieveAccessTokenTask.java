package com.shejiaomao.weibo.service.task;

import java.util.Date;

import com.shejiaomao.maobo.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.webkit.URLUtil;
import android.widget.Toast;

import com.cattong.commons.LibException;
import com.cattong.commons.Logger;
import com.cattong.commons.ServiceProvider;
import com.cattong.commons.http.auth.Authorization;
import com.cattong.commons.http.auth.OAuth2AuthorizeHelper;
import com.cattong.commons.oauth.config.OAuthConfig;
import com.cattong.commons.util.ArrayUtil;
import com.cattong.commons.util.StringUtil;
import com.cattong.entity.Account;
import com.cattong.entity.BaseUser;
import com.cattong.sns.Sns;
import com.cattong.sns.SnsFactory;
import com.cattong.weibo.Weibo;
import com.cattong.weibo.WeiboFactory;
import com.shejiaomao.common.NetUtil;
import com.shejiaomao.common.ResourceBook;
import com.shejiaomao.weibo.activity.AddAccountActivity;
import com.shejiaomao.weibo.common.Constants;
import com.shejiaomao.weibo.common.GlobalVars;
import com.shejiaomao.weibo.common.SheJiaoMaoException;
import com.shejiaomao.weibo.db.LocalAccount;
import com.shejiaomao.weibo.db.LocalAccountDao;

public class OAuth2RetrieveAccessTokenTask extends AsyncTask<String, Void, Boolean> {

	private AddAccountActivity context;
	private OAuthConfig oauthConfig;

    private ServiceProvider sp;
    private LocalAccount account;
    private String authorizationCode;

    private String resultMsg;
    private ProgressDialog progressDialog;

    public OAuth2RetrieveAccessTokenTask(AddAccountActivity context) {
    	this.context = context;
    }

    @Override
    protected void onPreExecute() {
    	progressDialog = ProgressDialog.show(
    		context, null,
    		context.getString(R.string.msg_retrieving_authorized_token), true,
    		false
    	);
    }

    @Override
    protected Boolean doInBackground(String... data) {
    	boolean isSuccess= false;
    	if (ArrayUtil.isEmpty(data) || data.length != 2) {
    		return isSuccess;
    	}
    	
    	authorizationCode = data[0];
    	sp = ServiceProvider.valueOf(data[1]);
    	if (sp == null || StringUtil.isEmpty(authorizationCode)) {
    		return isSuccess;
    	}
    	
    	Authorization auth = context.getAuth();
    	oauthConfig = auth.getoAuthConfig();
    	if (oauthConfig == null) {
    		return isSuccess;
    	}

		try {
			if (NetUtil.isNETWAP()
				&& URLUtil.isHttpsUrl(oauthConfig.getAccessTokenUrl())) {
				throw new SheJiaoMaoException(SheJiaoMaoException.NET_HTTPS_UNDER_CMWAP);
			}

			// 使用Authorization Code 换取 AccessToken
			OAuth2AuthorizeHelper oauthHelper = new OAuth2AuthorizeHelper();
	    	Authorization resultAuth = oauthHelper.retrieveAccessToken(
	    		auth, authorizationCode, null);

	    	Logger.debug(resultAuth.toString());

	    	BaseUser user = null;
	    	if (resultAuth.getServiceProvider().isSns()) {
	    	    Sns sns = SnsFactory.getInstance(resultAuth);
	    	    String userId = sns.getUserId();
	    	    user = sns.showUser(userId);
	    	} else {
	    		Weibo mBlog = WeiboFactory.getInstance(resultAuth);
	    		user = mBlog.verifyCredentials();
	    	}
	    	
	    	final LocalAccountDao accountDao = new LocalAccountDao(context);
	    	if (accountDao.isExists(sp, user.getUserId())) {
				throw new SheJiaoMaoException(SheJiaoMaoException.ACCOUNT_IS_EXIST);
			}

			account = new LocalAccount();
			account.setAuthorization(auth);
			account.setUser(user);
			account.setAccessSecret(auth.getRefreshToken() == null ? "null" : auth.getRefreshToken());
			account.setState(Account.STATE_ACTIVE);
			account.setAppKey(oauthConfig.getConsumerKey());
			account.setAppSecret(oauthConfig.getConsumerSecret());
			account.setCreatedAt(new Date());
			accountDao.add(account);

			GlobalVars.addAccount(account);

			isSuccess = true;
		} catch (LibException e) {
			resultMsg = ResourceBook.getResultCodeValue(e.getErrorCode(), context);
			Logger.debug("ErrorCode:" + e.getErrorCode(), e);
			context.resetAuthToken();
		} catch (SheJiaoMaoException e) {
			resultMsg = ResourceBook.getResultCodeValue(e.getStatusCode(), context);
			Logger.debug("ErrorCode:" + e.getStatusCode(), e);
			context.resetAuthToken();
		}

		return isSuccess;
    }

    protected void onPostExecute(Boolean result) {
    	if (progressDialog != null
    		&& progressDialog.getContext() != null) {
    		try {
                progressDialog.dismiss();
    		} catch(Exception e) { }
    	}

        if (result) {
        	Toast.makeText(context, R.string.msg_account_add_success, Toast.LENGTH_SHORT).show();
        	SharedPreferences preferences = context.getSharedPreferences(
        		Constants.PREFS_NAME_APP_TEMP, Activity.MODE_PRIVATE);
        	AddAccountActivity.saveNewAccountId(preferences, account.getAccountId());
        	context.setResult(Constants.RESULT_CODE_SUCCESS);
        	context.finish();
        } else {
            Toast.makeText(context, resultMsg, Toast.LENGTH_LONG).show();
        }
    }
}
