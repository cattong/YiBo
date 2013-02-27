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

		progressDialog = ProgressDialog.show(context, "", context.getString(R.string.msg_passport_login), true, false);
	    progressDialog.setOwnerActivity(context);
	}

	@Override
	protected Boolean doInBackground(Void... arg) {
		boolean isSuccess= false;
		try {
			passport = YiBoMe.login(username, password);
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
			Toast.makeText(context, R.string.msg_passport_login_success, Toast.LENGTH_LONG).show();
			context.setResult(Constants.RESULT_CODE_SUCCESS);
			context.finish();
		} else {
			Toast.makeText(context, message, Toast.LENGTH_LONG).show();
		}
	}

}
