package net.dev123.yibo.service.task;

import net.dev123.exception.LibException;
import net.dev123.yibo.PassportActivity;
import net.dev123.yibo.R;
import net.dev123.yibo.common.Constants;
import net.dev123.yibo.common.ResourceBook;
import net.dev123.yibo.db.ConfigSystemDao;
import net.dev123.yibome.YiBoMe;
import net.dev123.yibome.entity.Passport;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

public class PassportRegisterTask extends AsyncTask<Void, Void, Boolean> {
	private static final String TAG = PassportRegisterTask.class.getSimpleName();

	private PassportActivity context;
	private ProgressDialog progressDialog;

	private String username;
	private String password;
	private String passwordConfirm;
	private String email;
	private Passport passport;
	private Button btnSubmit;

	private String message;

	public PassportRegisterTask(PassportActivity context, String username,
			String password, String passwordConfirm, String email) {
		this.context = context;
		this.username = username;
		this.password = password;
		this.passwordConfirm = passwordConfirm;
		this.email = email;
	}

	@Override
	protected void onPreExecute() {
    	btnSubmit = (Button)context.findViewById(R.id.btnPassportFormSubmit);
    	btnSubmit.setEnabled(false);

		progressDialog = ProgressDialog.show(context, "", context.getString(R.string.msg_passport_signup), true, false);
	    progressDialog.setOwnerActivity(context);
	}

	@Override
	protected Boolean doInBackground(Void... arg) {
		boolean isSuccess= false;
		try {
			passport = YiBoMe.register(username, password, passwordConfirm, email);
			isSuccess = true;
		} catch (LibException e) {
			if (Constants.DEBUG) {
				Log.d(TAG, e.getMessage());
			}
			message = ResourceBook.getStatusCodeValue(e.getExceptionCode(), context);
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

    	btnSubmit.setEnabled(true);

		if (result) {
			ConfigSystemDao configDao = new ConfigSystemDao(context);
			configDao.savePassport(passport);
			Toast.makeText(context, R.string.msg_passport_signup_success, Toast.LENGTH_LONG).show();
			context.setResult(Constants.RESULT_CODE_SUCCESS);
			context.finish();
		} else {
			Toast.makeText(context, message, Toast.LENGTH_LONG).show();
		}
	}

}
