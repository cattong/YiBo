package com.shejiaomao.weibo.activity;

import com.shejiaomao.maobo.R;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cattong.commons.ServiceProvider;
import com.cattong.commons.http.auth.Authorization;
import com.cattong.entity.ConfigApp;
import com.shejiaomao.weibo.BaseActivity;
import com.shejiaomao.weibo.common.Constants;
import com.shejiaomao.weibo.common.theme.ThemeUtil;
import com.shejiaomao.weibo.db.ConfigAppDao;
import com.shejiaomao.weibo.service.listener.GoBackClickListener;

public class AddConfigAppActivity extends BaseActivity {

	private EditText etAppName;
	private EditText etAppKey;
	private EditText etAppSecret;
	private EditText etCallbackUrl;

	private Button btnFooterActionSubmit;
	private Button btnFooterActionReset;

	private ServiceProvider sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.add_config_app);

		initParams();
		initCompoments();
		bindEvent();
	}

	private void initParams() {
		Intent intent = this.getIntent();
		int spNo = intent.getIntExtra("spNo", ServiceProvider.Sina.getSpNo());
		sp = ServiceProvider.getServiceProvider(spNo);
	}
	
	private void initCompoments() {
		TextView tvTitle = (TextView) findViewById(R.id.tvTitle);
		tvTitle.setText(R.string.title_add_config_app);
		
		LinearLayout llRoot = (LinearLayout)findViewById(R.id.llRoot);
    	LinearLayout llHeaderBase = (LinearLayout)findViewById(R.id.llHeaderBase);

    	etAppName = (EditText) findViewById(R.id.etAppName);
		etAppKey = (EditText) findViewById(R.id.etAppKey);
		etAppSecret = (EditText) findViewById(R.id.etAppSecret);
		etCallbackUrl = (EditText) findViewById(R.id.etCallbackUrl);
		etCallbackUrl.setVisibility(View.GONE);
		Authorization auth = new Authorization(sp);
		if (auth.getAuthVersion() == Authorization.AUTH_VERSION_OAUTH_2) {
			etCallbackUrl.setVisibility(View.VISIBLE);
		}
		
		LinearLayout llFooterAction = (LinearLayout)findViewById(R.id.llFooterAction);
		btnFooterActionSubmit = (Button) findViewById(R.id.btnFooterActionSubmit);
		btnFooterActionReset = (Button) findViewById(R.id.btnFooterActionReset);
		
    	ThemeUtil.setSecondaryHeader(llHeaderBase);
    	ThemeUtil.setRootBackground(llRoot);
    	int content = theme.getColor("content");
    	etAppName.setBackgroundDrawable(theme.getDrawable("selector_input_frame"));
    	etAppName.setTextColor(content);
    	etAppKey.setBackgroundDrawable(theme.getDrawable("selector_input_frame"));
    	etAppKey.setTextColor(content);
    	etAppSecret.setBackgroundDrawable(theme.getDrawable("selector_input_frame"));
    	etAppSecret.setTextColor(content);
    	etCallbackUrl.setBackgroundDrawable(theme.getDrawable("selector_input_frame"));
    	etCallbackUrl.setTextColor(content);
		
    	llFooterAction.setBackgroundDrawable(theme.getDrawable("bg_footer_action"));
    	int padding8 = theme.dip2px(8);
    	llFooterAction.setPadding(padding8, padding8, padding8, padding8);
    	llFooterAction.setGravity(Gravity.CENTER);
    	ThemeUtil.setBtnActionPositive(btnFooterActionSubmit);
    	ThemeUtil.setBtnActionNegative(btnFooterActionReset);
    	
	}

	private void bindEvent() {
		Button btnBack = (Button) this.findViewById(R.id.btnBack);
		btnBack.setOnClickListener(new GoBackClickListener(R.anim.slide_in_right));

		TextWatcher textWatcher = new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) { }

			@Override
			public void afterTextChanged(Editable s) {
				updateFormButton();
			}
		};
		etAppName.addTextChangedListener(textWatcher);
		etAppKey.addTextChangedListener(textWatcher);
		etAppSecret.addTextChangedListener(textWatcher);
		etCallbackUrl.addTextChangedListener(textWatcher);

		btnFooterActionSubmit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
                ConfigAppDao configAppDao = new ConfigAppDao(v.getContext());
                ConfigApp configApp = new ConfigApp();
                configApp.setServiceProvider(sp);
                configApp.setAppName(etAppName.getText().toString());
                configApp.setAppKey(etAppKey.getText().toString());
                configApp.setAppSecret(etAppSecret.getText().toString());
                configApp.setCallbackUrl(etCallbackUrl.getText().toString());
                configAppDao.save(configApp);
                
                AddConfigAppActivity.this.setResult(Constants.RESULT_CODE_SUCCESS);
                AddConfigAppActivity.this.finish();
			}
		});

		btnFooterActionReset.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				etAppName.setText("");
				etAppKey.setText("");
				etAppSecret.setText("");
				etCallbackUrl.setText("");
			}
		});
	}

	private void updateFormButton() {
		boolean enabled = etAppName.getText().length() > 0
						&& etAppKey.getText().length() > 0
						&& etAppSecret.length() > 0;

		if (etCallbackUrl.getVisibility() == View.VISIBLE) {
		    enabled = enabled && etCallbackUrl.getText().length() > 0;
		}

		btnFooterActionSubmit.setEnabled(enabled);
	}

}
