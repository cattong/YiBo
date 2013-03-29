package com.shejiaomao.weibo.service.listener;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

import com.cattong.commons.ServiceProvider;
import com.shejiaomao.maobo.R;
import com.shejiaomao.weibo.activity.AddAccountActivity;
import com.shejiaomao.weibo.service.adapter.ConfigAppSpinnerAdapter;

public class AddAccountSpItemSelectedListener implements OnItemSelectedListener {
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

    private LinearLayout llXAuthForm;
	private LinearLayout llOAuthIntro;

	private EditText etUsername;
	private EditText etPassword;
	private TextView tvOAuthIntro;
	private CheckBox cbUseProxy;
	private EditText etRestProxy;
	private EditText etSearchProxy;
	private CheckBox cbFollowOffical;
	private CheckBox cbMakeDefault;
	private Spinner spConfigApp;
	private Button btnAuthorize;
	
	public AddAccountSpItemSelectedListener(AddAccountActivity context) {
		this.context = context;

		llXAuthForm = (LinearLayout) context.findViewById(R.id.llXAuthForm);
		etUsername = (EditText) context.findViewById(R.id.etUsername);
		etPassword = (EditText) context.findViewById(R.id.etPassword);

		llOAuthIntro = (LinearLayout) context.findViewById(R.id.llOAuthIntro);
		tvOAuthIntro = (TextView) context.findViewById(R.id.tvOAuthIntro);

		cbUseProxy = (CheckBox) context.findViewById(R.id.cbUseApiProxy);
		etRestProxy = (EditText) context.findViewById(R.id.etRestProxy);
		etSearchProxy = (EditText) context.findViewById(R.id.etSearchProxy);
		cbFollowOffical = (CheckBox) context.findViewById(R.id.cbFollowOffical);
		cbMakeDefault = (CheckBox) context.findViewById(R.id.cbDefault);
		spConfigApp = (Spinner) context.findViewById(R.id.spConfigApp);
		btnAuthorize = (Button) context.findViewById(R.id.btnAuthorize);
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		final ServiceProvider sp = serviceProviders[position];
		if (sp == null) {
			return;
		}
		
        context.setSp(sp);
        if (spConfigApp != null && spConfigApp.getAdapter() != null) {
        	ConfigAppSpinnerAdapter adapter = (ConfigAppSpinnerAdapter) spConfigApp.getAdapter();
        	adapter.setServiceProvider(sp);
        }

        showForm(sp);

	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {

	}
	
	private void showForm(final ServiceProvider sp) {
		switch (sp) {
		case Sina:
		case Tencent:
			showOAuthForm(sp);
			break;
		case Twitter:
			showOAuthForm(sp);
			cbUseProxy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if (isChecked) {
						showXAuthForm(sp);						
					} else {
						showOAuthForm(sp);						
					}
				}
			});
			
			break;
		case RenRen:
		case KaiXin:
		case QQZone:
			showOAuthForm(sp);
			break;
		default:
			showXAuthForm(sp);
			break;
		}
	}
	
	private void showXAuthForm(ServiceProvider serviceProvider) {
		llOAuthIntro.setVisibility(View.GONE);
		llXAuthForm.setVisibility(View.VISIBLE);

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
		
		btnAuthorize.setEnabled(false);
	}

	private void showOAuthForm(ServiceProvider serviceProvider) {
		llXAuthForm.setVisibility(View.GONE);

		llOAuthIntro.setVisibility(View.VISIBLE);

		if (serviceProvider == ServiceProvider.Twitter) {
			cbUseProxy.setVisibility(View.VISIBLE);
		} else {
			cbUseProxy.setVisibility(View.GONE);
		}

		if (serviceProvider.getSpCategory().equals(ServiceProvider.CATEGORY_SNS)) {
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

		btnAuthorize.setEnabled(true);
	}

}
