package com.shejiaomao.weibo.service.task;

import java.util.Date;

import com.shejiaomao.maobo.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.Toast;

import com.cattong.commons.LibException;
import com.cattong.commons.Logger;
import com.cattong.commons.ServiceProvider;
import com.cattong.commons.http.auth.Authorization;
import com.cattong.commons.http.auth.OAuth2AuthorizeHelper;
import com.cattong.commons.http.auth.OAuthAuthorizeHelper;
import com.cattong.commons.oauth.config.OAuthConfig;
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

public class XAuthTask extends AsyncTask<Void, Void, Boolean> {
	private AddAccountActivity context;	

	private Authorization auth;
	private boolean isMakeDefault;
	private boolean isFollowOffical;
	private OAuthConfig oauthConfig;

	private ProgressDialog progressDialog;

	private LocalAccount account;

	private String resultMsg;
	public XAuthTask(AddAccountActivity context, Authorization auth,
			boolean isMakeDefault, boolean isFollowOffical) {
		this.context = context;
		this.auth = auth;
		this.isMakeDefault = isMakeDefault;
		this.isFollowOffical = isFollowOffical;		
	}

	@Override
	protected void onPreExecute() {
    	Button btnAuthorize = (Button)context.findViewById(R.id.btnAuthorize);
    	btnAuthorize.setEnabled(false);

		progressDialog = ProgressDialog.show(context, "",
			context.getString(R.string.msg_retrieving_authorized_token), true, false);
	    progressDialog.setOwnerActivity(context);
	}

	@Override
	protected Boolean doInBackground(Void... arg) {
		boolean isSuccess= false;
		if (auth == null) {
			return isSuccess;
		}
		
    	oauthConfig = auth.getoAuthConfig();
    	if (oauthConfig == null) {
    		return isSuccess;
    	}
    	
    	String username = auth.getAccessToken();
    	String password = auth.getAccessSecret();
    	ServiceProvider sp = auth.getServiceProvider();
		try {
			if (NetUtil.isNETWAP()
				&& (URLUtil.isHttpsUrl(oauthConfig.getRequestTokenUrl())
				  	|| URLUtil.isHttpsUrl(oauthConfig.getAccessTokenUrl()))) {
				throw new SheJiaoMaoException(SheJiaoMaoException.NET_HTTPS_UNDER_CMWAP);
			}
			
			Authorization resultAuth = null;
			switch (sp) {
			case NetEase:
				if (username.indexOf("@") < 0) {
					username += "@163.com"; //如果用户没输入完整的邮件地址，则自动追加 @163.com
				}
				//直落
			case Sina:
			case Sohu:
			case Fanfou:
			case Twitter:
				auth.setAccessToken(username);
				auth.setAccessSecret(password);
				if (oauthConfig.getAuthVersion() == Authorization.AUTH_VERSION_OAUTH_2) {
					OAuth2AuthorizeHelper authHelper = new OAuth2AuthorizeHelper();
					resultAuth = authHelper.retrieveAccessToken(auth);
				} else {
					OAuthAuthorizeHelper authHelper = new OAuthAuthorizeHelper();
					resultAuth = authHelper.retrieveAccessToken(auth);
				}
				
				break;
			default:
				break;
			}

			if (resultAuth == null) {
				throw new SheJiaoMaoException(SheJiaoMaoException.AUTH_TOKEN_IS_NULL);
			}

			Logger.debug(resultAuth.toString());
			
			BaseUser user = null;
			if (sp.isSns()) {
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

			if (isMakeDefault) {
				accountDao.makeDefault(account);
			}

			GlobalVars.addAccount(account);

			isSuccess = true;
		} catch (LibException e) {
			resultMsg = ResourceBook.getResultCodeValue(e.getErrorCode(), context);
			Logger.error(resultMsg, e);
			context.resetAuthToken();
		} catch (SheJiaoMaoException e) {
			resultMsg = ResourceBook.getResultCodeValue(e.getStatusCode(), context);
			Logger.error(resultMsg, e);
			context.resetAuthToken();
		}

		return isSuccess;
	}

	protected void onPostExecute(Boolean result) {
		if (progressDialog != null) {
			try {
			    progressDialog.dismiss();
			} catch(Exception e){}
		}

    	Button btnAuthorize = (Button) context.findViewById(R.id.btnAuthorize);
    	btnAuthorize.setEnabled(true);

		if (result) {
			SharedPreferences prefs =
				context.getSharedPreferences(Constants.PREFS_NAME_APP_TEMP, Activity.MODE_PRIVATE);
			AddAccountActivity.saveNewAccountId(prefs, account.getAccountId());
			if (isFollowOffical) {
				new FollowOfficalTask(account).execute();
			}
			context.setResult(Constants.RESULT_CODE_SUCCESS);
			context.finish();
		} else {
			Toast.makeText(context, resultMsg, Toast.LENGTH_LONG).show();
		}
	}

}
