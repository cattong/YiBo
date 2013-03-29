package com.shejiaomao.weibo.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shejiaomao.weibo.BaseActivity;
import com.shejiaomao.maobo.R;
import com.shejiaomao.weibo.common.theme.ThemeUtil;
import com.shejiaomao.weibo.service.listener.GoBackClickListener;
import com.shejiaomao.weibo.service.task.PassportLoginTask;
import com.shejiaomao.weibo.service.task.PassportRegisterTask;
import com.shejiaomao.widget.TabButton;

public class PassportActivity extends BaseActivity {

	private Button btnLoginTab;
	private Button btnRegisterTab;
	private TabButton tabButton;

	private EditText etUsername;
	private EditText etPassword;
	private EditText etPasswordConfirmed;
	private EditText etEmail;

	private Button btnFormSubmit;
	private Button btnFormReset;

	private boolean isRegister;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.passport);

		TextView tvTitle = (TextView) findViewById(R.id.tvTitle);
		tvTitle.setText(R.string.title_passport);
		initCompoments();
		bindEvent();
	}

	private void initCompoments() {
		LinearLayout llRoot = (LinearLayout)findViewById(R.id.llRoot);
    	LinearLayout llHeaderBase = (LinearLayout)findViewById(R.id.llHeaderBase);
    	LinearLayout llTabHeader = (LinearLayout)findViewById(R.id.llTabHeader);
    	btnLoginTab = (Button) findViewById(R.id.btnTabLeft);
		btnRegisterTab = (Button) findViewById(R.id.btnTabRight);
		etUsername = (EditText) findViewById(R.id.etPassportUsername);
		etPassword = (EditText) findViewById(R.id.etPassportPassword);
		etPasswordConfirmed = (EditText) findViewById(R.id.etPassportPasswordConfirmed);
		etEmail = (EditText) findViewById(R.id.etPassportEmail);
		
		LinearLayout llFooterAction = (LinearLayout)findViewById(R.id.llFooterAction);
		btnFormSubmit = (Button) findViewById(R.id.btnPassportFormSubmit);
		btnFormReset = (Button) findViewById(R.id.btnPassportFormReset);
		
    	ThemeUtil.setSecondaryHeader(llHeaderBase);
    	ThemeUtil.setRootBackground(llRoot);
    	ThemeUtil.setHeaderToggleTab(llTabHeader);
    	int content = theme.getColor("content");
    	etUsername.setBackgroundDrawable(theme.getDrawable("selector_input_frame"));
    	etUsername.setTextColor(content);
    	etPassword.setBackgroundDrawable(theme.getDrawable("selector_input_frame"));
    	etPassword.setTextColor(content);
    	etPasswordConfirmed.setBackgroundDrawable(theme.getDrawable("selector_input_frame"));
    	etPasswordConfirmed.setTextColor(content);
    	etEmail.setBackgroundDrawable(theme.getDrawable("selector_input_frame"));
    	etEmail.setTextColor(content);
		
    	llFooterAction.setBackgroundDrawable(theme.getDrawable("bg_footer_action"));
    	int padding8 = theme.dip2px(8);
    	llFooterAction.setPadding(padding8, padding8, padding8, padding8);
    	llFooterAction.setGravity(Gravity.CENTER);
    	ThemeUtil.setBtnActionPositive(btnFormSubmit);
    	ThemeUtil.setBtnActionNegative(btnFormReset);
    	
    	btnLoginTab.setText(R.string.label_passport_login);
    	btnRegisterTab.setText(R.string.label_passport_register);
	}

	private void bindEvent() {
		Button btnBack = (Button) this.findViewById(R.id.btnBack);
		btnBack.setOnClickListener(new GoBackClickListener(R.anim.slide_in_right));

		tabButton = new TabButton();
		tabButton.addButton(btnLoginTab);
		tabButton.addButton(btnRegisterTab);

		btnLoginTab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				isRegister = false;
				updateFormView();
			}
		});
		btnRegisterTab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				isRegister = true;
				updateFormView();
			}
		});

		TextWatcher textWatcher = new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) { }

			@Override
			public void afterTextChanged(Editable s) {
				updateLoginButton();
			}
		};
		etUsername.addTextChangedListener(textWatcher);
		etPassword.addTextChangedListener(textWatcher);
		etPasswordConfirmed.addTextChangedListener(textWatcher);
		etEmail.addTextChangedListener(textWatcher);

		btnFormSubmit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isRegister) {
					new PassportRegisterTask(
							PassportActivity.this,
							etUsername.getText().toString(),
							etPassword.getText().toString(),
							etPasswordConfirmed.getText().toString(),
							etEmail.getText().toString()
						).execute();
				} else {
					new PassportLoginTask(
							PassportActivity.this,
							etUsername.getText().toString(),
							etPassword.getText().toString()
						).execute();
				}
			}
		});

		btnFormReset.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				etUsername.setText("");
				etPassword.setText("");
				etPasswordConfirmed.setText("");
				etEmail.setText("");
			}
		});

		updateFormView();
	}

	private void updateFormView() {
		if (isRegister) {
			tabButton.toggleButton(btnRegisterTab);
			etUsername.setHint(R.string.hint_passport_register_username);
			etPassword.setHint(R.string.hint_passport_register_password);
			etPasswordConfirmed.setVisibility(View.VISIBLE);
			etEmail.setVisibility(View.VISIBLE);
		} else {
			tabButton.toggleButton(btnLoginTab);
			etUsername.setHint(R.string.hint_passport_login_username);
			etPassword.setHint(R.string.hint_passport_login_password);
			etPasswordConfirmed.setVisibility(View.GONE);
			etEmail.setVisibility(View.GONE);
		}
		etUsername.setText("");
		etPassword.setText("");
		etPasswordConfirmed.setText("");
		etEmail.setText("");
	}

	private void updateLoginButton() {
		boolean enabled = etUsername.getText().length() > 0
							&& etPassword.getText().length() > 0;
		if (isRegister) {
			enabled = enabled && etPasswordConfirmed.length() > 0
						&& etEmail.getText().length() > 0;
		}

		btnFormSubmit.setEnabled(enabled);
	}

}
