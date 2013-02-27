package net.dev123.yibo.service.listener;

import net.dev123.commons.ServiceProvider;
import net.dev123.yibo.AddAccountActivity;
import net.dev123.yibo.R;
import net.dev123.yibo.service.adapter.AppKeySpinnerAdapter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class AccountSpItemSelectedListener implements OnItemSelectedListener {
    private AddAccountActivity context;

	private static ServiceProvider[] serviceProviders = {
		ServiceProvider.Sina,
		ServiceProvider.Tencent,
		ServiceProvider.Sohu,
		ServiceProvider.NetEase,
		ServiceProvider.Fanfou,
		ServiceProvider.Twitter,
		ServiceProvider.RenRen,
		ServiceProvider.KaiXin,
		ServiceProvider.QQZone
	};

    private LinearLayout llLoginForm;
	private LinearLayout llAuthorizeForm;
	private LinearLayout llFooterAction;
	private LinearLayout llOAuthIntro;

	private EditText etUsername;
	private EditText etPassword;
	private TextView tvOAuthIntro;
	private CheckBox cbUseProxy;
	private EditText etRestProxy;
	private EditText etSearchProxy;
	private CheckBox cbFollowOffical;
	private CheckBox cbMakeDefault;
	private Spinner spAppKey;
	private CheckBox cbUseCustomKey;

	public AccountSpItemSelectedListener(AddAccountActivity context) {
		this.context = context;

		llLoginForm = (LinearLayout) context.findViewById(R.id.llLoginForm);
		etUsername = (EditText) context.findViewById(R.id.etUsername);
		etPassword = (EditText) context.findViewById(R.id.etPassword);

		llAuthorizeForm = (LinearLayout) context.findViewById(R.id.llAuthorizeForm);

		llOAuthIntro = (LinearLayout) context.findViewById(R.id.llOAuthIntro);
		tvOAuthIntro = (TextView) context.findViewById(R.id.tvOAuthIntro);

		llFooterAction = (LinearLayout) context.findViewById(R.id.llFooterAction);
		cbUseProxy = (CheckBox) context.findViewById(R.id.cbUseApiProxy);
		etRestProxy = (EditText) context.findViewById(R.id.etRestProxy);
		etSearchProxy = (EditText) context.findViewById(R.id.etSearchProxy);
		cbFollowOffical = (CheckBox) context.findViewById(R.id.chkFollowOffical);
		cbMakeDefault = (CheckBox) context.findViewById(R.id.chkDefault);
		cbUseCustomKey = (CheckBox) context.findViewById(R.id.cbUseCustomKey);
		spAppKey = (Spinner) context.findViewById(R.id.spAppKey);
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		final ServiceProvider sp = serviceProviders[position];
		if (sp == null) {
			return;
		}
        context.setSpSelected(sp);
        
        cbUseCustomKey.setChecked(false);
        if (spAppKey != null && spAppKey.getAdapter() != null) {
        	AppKeySpinnerAdapter adapter = (AppKeySpinnerAdapter) spAppKey.getAdapter();
        	adapter.setServiceProvider(sp);
        }
        context.setAppSelected(null);
        spAppKey.setVisibility(View.GONE);

		switch (sp) {
		case Tencent:
			showOAuthAuthorizeForm(sp);
			break;
		case Twitter:
			showOAuthAuthorizeForm(sp);
			cbUseProxy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if (isChecked) {
						showLoginForm(sp);
						if (context.isCustomKeyLevel()) {
							cbUseCustomKey.setVisibility(View.GONE);
						}
					} else {
						showOAuthAuthorizeForm(sp);
						if (context.isCustomKeyLevel()) {
							cbUseCustomKey.setVisibility(View.VISIBLE);
						}
					}
				}
			});
			
			break;
		case RenRen:
		case KaiXin:
		case QQZone:
			showOAuthAuthorizeForm(sp);
			break;
		default:
			showLoginForm(sp);
			break;
		}

		context.updateLoginButton();

	}

	private void showLoginForm(ServiceProvider serviceProvider) {
		llAuthorizeForm.setVisibility(View.GONE);
		llOAuthIntro.setVisibility(View.GONE);

		llLoginForm.setVisibility(View.VISIBLE);
		llFooterAction.setVisibility(View.VISIBLE);
		etUsername.setText("");
		etPassword.setText("");

		if (serviceProvider == ServiceProvider.Twitter) {
			cbUseProxy.setVisibility(View.VISIBLE);
			etRestProxy.setVisibility(View.VISIBLE);
			etRestProxy.setText("");
			etSearchProxy.setVisibility(View.GONE);
		} else {
			cbUseProxy.setVisibility(View.GONE);
			etRestProxy.setVisibility(View.GONE);
			etSearchProxy.setVisibility(View.GONE);
		}
	}

	private void showOAuthAuthorizeForm(ServiceProvider serviceProvider) {
		llLoginForm.setVisibility(View.GONE);
		llFooterAction.setVisibility(View.GONE);

		llAuthorizeForm.setVisibility(View.VISIBLE);
		llOAuthIntro.setVisibility(View.VISIBLE);

		if (serviceProvider == ServiceProvider.Twitter) {
			cbUseProxy.setVisibility(View.VISIBLE);
		} else {
			cbUseProxy.setVisibility(View.GONE);
		}

		if (serviceProvider.getServiceProviderCategory().equals(ServiceProvider.CATEGORY_SNS)) {
			cbFollowOffical.setVisibility(View.GONE);
			cbMakeDefault.setVisibility(View.GONE);
		} else {
			cbFollowOffical.setVisibility(View.VISIBLE);
			cbMakeDefault.setVisibility(View.VISIBLE);
		}

		etRestProxy.setVisibility(View.GONE);
		etSearchProxy.setVisibility(View.GONE);

		tvOAuthIntro.setText(R.string.hint_accounts_oauth_intro_tencent);
		if (serviceProvider.isSns()) {
			tvOAuthIntro.append("\n\n");
			tvOAuthIntro.append(context.getText(R.string.hint_accounts_sns));
		}

	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {

	}

}
