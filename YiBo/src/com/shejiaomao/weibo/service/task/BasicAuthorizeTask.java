package com.shejiaomao.weibo.service.task;

import java.util.Date;

import com.shejiaomao.maobo.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.cattong.commons.LibException;
import com.cattong.commons.Logger;
import com.cattong.commons.ServiceProvider;
import com.cattong.commons.http.auth.Authorization;
import com.cattong.entity.Account;
import com.cattong.entity.User;
import com.cattong.weibo.Weibo;
import com.cattong.weibo.WeiboFactory;
import com.shejiaomao.common.ResourceBook;
import com.shejiaomao.weibo.activity.AddAccountActivity;
import com.shejiaomao.weibo.common.Constants;
import com.shejiaomao.weibo.common.GlobalVars;
import com.shejiaomao.weibo.common.SheJiaoMaoException;
import com.shejiaomao.weibo.db.LocalAccount;
import com.shejiaomao.weibo.db.LocalAccountDao;

public class BasicAuthorizeTask extends AsyncTask<Void, Void, Boolean> {
	private static final String TAG = "BasicAuthorizeTask";

	private AddAccountActivity context;

	private ServiceProvider spSelected;
	private boolean isMakeDefault;
	private String username;
	private String password;
	private Weibo mBlog;
	private LocalAccount account;

    private ProgressDialog progressDialog;

    private String message;
    public BasicAuthorizeTask(
    	AddAccountActivity context, String username, String paswword,
    	ServiceProvider sp, boolean makeDefault) {
    	this.context = context;
    	this.username = username;
    	this.password = paswword;
    	this.spSelected = sp;
    	this.isMakeDefault = makeDefault;
    }

    @Override
    protected void onPreExecute() {
    	Button btnAuthorize = (Button) context.findViewById(R.id.btnAuthorize);
    	btnAuthorize.setEnabled(false);

    	progressDialog = ProgressDialog.show(context, null,
    		context.getString(R.string.msg_verifying), true, false);
    }

    @Override
    protected Boolean doInBackground(Void... arg) {
    	boolean isSuccess = false;
    	//Authorization auth = new BasicAuthorization(username, password, spSelected);
    	Authorization auth = new Authorization(spSelected);
    	auth.setAccessToken(username);
    	auth.setAccessSecret(password);
    	
		mBlog = WeiboFactory.getInstance(auth);

		try {
			User user = mBlog.verifyCredentials();

			final LocalAccountDao accountDao = new LocalAccountDao(context);
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
			accountDao.add(account);

			if (isMakeDefault) {
				accountDao.makeDefault(account);
			}

			GlobalVars.addAccount(account);

			isSuccess = true;
		} catch (LibException e) {
            message = ResourceBook.getResultCodeValue(e, context);
			if(Logger.isDebug()) Log.d(TAG, e.getMessage(),e);
		} catch(SheJiaoMaoException e) {
			message = ResourceBook.getResultCodeValue(e.getStatusCode(), context);
			if(Logger.isDebug()) Log.d(TAG, e.getMessage(),e);
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
        	SharedPreferences preference = context.getSharedPreferences(
        		Constants.PREFS_NAME_APP_TEMP, Activity.MODE_PRIVATE);
        	AddAccountActivity.saveNewAccountId(preference, account.getAccountId());
			context.setResult(Constants.RESULT_CODE_SUCCESS);
        	context.finish();
        } else {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        }

    }
}
