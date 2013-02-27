package net.dev123.yibo.service.task;

import net.dev123.commons.ServiceProvider;
import net.dev123.commons.http.auth.OAuthAuthorizeHelper;
import net.dev123.commons.oauth.OAuthRequestToken;
import net.dev123.commons.oauth.config.OAuthConfiguration;
import net.dev123.commons.oauth.config.OAuthConfigurationFactory;
import net.dev123.exception.LibException;
import net.dev123.yibo.AddAccountActivity;
import net.dev123.yibo.AuthorizeActivity;
import net.dev123.yibo.R;
import net.dev123.yibo.common.Constants;
import net.dev123.yibo.common.NetUtil;
import net.dev123.yibo.common.ResourceBook;
import net.dev123.yibo.common.YiBoException;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.webkit.URLUtil;
import android.widget.Toast;

public class OAuthRetrieveRequestTokenTask extends AsyncTask<Void, Void, Boolean> {
	private static final String TAG = OAuthRetrieveRequestTokenTask.class.getSimpleName();

	private AddAccountActivity context;
	private ServiceProvider spSelected;
	private boolean makeDefault;
	private boolean followOffical;

	private OAuthConfiguration conf;
    private ProgressDialog progressDialog;

    private String message;
    public OAuthRetrieveRequestTokenTask(AddAccountActivity context,ServiceProvider sp,
    	boolean makeDefault, boolean followOffical) {
    	this.context = context;
    	this.spSelected = sp;
    	this.makeDefault = makeDefault;
    	this.followOffical = followOffical;
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
    	conf = OAuthConfigurationFactory.getOAuthConfiguration(spSelected);
        if (conf == null) {
        	return isSuccess;
        }

		try {
			if (NetUtil.isCMWAP()
				&& URLUtil.isHttpsUrl(conf.getOAuthRequestTokenURL())) {
				throw new YiBoException(YiBoException.NET_HTTPS_UNDER_CMWAP);
			}

			// 获取RequestToken和RequestTokenSecret
			OAuthAuthorizeHelper authorizeHelper = new OAuthAuthorizeHelper(spSelected);
			if (context.isUseCustomAppKey()&& context.getAppSelected() != null) {
				authorizeHelper.setConsumer(context.getAppSelected().getAppKey(), 
					context.getAppSelected().getAppSecret());
			}
			OAuthRequestToken requestToken = authorizeHelper.retrieveOAuthRequestToken();

			//网站认证跳转前保存获取的RequestToken 和 RequestTokenSecret 以及是否设为默认帐号 数据
			SharedPreferences.Editor editor =
				context.getSharedPreferences(
					Constants.PREFS_NAME_APP_TEMP, Activity.MODE_PRIVATE).edit();
			editor.putString(Constants.PREFS_KEY_OAUTH_TOKEN, requestToken.getToken());
			editor.putString(Constants.PREFS_KEY_OAUTH_TOKEN_SECRET, requestToken.getTokenSecret());
			editor.putBoolean(Constants.PREFS_KEY_MAKE_DEFAULT, makeDefault);
			editor.putBoolean(Constants.PREFS_KEY_FOLLOW_OFFICAL, followOffical);
			editor.commit();

			Intent intent = new Intent();
			intent.setClass(context, AuthorizeActivity.class);
			intent.putExtra("ServiceProvider", spSelected.toString());
			intent.putExtra("Authorize_Url", requestToken.getAuthorizationURL());
			intent.putExtra("Callback_Url", requestToken.getCallbackUrl());
			context.startActivityForResult(intent, Constants.REQUEST_CODE_OAUTH_AUTHORIZE);

			isSuccess = true;
		} catch (LibException e) {
			message = ResourceBook.getStatusCodeValue(e.getExceptionCode(), context);
			if(Constants.DEBUG) {
				Log.d(TAG, e.getMessage(), e);
			}
		} catch (YiBoException e) {
			message = ResourceBook.getStatusCodeValue(e.getStatusCode(), context);
			if(Constants.DEBUG) {
				Log.d(TAG, e.getMessage(), e);
			}
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

