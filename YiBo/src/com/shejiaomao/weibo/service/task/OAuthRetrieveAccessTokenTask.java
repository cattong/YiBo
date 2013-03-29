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
import com.cattong.commons.http.auth.OAuthAuthorizeHelper;
import com.cattong.commons.oauth.config.OAuthConfig;
import com.cattong.commons.util.ArrayUtil;
import com.cattong.commons.util.StringUtil;
import com.cattong.entity.Account;
import com.cattong.entity.User;
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

public class OAuthRetrieveAccessTokenTask extends AsyncTask<String, Void, Boolean> {

	private AddAccountActivity context;
	private OAuthConfig oauthConfig;

    private Weibo mBlog;
    private ServiceProvider sp;
    private LocalAccount account;
    private boolean isMakeDefault;
    private boolean isFollowOffical;

    private String resultMsg;
    private ProgressDialog progressDialog;

    public OAuthRetrieveAccessTokenTask(AddAccountActivity context){
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
    	if (ArrayUtil.isEmpty(data) || data.length != 3) {
    		return isSuccess;
    	}
    	
    	String token = data[0];
    	String verifier = data[1];
    	String spString = data[2];
    	sp = ServiceProvider.valueOf(spString);
    	if (sp == null || StringUtil.isEmpty(token)) {
    		return isSuccess;
    	}
    	
    	Authorization auth = context.getAuth();
    	auth.setAccessToken(token);
    	oauthConfig = auth.getoAuthConfig();
    	if (oauthConfig == null) {
    		return isSuccess;
    	}

		try {
			if (NetUtil.isNETWAP()
				&& URLUtil.isHttpsUrl(oauthConfig.getAccessTokenUrl())) {
				throw new SheJiaoMaoException(SheJiaoMaoException.NET_HTTPS_UNDER_CMWAP);
			}

			//获取临时存储的数据
			SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFS_NAME_APP_TEMP, Activity.MODE_PRIVATE);
			String requestTokenStr = sharedPreferences.getString(Constants.PREFS_KEY_OAUTH_TOKEN, null);
			String tokenSecret = sharedPreferences.getString(Constants.PREFS_KEY_OAUTH_TOKEN_SECRET, null);
			isMakeDefault = sharedPreferences.getBoolean(Constants.PREFS_KEY_MAKE_DEFAULT, Boolean.FALSE);
			isFollowOffical = sharedPreferences.getBoolean(Constants.PREFS_KEY_FOLLOW_OFFICAL, Boolean.TRUE);

			//清除临时存储的OAuth Request Token 及 OAuth Request Token Secret
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.remove(Constants.PREFS_KEY_OAUTH_TOKEN);
			editor.remove(Constants.PREFS_KEY_OAUTH_TOKEN_SECRET);
			editor.remove(Constants.PREFS_KEY_MAKE_DEFAULT);
			editor.remove(Constants.PREFS_KEY_FOLLOW_OFFICAL);
			editor.commit();

			if (!StringUtil.isEquals(token, requestTokenStr)) {
				throw new SheJiaoMaoException(SheJiaoMaoException.TOKEN_MISMATCH);
			}

			// 使用OAuth Verifier 及 RequestToken 换取 AccessToken
			OAuthAuthorizeHelper authorizeHelper = new OAuthAuthorizeHelper();
			
			auth.setAccessToken(requestTokenStr);
			auth.setAccessSecret(tokenSecret);
			Authorization resultAuth = authorizeHelper.retrieveAccessToken(auth, verifier);

			Logger.debug(resultAuth.toString());

	    	mBlog = WeiboFactory.getInstance(resultAuth);
	    	User user = mBlog.verifyCredentials();

	    	final LocalAccountDao accountDao = new LocalAccountDao(context);

	    	if (accountDao.isExists(sp, user.getUserId())) {
				throw new SheJiaoMaoException(SheJiaoMaoException.ACCOUNT_IS_EXIST);
			}

			if (accountDao.findAllValid() == null) {
				isMakeDefault = true;
			}

			account = new LocalAccount();
			account.setAuthorization(auth);
			account.setUser(user);
			account.setState(Account.STATE_ACTIVE);
			account.setAppKey(oauthConfig.getConsumerKey());
			account.setAppSecret(oauthConfig.getConsumerSecret());
			account.setCreatedAt(new Date());
			accountDao.add(account);

			if(isMakeDefault){
				accountDao.makeDefault(account);
			}

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
    	if (progressDialog != null) {
    		try {
                progressDialog.dismiss();
    		} catch(Exception e) { }
    	}

        if (result) {
        	Toast.makeText(context, R.string.msg_account_add_success, Toast.LENGTH_SHORT).show();
        	SharedPreferences preferences = context.getSharedPreferences(
        		Constants.PREFS_NAME_APP_TEMP, Activity.MODE_PRIVATE);
    		if (isFollowOffical) {
				new FollowOfficalTask(account).execute();
			}
        	AddAccountActivity.saveNewAccountId(preferences, account.getAccountId());
			context.setResult(Constants.RESULT_CODE_SUCCESS);
        	context.finish();
        } else {
            Toast.makeText(context, resultMsg, Toast.LENGTH_LONG).show();
        }
    }
}
