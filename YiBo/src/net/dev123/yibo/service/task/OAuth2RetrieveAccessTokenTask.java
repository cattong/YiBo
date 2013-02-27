package net.dev123.yibo.service.task;

import java.util.Date;

import net.dev123.commons.ServiceProvider;
import net.dev123.commons.http.auth.OAuth2Authorization;
import net.dev123.commons.http.auth.OAuth2AuthorizeHelper;
import net.dev123.commons.oauth.config.OAuthConfiguration;
import net.dev123.commons.oauth.config.OAuthConfigurationFactory;
import net.dev123.commons.oauth2.OAuth2AccessToken;
import net.dev123.commons.util.StringUtil;
import net.dev123.entity.BaseUser;
import net.dev123.exception.LibException;
import net.dev123.sns.Sns;
import net.dev123.sns.SnsFactory;
import net.dev123.yibo.AddAccountActivity;
import net.dev123.yibo.R;
import net.dev123.yibo.common.Constants;
import net.dev123.yibo.common.GlobalVars;
import net.dev123.yibo.common.NetUtil;
import net.dev123.yibo.common.ResourceBook;
import net.dev123.yibo.common.YiBoException;
import net.dev123.yibo.db.LocalAccount;
import net.dev123.yibo.db.LocalAccountDao;
import net.dev123.yibome.entity.Account;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.webkit.URLUtil;
import android.widget.Toast;

public class OAuth2RetrieveAccessTokenTask extends AsyncTask<String, Void, Boolean> {
	private static final String TAG = OAuth2RetrieveAccessTokenTask.class.getSimpleName();

	private AddAccountActivity context;
	private OAuthConfiguration conf;

	private Sns sns;
    private ServiceProvider sp;
    private LocalAccount account;
    private String authorizationCode;

    private String message;
    private ProgressDialog progressDialog;

    public OAuth2RetrieveAccessTokenTask(AddAccountActivity context){
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
    	authorizationCode = data[0];
    	sp = ServiceProvider.valueOf(data[1]);
    	conf = OAuthConfigurationFactory.getOAuthConfiguration(sp);
    	if (sp == null || conf == null || StringUtil.isEmpty(authorizationCode)) {
    		return isSuccess;
    	}

		try {
			if (NetUtil.isCMWAP()
				&& URLUtil.isHttpsUrl(conf.getOAuthAccessTokenURL())) {
				throw new YiBoException(YiBoException.NET_HTTPS_UNDER_CMWAP);
			}

			// 使用Authorization Code 换取 AccessToken
			OAuth2AuthorizeHelper oauthHelper = new OAuth2AuthorizeHelper(sp);
			if (context.isUseCustomAppKey() && context.getAppSelected() != null) {
				oauthHelper.setConsumer(context.getAppSelected().getAppKey(), 
					context.getAppSelected().getAppSecret());
			}
	    	OAuth2AccessToken accessToken =
	    		oauthHelper.getAccessTokenByAuthorizationCode(authorizationCode, null);

	    	if(Constants.DEBUG) {
	    		Log.d(TAG, accessToken.toString());
	    	}

	    	OAuth2Authorization auth = new OAuth2Authorization(accessToken, sp);

	    	sns = SnsFactory.getInstance(auth);
	    	String userId = sns.getUserId();
	    	BaseUser user = sns.showUser(userId);

	    	final LocalAccountDao accountDao = new LocalAccountDao(context);

	    	if (accountDao.isExists(sp, user.getId())) {
				throw new YiBoException(YiBoException.ACCOUNT_IS_EXIST);
			}

			account = new LocalAccount();
			account.setAuthorization(auth);
			account.setUser(user);
			account.setAuthSecret(String.valueOf(accessToken.getRefreshToken()));
			account.setState(Account.STATE_ADDED);
			account.setAppKey(oauthHelper.getConsumerKey());
			account.setAppSecret(oauthHelper.getConsumerSecret());
			account.setCreatedAt(new Date());
			accountDao.add(account);

			GlobalVars.addAccount(account);

			isSuccess = true;
		} catch (LibException e) {
			message = ResourceBook.getStatusCodeValue(e.getExceptionCode(), context);
        	if (Constants.DEBUG) {
        		Log.d(TAG, "ErrorCode:" + e.getExceptionCode(), e);
        	}
		} catch (YiBoException e) {
			message = ResourceBook.getStatusCodeValue(e.getStatusCode(), context);
        	if (Constants.DEBUG) {
        		Log.d(TAG, "ErrorCode:" + e.getStatusCode(), e);
        	}
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
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        }
    }
}
