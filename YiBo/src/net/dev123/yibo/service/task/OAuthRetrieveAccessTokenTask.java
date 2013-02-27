package net.dev123.yibo.service.task;

import java.util.Date;

import net.dev123.commons.ServiceProvider;
import net.dev123.commons.http.auth.OAuthAuthorization;
import net.dev123.commons.http.auth.OAuthAuthorizeHelper;
import net.dev123.commons.oauth.OAuthAccessToken;
import net.dev123.commons.oauth.OAuthRequestToken;
import net.dev123.commons.oauth.config.OAuthConfiguration;
import net.dev123.commons.oauth.config.OAuthConfigurationFactory;
import net.dev123.commons.util.StringUtil;
import net.dev123.exception.LibException;
import net.dev123.mblog.MicroBlog;
import net.dev123.mblog.MicroBlogFactory;
import net.dev123.mblog.entity.User;
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

public class OAuthRetrieveAccessTokenTask extends AsyncTask<String, Void, Boolean> {
	private static final String TAG = OAuthRetrieveAccessTokenTask.class.getSimpleName();

	private AddAccountActivity context;
	private OAuthConfiguration conf;

    private MicroBlog mBlog;
    private ServiceProvider sp;
    private LocalAccount account;
    private boolean isMakeDefault;
    private boolean isFollowOffical;

    private String message;
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
    	String token = data[0];
    	String verifier = data[1];
    	String spString = data[2];
    	sp = ServiceProvider.valueOf(spString);
    	conf = OAuthConfigurationFactory.getOAuthConfiguration(sp);
    	if (sp == null || conf == null) {
    		return isSuccess;
    	}

		try {
			if (NetUtil.isCMWAP()
				&& URLUtil.isHttpsUrl(conf.getOAuthAccessTokenURL())) {
				throw new YiBoException(YiBoException.NET_HTTPS_UNDER_CMWAP);
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
				throw new YiBoException(YiBoException.TOKEN_MISMATCH);
			}

			// 使用OAuth Verifier 及 RequestToken 换取 AccessToken
			OAuthAuthorizeHelper authorizeHelper = new OAuthAuthorizeHelper(sp);
			if (context.isUseCustomAppKey() && context.getAppSelected() != null) {
				authorizeHelper.setConsumer(context.getAppSelected().getAppKey(), 
					context.getAppSelected().getAppSecret());
			}
			OAuthRequestToken requestToken = new OAuthRequestToken(token, tokenSecret);
			OAuthAccessToken accessToken = authorizeHelper.retrieveOAuthAccessToken(requestToken, verifier);

	    	if(Constants.DEBUG) {
	    		Log.d(TAG, accessToken.toString());
	    	}

	    	OAuthAuthorization auth = new OAuthAuthorization(accessToken, sp);
			auth.setConsumerKey(authorizeHelper.getConsumerKey());
			auth.setConsumerSecret(authorizeHelper.getConsumerSecret());
	    	mBlog = MicroBlogFactory.getInstance(auth);
	    	User user = mBlog.verifyCredentials();

	    	final LocalAccountDao accountDao = new LocalAccountDao(context);

	    	if (accountDao.isExists(sp, user.getId())) {
				throw new YiBoException(YiBoException.ACCOUNT_IS_EXIST);
			}

			if (accountDao.findAllValid() == null) {
				isMakeDefault = true;
			}

			account = new LocalAccount();
			account.setAuthorization(auth);
			account.setUser(user);
			account.setState(Account.STATE_ADDED);
			account.setAppKey(authorizeHelper.getConsumerKey());
			account.setAppSecret(authorizeHelper.getConsumerSecret());
			account.setCreatedAt(new Date());
			accountDao.add(account);

			if(isMakeDefault){
				accountDao.makeDefault(account);
			}

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
    		&& progressDialog.getContext()!= null) {
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
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        }
    }
}
