package net.dev123.yibo.service.task;

import java.util.Date;

import net.dev123.commons.ServiceProvider;
import net.dev123.exception.LibException;
import net.dev123.mblog.MicroBlog;
import net.dev123.mblog.MicroBlogFactory;
import net.dev123.mblog.entity.User;
import net.dev123.mblog.twitter.ProxyBasicAuth;
import net.dev123.yibo.AddAccountActivity;
import net.dev123.yibo.R;
import net.dev123.yibo.common.Constants;
import net.dev123.yibo.common.ResourceBook;
import net.dev123.yibo.common.YiBoException;
import net.dev123.yibo.db.LocalAccount;
import net.dev123.yibo.db.LocalAccountDao;
import net.dev123.yibome.entity.Account;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

public class TwitterProxyAuthTask extends AsyncTask<Void, Void, JSONObject> {

	private static final String TAG = "BasicAuthorizeTask";

	private AddAccountActivity context;

	private ServiceProvider spSelected;
	private boolean isMakeDefault;
	private String username;
	private String password;
	private String restProxy;
	private String searchProxy;
	private MicroBlog mBlog;
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
    	Button btnLogin = (Button)context.findViewById(R.id.btnLogin);
    	btnLogin.setEnabled(false);

    	progressDialog = ProgressDialog.show(context, null,
    		context.getString(R.string.msg_verifying), true, false);
    }

    @Override
    protected JSONObject doInBackground(Void... arg) {
    	JSONObject json = null;
    	ProxyBasicAuth auth = new ProxyBasicAuth(username, password, spSelected);
    	auth.setRestApiServer(restProxy);
    	auth.setSearchApiServer(searchProxy);

		mBlog = MicroBlogFactory.getInstance(auth);
		boolean succeeded = false;
		String message = "";
		try {
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
			if(Constants.DEBUG) Log.d(TAG, e.getMessage(),e);
            message = ResourceBook.getStatusCodeValue(e.getExceptionCode(), context);
		} catch (YiBoException e) {
			if(Constants.DEBUG) Log.d(TAG, e.getMessage(),e);
			message = ResourceBook.getStatusCodeValue(e.getStatusCode(), context);
		}

        try {
        	json = new JSONObject();
        	json.put("succeeded", succeeded);
        	json.put("message", message);
        } catch (JSONException e) {
        	if(Constants.DEBUG) Log.d(TAG,e.getMessage(), e);
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

    	Button btnLogin = (Button)context.findViewById(R.id.btnLogin);
    	btnLogin.setEnabled(true);

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
                if(Constants.DEBUG) Log.d(TAG,e.getMessage(), e);
            }
        }
    }
}
