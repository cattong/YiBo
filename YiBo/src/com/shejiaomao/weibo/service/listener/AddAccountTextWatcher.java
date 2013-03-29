package com.shejiaomao.weibo.service.listener;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

import com.shejiaomao.maobo.R;
import com.shejiaomao.weibo.activity.AddAccountActivity;

public class AddAccountTextWatcher implements TextWatcher {
	private AddAccountActivity context;
	
	public AddAccountTextWatcher(AddAccountActivity context) {
		this.context = context;
	}
	
	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {

	}

	@Override
	public void afterTextChanged(Editable s) {
		boolean isEnabled = false;
		EditText etUsername = (EditText) context.findViewById(R.id.etUsername);
		EditText etPassword = (EditText) context.findViewById(R.id.etPassword);
		isEnabled = etUsername.getText().length() > 0 && etPassword.getText().length() > 0;
//		if (sp == ServiceProvider.Twitter && cbUseProxy.isChecked()) {
//			isEnabled = isEnabled && etRestProxy.getText().length() > 0;
//		}
		
		Button btnAuthorize = (Button) context.findViewById(R.id.btnAuthorize);
		btnAuthorize.setEnabled(isEnabled);
	}

}
