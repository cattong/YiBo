package net.dev123.yibo.service.task;

import java.util.Date;

import net.dev123.commons.ServiceProvider;
import net.dev123.commons.http.auth.OAuthAuthorization;
import net.dev123.commons.http.auth.OAuthAuthorizeHelper;
import net.dev123.commons.oauth.OAuthAccessToken;
import net.dev123.commons.oauth.config.OAuthConfiguration;
import net.dev123.commons.oauth.config.OAuthConfigurationFactory;
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
import android.widget.Button;
import android.widget.Toast;

public class XAuthTask extends AsyncTask<Void, Void, Boolean> {
	private static final String TAG = "XAuthTask";

	private AddAccountActivity context;
	private ServiceProvider spSelected;
	private boolean isMakeDefault;
	private boolean isFollowOffical;
	private OAuthConfiguration conf;

	private ProgressDialog progressDialog;

	private String password;
	private String username;
	private LocalAccount account;

	private String message;
	public XAuthTask(AddAccountActivity context, String username, String password, ServiceProvider sp,
			boolean isMakeDefault, boolean isFollowOffical) {
		this.context = context;
		this.username = username;
		this.password = password;
		this.spSelected = sp;
		this.isMakeDefault = isMakeDefault;
		this.isFollowOffical = isFollowOffical;
		this.conf = OAuthConfigurationFactory.getOAuthConfiguration(spSelected);
		if (conf == null) {
			throw new NullPointerException("Can not find the Configuration for " + spSelected);
		}
	}

	@Override
	protected void onPreExecute() {
    	Button btnLogin = (Button)context.findViewById(R.id.btnLogin);
    	btnLogin.setEnabled(false);

		progressDialog = ProgressDialog.show(context, "",
			context.getString(R.string.msg_retrieving_authorized_token), true, false);
	    progressDialog.setOwnerActivity(context);
	}

	@Override
	protected Boolean doInBackground(Void... arg) {
		boolean isSuccess= false;
		OAuthAccessToken accessToken = null;
		try {
			if (NetUtil.isCMWAP()
				&& (URLUtil.isHttpsUrl(conf.getOAuthRequestTokenURL())
				  	|| URLUtil.isHttpsUrl(conf.getOAuthAccessTokenURL()))) {
				throw new YiBoException(YiBoException.NET_HTTPS_UNDER_CMWAP);
			}

			OAuthAuthorizeHelper authHelper = new OAuthAuthorizeHelper(spSelected);
			if (context.isUseCustomAppKey() && context.getAppSelected() != null) {
				authHelper.setConsumer(context.getAppSelected().getAppKey(), 
					context.getAppSelected().getAppSecret());
			}
			
			switch (spSelected) {
			case NetEase:
				if(username.indexOf("@") < 0){
					username += "@163.com"; //如果用户没输入完整的邮件地址，则自动追加 @163.com
				}
				//直落
			case Sina:
			case Sohu:
			case Fanfou:
			case Twitter:
				accessToken = authHelper.retrieveOAuthAccessToken(username, password);
				break;
			default:
				break;
			}

			if (accessToken == null) {
				throw new YiBoException(YiBoException.AUTH_TOKEN_IS_NULL);
			}

			if (Constants.DEBUG) {
				Log.d(TAG, accessToken.toString());
			}

			OAuthAuthorization auth = new OAuthAuthorization(accessToken.getToken(), accessToken.getTokenSecret(), spSelected);
			auth.setConsumerKey(authHelper.getConsumerKey());
			auth.setConsumerSecret(authHelper.getConsumerSecret());
			MicroBlog mBlog = MicroBlogFactory.getInstance(auth);

			final LocalAccountDao accountDao = new LocalAccountDao(context);

			User user = mBlog.verifyCredentials();

			if (accountDao.isExists(spSelected, user.getId())) {
				throw new YiBoException(YiBoException.ACCOUNT_IS_EXIST);
			}

			if (accountDao.findAllValid() == null) {
				isMakeDefault = true;
			}

			account = new LocalAccount();
			account.setAuthorization(auth);
			account.setUser(user);
			account.setState(Account.STATE_ADDED);
			account.setAppKey(authHelper.getConsumerKey());
			account.setAppSecret(authHelper.getConsumerSecret());
			account.setCreatedAt(new Date());
			accountDao.add(account);

			if (isMakeDefault) {
				accountDao.makeDefault(account);
			}

			GlobalVars.addAccount(account);

			isSuccess = true;
		} catch (LibException e) {
			if (Constants.DEBUG) {
				Log.d(TAG, message, e);
			}
			message = ResourceBook.getStatusCodeValue(e.getExceptionCode(), context);
		} catch (YiBoException e) {
			if (Constants.DEBUG) {
				Log.d(TAG, message, e);
			}
			message = ResourceBook.getStatusCodeValue(e.getStatusCode(), context);
		}

		return isSuccess;
	}

	protected void onPostExecute(Boolean result) {
		if (progressDialog != null
				&& progressDialog.isShowing()) {
			try {
			    progressDialog.dismiss();
			} catch(Exception e){}
		}

    	Button btnLogin = (Button) context.findViewById(R.id.btnLogin);
    	btnLogin.setEnabled(true);

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
			Toast.makeText(context, message, Toast.LENGTH_LONG).show();
		}
	}

}
