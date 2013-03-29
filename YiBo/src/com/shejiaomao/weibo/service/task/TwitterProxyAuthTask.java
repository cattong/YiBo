package com.shejiaomao.weibo.service.task;

import java.util.Date;

import com.shejiaomao.maobo.R;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.cattong.commons.LibException;
import com.cattong.commons.Logger;
import com.cattong.commons.ServiceProvider;
import com.cattong.entity.Account;
import com.cattong.entity.User;
import com.cattong.weibo.Weibo;
import com.cattong.weibo.WeiboFactory;
import com.cattong.weibo.impl.twitter.ProxyBasicAuth;
import com.shejiaomao.common.ResourceBook;
import com.shejiaomao.weibo.activity.AddAccountActivity;
import com.shejiaomao.weibo.common.Constants;
import com.shejiaomao.weibo.common.SheJiaoMaoException;
import com.shejiaomao.weibo.db.LocalAccount;
import com.shejiaomao.weibo.db.LocalAccountDao;

public class TwitterProxyAuthTask extends AsyncTask<Void, Void, JSONObject> {

	private static final String TAG = "BasicAuthorizeTask";

	private AddAccountActivity context;

	private ServiceProvider spSelected;
	private boolean isMakeDefault;
	private String username;
	private String password;
	private String restProxy;
	private String searchProxy;
	private Weibo mBlog;
	private LocalAccount account;

    private ProgressDialog progressDialog;

    public TwitterProxyAuthTask(
    	AddAccountActivity context, String username, String paswword,
    	String restProxy, String searchProxy, boolean makeDefault ){
    	this.context = context;
    	this.username = username;
    	this.password = paswword;
    	this.restProxy = restProxy;
    	this.searchProxy = searchProxy;
    	this.spSelected = ServiceProvider.Twitter;
    	this.isMakeDefault = makeDefault;
    }

    @Override
    protected void onPreExecute() {
    	Button btnAuthorize = (Button)context.findViewById(R.id.btnAuthorize);
    	btnAuthorize.setEnabled(false);

    	progressDialog = ProgressDialog.show(context, null,
    		context.getString(R.string.msg_verifying), true, false);
    }

    @Override
    protected JSONObject doInBackground(Void... arg) {
    	JSONObject json = null;
    	ProxyBasicAuth auth = new ProxyBasicAuth(username, password, spSelected);
    	auth.setRestApiServer(restProxy);
    	auth.setSearchApiServer(searchProxy);

		mBlog = WeiboFactory.getInstance(auth);
		boolean succeeded = false;
		String message = "";
		try {
			final LocalAccountDao accountDao = new LocalAccountDao(context);

			User user = mBlog.verifyCredentials();

			if (accountDao.isExists(spSelected, user.getUserId())) {
				throw new SheJiaoMaoException(SheJiaoMaoException.ACCOUNT_IS_EXIST);
			}

			if (accountDao.findAllValid() == null) {
				isMakeDefault = true;
			}

			account = new LocalAccount();
			account.setAuthorization(auth);
			account.setUser(user);
			account.setState(Account.STATE_ACTIVE);
			account.setAppKey("NULL");
			account.setCreatedAt(new Date());
			account.setRestProxyUrl(restProxy);
			account.setSearchProxyUrl(searchProxy);
			accountDao.add(account);

			if(isMakeDefault){
				accountDao.makeDefault(account);
			}

			succeeded = true;
		} catch (LibException e) {
			if(Logger.isDebug()) Log.d(TAG, e.getMessage(),e);
            message = ResourceBook.getResultCodeValue(e.getErrorCode(), context);
		} catch (SheJiaoMaoException e) {
			if(Logger.isDebug()) Log.d(TAG, e.getMessage(),e);
			message = ResourceBook.getResultCodeValue(e.getStatusCode(), context);
		}

        try {
        	json = new JSONObject();
        	json.put("succeeded", succeeded);
        	json.put("message", message);
        } catch (JSONException e) {
        	if(Logger.isDebug()) Log.d(TAG,e.getMessage(), e);
        }
        return json;
    }

    protected void onPostExecute(JSONObject json) {
    	if (progressDialog != null &&
    		progressDialog.isShowing() &&
    		progressDialog.getContext() != null
    	) {
    		try {
                progressDialog.dismiss();
    		} catch(Exception e){}
    	}

    	Button btnAuthorize = (Button)context.findViewById(R.id.btnAuthorize);
    	btnAuthorize.setEnabled(true);

        if (json != null) {
            try {
                boolean succeeded = json.getBoolean("succeeded");
                String message = json.getString("message");

                if (!succeeded) {
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                }else{
                	AddAccountActivity.saveNewAccountId(context.getSharedPreferences(Constants.PREFS_NAME_APP_TEMP, Activity.MODE_PRIVATE), account.getAccountId());
    				context.finish();
                }
            } catch (JSONException e) {
                if(Logger.isDebug()) Log.d(TAG,e.getMessage(), e);
            }
        }
    }
}
