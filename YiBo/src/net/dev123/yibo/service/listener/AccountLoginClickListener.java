package net.dev123.yibo.service.listener;

import java.util.regex.Pattern;

import net.dev123.commons.ServiceProvider;
import net.dev123.yibo.AddAccountActivity;
import net.dev123.yibo.R;
import net.dev123.yibo.service.task.TwitterProxyAuthTask;
import net.dev123.yibo.service.task.XAuthTask;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class AccountLoginClickListener implements OnClickListener {
	private static final Pattern SCHEME_HOST_PATH_PATTERN =
		Pattern.compile("http[s]?://[a-z0-9-]+(\\.[a-z0-9-]+)+(/[\\w-]+)*[/]?");
	private static final Pattern HOST_PATH_PATTERN =
		Pattern.compile("[a-z0-9-]+(\\.[a-z0-9-]+)+(/[\\w-]+)*[/]?");

	private AddAccountActivity context;
	public AccountLoginClickListener(AddAccountActivity context) {
		this.context = context;
	}

	@Override
	public void onClick(View v) {
		ServiceProvider spSelected = context.getSpSelected();
		if (spSelected == null) {
			Toast.makeText(context, R.string.msg_accounts_add_spSelect, Toast.LENGTH_LONG).show();
			return;
		}

		EditText etUsername = (EditText) context.findViewById(R.id.etUsername);
		EditText etPassword = (EditText) context.findViewById(R.id.etPassword);
		CheckBox cbUseProxy = (CheckBox) context.findViewById(R.id.cbUseApiProxy);

		EditText etRestProxy = (EditText) context.findViewById(R.id.etRestProxy);
		EditText etSearchProxy = (EditText) context.findViewById(R.id.etSearchProxy);

		CheckBox cbMakeDefault = (CheckBox) context.findViewById(R.id.chkDefault);
		boolean isMakeDefault = cbMakeDefault.isChecked();

		CheckBox cbFollowOffical = (CheckBox) context.findViewById(R.id.chkFollowOffical);
		boolean isFollowOffical = cbFollowOffical.isChecked();

		String userName = etUsername.getText().toString().trim();
		String password = etPassword.getText().toString().trim();

		switch (spSelected) {
		case Sina:
		case NetEase:
		case Sohu:
		case Fanfou:
			new XAuthTask(
				context, userName, password,
				spSelected, isMakeDefault,
				isFollowOffical
			).execute();
			break;
		case Twitter:
			if (cbUseProxy.isChecked()) {
				String restApi = etRestProxy.getText().toString().trim().toLowerCase();
				if (!restApi.matches(SCHEME_HOST_PATH_PATTERN.toString())) {
					if (restApi.matches(HOST_PATH_PATTERN.toString())) {
						restApi = "http://" + restApi;
					} else {
						Toast.makeText(context,
							R.string.msg_accounts_add_invalid_proxy_url,
							Toast.LENGTH_SHORT).show();
						etRestProxy.requestFocus();
						break;
					}
				}
				String searchApi = etSearchProxy.getText().toString().trim();
				new TwitterProxyAuthTask(
					context, userName,  password,
					restApi, searchApi, isMakeDefault
				).execute();
			} else {
				new XAuthTask(
					context,    userName, password,
					spSelected, isMakeDefault, isFollowOffical
				).execute();
			}
			break;
		default:
			break;
		}
	}

}
