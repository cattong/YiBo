package net.dev123.yibo.service.task;

import java.util.Date;

import net.dev123.commons.ServiceProvider;
import net.dev123.commons.http.auth.Authorization;
import net.dev123.commons.http.auth.BasicAuthorization;
import net.dev123.exception.LibException;
import net.dev123.mblog.MicroBlog;
import net.dev123.mblog.MicroBlogFactory;
import net.dev123.mblog.entity.User;
import net.dev123.yibo.AddAccountActivity;
import net.dev123.yibo.R;
import net.dev123.yibo.common.Constants;
import net.dev123.yibo.common.GlobalVars;
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
import android.widget.Button;
import android.widget.Toast;

public class BasicAuthorizeTask extends AsyncTask<Void, Void, Boolean> {
	private static final String TAG = "BasicAuthorizeTask";

	private AddAccountActivity context;

	private ServiceProvider spSelected;
	private boolean isMakeDefault;
	private String username;
	private String password;
	private MicroBlog mBlog;
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
    	Button btnLogin = (Button) context.findViewById(R.id.btnLogin);
    	btnLogin.setEnabled(false);

    	progressDialog = ProgressDialog.show(context, null,
    		context.getString(R.string.msg_verifying), true, false);
    }

    @Override
    protected Boolean doInBackground(Void... arg) {
    	boolean isSuccess = false;
    	Authorization auth = new BasicAuthorization(username, password, spSelected);
		mBlog = MicroBlogFactory.getInstance(auth);

		try {
			User user = mBlog.verifyCredentials();

			final LocalAccountDao accountDao = new LocalAccountDao(context);
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
			account.setAppKey("NULL");
			account.setCreatedAt(new Date());
			accountDao.add(account);

			if (isMakeDefault) {
				accountDao.makeDefault(account);
			}

			GlobalVars.addAccount(account);

			isSuccess = true;
		} catch (LibException e) {
            message = ResourceBook.getStatusCodeValue(e.getExceptionCode(), context);
			if(Constants.DEBUG) Log.d(TAG, e.getMessage(),e);
		} catch(YiBoException e) {
			message = ResourceBook.getStatusCodeValue(e.getStatusCode(), context);
			if(Constants.DEBUG) Log.d(TAG, e.getMessage(),e);
		}

        return isSuccess;
    }

    protected void onPostExecute(Boolean result) {
    	if (progressDialog != null) {
    		try {
                progressDialog.dismiss();
    		} catch(Exception e){}
    	}

    	Button btnLogin = (Button) context.findViewById(R.id.btnLogin);
    	btnLogin.setEnabled(true);

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
