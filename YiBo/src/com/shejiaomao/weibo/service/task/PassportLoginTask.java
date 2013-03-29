package com.shejiaomao.weibo.service.task;

import com.shejiaomao.maobo.R;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.Button;
import android.widget.Toast;

import com.cattong.entity.Passport;
import com.shejiaomao.weibo.activity.PassportActivity;
import com.shejiaomao.weibo.common.Constants;
import com.shejiaomao.weibo.db.ConfigSystemDao;

public class PassportLoginTask extends AsyncTask<Void, Void, Boolean> {
	private static final String TAG = PassportLoginTask.class.getSimpleName();

	private PassportActivity context;
	private ProgressDialog progressDialog;

	private String username;
	private String password;

	private Passport passport;
	private String message;

	private Button btnSubmit;

	public PassportLoginTask(PassportActivity context,
			String username, String password) {
		this.context = context;
		this.username = username;
		this.password = password;
	}

	@Override
	protected void onPreExecute() {
    	btnSubmit = (Button)context.findViewById(R.id.btnPassportFormSubmit);
    	btnSubmit.setEnabled(false);

    	String msgPassportLogin = context.getString(R.string.msg_passport_login);
		progressDialog = ProgressDialog.show(context, "", msgPassportLogin, true, false);
	    progressDialog.setOwnerActivity(context);
	}

	@Override
	protected Boolean doInBackground(Void... arg) {
		boolean isSuccess= false;
		
//		SocialCat socialCat = Util.getSocialCat(context);
//		try {
//			passport = socialCat.login(username, password);
//			isSuccess = true;
//		} catch (LibException e) {
//			if (Constants.DEBUG) {
//				Log.d(TAG, e.getMessage());
//			}
//			message = ResourceBook.getResultCodeValue(e.getErrorCode(), context);
//		}
		
		return isSuccess;
	}

	protected void onPostExecute(Boolean result) {
		if (progressDialog != null
				&& progressDialog.isShowing()) {
			try {
			    progressDialog.dismiss();
			} catch(Exception e){}
		}

    	btnSubmit.setEnabled(true);

		if (result) {
			ConfigSystemDao configDao = new ConfigSystemDao(context);
			configDao.savePassport(passport);
			Toast.makeText(context, R.string.msg_passport_login_success, Toast.LENGTH_LONG).show();
			context.setResult(Constants.RESULT_CODE_SUCCESS);
			context.finish();
		} else {
			Toast.makeText(context, message, Toast.LENGTH_LONG).show();
		}
	}

}
