package net.dev123.yibo.service.listener;

import net.dev123.yibo.AccountsActivity;
import net.dev123.yibo.PassportActivity;
import net.dev123.yibo.R;
import net.dev123.yibo.common.CompatibilityUtil;
import net.dev123.yibo.common.Constants;
import net.dev123.yibo.db.ConfigSystemDao;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class AccountPassportClickListener implements OnClickListener {
    private AccountsActivity context;
    
	public AccountPassportClickListener(AccountsActivity context) {
		this.context = context;
	}
	
	@Override
	public void onClick(View v) {
		if (!context.isPassportLogin()) {
			Intent intent = new Intent();
			intent.setClass(context, PassportActivity.class);
			context.startActivityForResult(intent,
				Constants.REQUEST_CODE_PASSPORT_LOGIN);
			CompatibilityUtil.overridePendingTransition(context,
				R.anim.slide_in_left, android.R.anim.fade_out);
			
			return;
		}		

		new AlertDialog.Builder(context)
			.setTitle(R.string.title_dialog_alert)
			.setMessage(R.string.msg_passport_logout_confirm)
			.setPositiveButton(R.string.btn_confirm,
				new AlertDialog.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						try {
							ConfigSystemDao configSystemDao = context.getConfigSystemDao();
							configSystemDao.destroyPassport();
							context.setPassportLogin(false);
							context.initPassport();
						} catch (Exception e) {
							Log.e("AccountsActivity", e.getMessage(), e);
							Toast.makeText(context,
								R.string.msg_accounts_delete_failed,
								Toast.LENGTH_SHORT).show();
						}
					}
			})
			.setNegativeButton(R.string.btn_cancel,
				new AlertDialog.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
					}
			})
			.show();
			
	}

}
