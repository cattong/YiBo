package net.dev123.yibo;

import net.dev123.commons.ServiceProvider;
import net.dev123.commons.http.auth.OAuth2AuthorizeHelper;
import net.dev123.commons.oauth.OAuth;
import net.dev123.commons.oauth.config.OAuthConfiguration;
import net.dev123.commons.oauth.config.OAuthConfigurationFactory;
import net.dev123.commons.oauth2.OAuth2;
import net.dev123.commons.oauth2.OAuth2.GrantType;
import net.dev123.exception.LibException;
import net.dev123.yibo.common.Constants;
import net.dev123.yibo.common.theme.ThemeUtil;
import net.dev123.yibo.db.ConfigSystemDao;
import net.dev123.yibo.service.adapter.AppKeySpinnerAdapter;
import net.dev123.yibo.service.adapter.SpSpinnerAdapter;
import net.dev123.yibo.service.listener.AccountLoginClickListener;
import net.dev123.yibo.service.listener.AccountSpItemSelectedListener;
import net.dev123.yibo.service.listener.GoBackClickListener;
import net.dev123.yibo.service.task.OAuth2RetrieveAccessTokenTask;
import net.dev123.yibo.service.task.OAuthRetrieveAccessTokenTask;
import net.dev123.yibo.service.task.OAuthRetrieveRequestTokenTask;
import net.dev123.yibo.service.task.VerifyTimeTask;
import net.dev123.yibome.entity.ConfigApp;
import net.dev123.yibome.entity.Passport;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class AddAccountActivity extends BaseActivity {
	private static final String TAG = AddAccountActivity.class.getSimpleName();
	private ServiceProvider spSelected;
	private ConfigApp appSelected;
	private boolean isCustomKeyLevel;

	private Spinner spAppKey;
	private Spinner spServiceProvider;
	private EditText etUsername;
	private EditText etPassword;
	private CheckBox cbMakeDefault;
	private CheckBox cbFollowOffical;
	private CheckBox cbUserCustomKey;
	private Button btnLogin;
	private Button btnReset;
	private Button btnAuthorize;
	private CheckBox cbUseProxy;
	private EditText etRestProxy;
	private EditText etSearchProxy;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.add_account);

		//默认不弹出输入法
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		initComponents();
		bindEvent();
		VerifyTimeTask verifyTimeTask = new VerifyTimeTask(this);
		verifyTimeTask.execute();
	}

	private void initComponents() {
    	LinearLayout llHeaderBase = (LinearLayout)findViewById(R.id.llHeaderBase);
    	ScrollView svContentPanel = (ScrollView)findViewById(R.id.svContentPanel);
    	spServiceProvider = (Spinner) findViewById(R.id.spServiceProvider);
    	spAppKey = (Spinner) findViewById(R.id.spAppKey);
    	etUsername = (EditText) findViewById(R.id.etUsername);
		etPassword = (EditText) findViewById(R.id.etPassword);
		cbMakeDefault = (CheckBox) findViewById(R.id.chkDefault);
		cbFollowOffical = (CheckBox) findViewById(R.id.chkFollowOffical);
		cbUseProxy = (CheckBox) findViewById(R.id.cbUseApiProxy);
		cbUserCustomKey = (CheckBox) findViewById(R.id.cbUseCustomKey);
		etRestProxy = (EditText) findViewById(R.id.etRestProxy);
		etSearchProxy = (EditText) findViewById(R.id.etSearchProxy);
		btnAuthorize = (Button) findViewById(R.id.btnAuthorize);
		LinearLayout llOAuthIntro = (LinearLayout)findViewById(R.id.llOAuthIntro);
		TextView tvOAuthIntro = (TextView)findViewById(R.id.tvOAuthIntro);

    	LinearLayout llFooterAction = (LinearLayout)findViewById(R.id.llFooterAction);
    	btnLogin = (Button)findViewById(R.id.btnLogin);
		btnReset = (Button)findViewById(R.id.btnReset);

    	ThemeUtil.setSecondaryHeader(llHeaderBase);
    	ThemeUtil.setContentBackground(svContentPanel);
    	spServiceProvider.setBackgroundDrawable(theme.getDrawable("selector_btn_dropdown"));
    	spAppKey.setBackgroundDrawable(theme.getDrawable("selector_btn_dropdown"));
    	int padding2 = theme.dip2px(2);
    	spServiceProvider.setPadding(padding2, padding2, padding2, padding2);
    	spAppKey.setPadding(padding2, padding2, padding2, padding2);
    	int content = theme.getColor("content");
    	etUsername.setBackgroundDrawable(theme.getDrawable("selector_input_frame"));
    	etUsername.setTextColor(content);
    	etPassword.setBackgroundDrawable(theme.getDrawable("selector_input_frame"));
    	etPassword.setTextColor(content);
    	cbMakeDefault.setButtonDrawable(theme.getDrawable("selector_checkbox"));
    	cbMakeDefault.setTextColor(content);
    	cbFollowOffical.setButtonDrawable(theme.getDrawable("selector_checkbox"));
    	cbFollowOffical.setTextColor(content);
    	cbUserCustomKey.setButtonDrawable(theme.getDrawable("selector_checkbox"));
    	cbUserCustomKey.setTextColor(content);
    	cbUseProxy.setButtonDrawable(theme.getDrawable("selector_checkbox"));
    	cbUseProxy.setTextColor(content);
    	etRestProxy.setBackgroundDrawable(theme.getDrawable("selector_input_frame"));
    	etRestProxy.setTextColor(content);
    	etSearchProxy.setBackgroundDrawable(theme.getDrawable("selector_input_frame"));
    	etSearchProxy.setTextColor(content);
    	ThemeUtil.setBtnActionPositive(btnAuthorize);

    	llOAuthIntro.setBackgroundDrawable(theme.getDrawable("bg_frame_normal"));
    	int padding8 = theme.dip2px(8);
    	llOAuthIntro.setPadding(padding8, padding8, padding8, padding8);
    	tvOAuthIntro.setTextColor(theme.getColor("quote"));

    	llFooterAction.setBackgroundDrawable(theme.getDrawable("bg_footer_action"));
    	llFooterAction.setPadding(padding8, padding8, padding8, padding8);
    	llFooterAction.setGravity(Gravity.CENTER);
    	ThemeUtil.setBtnActionPositive(btnLogin);
    	ThemeUtil.setBtnActionNegative(btnReset);

		TextView tvTitle = (TextView) this.findViewById(R.id.tvTitle);
		tvTitle.setText(getString(R.string.title_add_account));
		
		ConfigSystemDao configDao = new ConfigSystemDao(this);
		Passport passport = configDao.getPassport();
		if (passport != null) {
			isCustomKeyLevel = 
				passport.getPointLevel().getPoints() >= Constants.POINTS_CUSTOM_SOURCE_LEVEL ;
		}
		// isCustomKeyLevel = true; // 调试用
		if (isCustomKeyLevel) {
			LinearLayout llAppKey = (LinearLayout) findViewById(R.id.llAppKey);
			llAppKey.setVisibility(View.VISIBLE);
		}
	}

	private void bindEvent() {
		Button btnBack = (Button) this.findViewById(R.id.btnBack);
		btnBack.setOnClickListener(new GoBackClickListener(R.anim.slide_in_right));

		spServiceProvider.setAdapter(new SpSpinnerAdapter(this));
		AccountSpItemSelectedListener spItemListener = new AccountSpItemSelectedListener(this);
		spServiceProvider.setOnItemSelectedListener(spItemListener);
		
		spAppKey.setAdapter(new AppKeySpinnerAdapter(this));
		spAppKey.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				appSelected = (ConfigApp) spAppKey.getAdapter().getItem(position);
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				appSelected = null;
			}
			
		});
		
		cbUserCustomKey.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (cbUserCustomKey.isChecked()) {
					spAppKey.setVisibility(View.VISIBLE);
				} else {
					spAppKey.setVisibility(View.GONE);
				}
			}
		});
		
		etUsername.addTextChangedListener(editTextWatcher);
		etPassword.addTextChangedListener(editTextWatcher);
		etRestProxy.addTextChangedListener(editTextWatcher);
		etSearchProxy.addTextChangedListener(editTextWatcher);

		btnAuthorize.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (spSelected == null) {
					Toast.makeText(v.getContext(),
						R.string.msg_accounts_add_spSelect, Toast.LENGTH_LONG).show();
					return;
				}
				if (spSelected.isSns()) {
					try {
						Activity context = AddAccountActivity.this;
						OAuth2AuthorizeHelper authHelper = new OAuth2AuthorizeHelper(spSelected);
						if (isUseCustomAppKey() && appSelected != null) {
							authHelper.setConsumer(appSelected.getAppKey(), appSelected.getAppSecret());
						}
						OAuthConfiguration oauthConfig
							= OAuthConfigurationFactory.getOAuthConfiguration(spSelected);
						Intent intent = new Intent();
						intent.setClass(context, AuthorizeActivity.class);
						intent.putExtra("ServiceProvider", spSelected.toString());
						intent.putExtra("Authorize_Url",
							authHelper.getAuthrizationUrl(GrantType.AUTHORIZATION_CODE, null));
						intent.putExtra("Callback_Url", oauthConfig.getOAuthCallbackURL());
						context.startActivityForResult(intent,
							Constants.REQUEST_CODE_OAUTH_AUTHORIZE);
					} catch (LibException e) {
						if (Constants.DEBUG) {
							Log.d(TAG, e.getMessage());
						}
					}
					
				} else {
					new OAuthRetrieveRequestTokenTask(
							AddAccountActivity.this, spSelected,
							cbMakeDefault.isChecked(), cbFollowOffical.isChecked()).execute();
				}
			}
		});

		AccountLoginClickListener loginListener = new AccountLoginClickListener(this);
		btnLogin.setOnClickListener(loginListener);

		btnReset.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				reset();
			}
		});

	}

	/**
	 * 重置
	 */
	private void reset() {
		etUsername.setText("");
		etPassword.setText("");
		spServiceProvider.setSelection(0);
		btnLogin.setEnabled(false);
		cbMakeDefault.setChecked(false);
		cbFollowOffical.setChecked(true);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		setIntent(intent);
	}

	public static void saveNewAccountId(SharedPreferences settings, long accountId) {
		SharedPreferences.Editor editor = settings.edit();
		editor.putLong(Constants.PREFS_KEY_ACCOUNT_ADDED, accountId);
		editor.commit();
	}

	public void updateLoginButton() {
		boolean enabled = false;
		enabled = etUsername.getText().length() > 0 && etPassword.getText().length() > 0;
		if (spSelected == ServiceProvider.Twitter && cbUseProxy.isChecked()) {
			enabled = enabled && etRestProxy.getText().length() > 0;
		}
		btnLogin.setEnabled(enabled);
	}

	TextWatcher editTextWatcher = new TextWatcher() {
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {

		}

		@Override
		public void afterTextChanged(Editable s) {
			updateLoginButton();
		}
	};

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == Constants.REQUEST_CODE_OAUTH_AUTHORIZE
			&& resultCode == Constants.RESULT_CODE_SUCCESS) {

			String spStr = data.getStringExtra("ServiceProvider");
			ServiceProvider sp = ServiceProvider.valueOf(spStr);
			if (sp == null) {
				return ;
			}
			if (spSelected.isSns()) {
				String code = data.getStringExtra(OAuth2.CODE);
				OAuth2RetrieveAccessTokenTask oauth2TokenTask
			    	= new OAuth2RetrieveAccessTokenTask(AddAccountActivity.this);
				oauth2TokenTask.execute(code, spStr);
			} else {
				String verifier = data.getStringExtra(OAuth.OAUTH_VERIFIER);
				String token = data.getStringExtra(OAuth.OAUTH_TOKEN);
				OAuthRetrieveAccessTokenTask task
				    = new OAuthRetrieveAccessTokenTask(AddAccountActivity.this);
				task.execute(token, verifier, spStr);
			}

		}
	};

	public ServiceProvider getSpSelected() {
		return spSelected;
	}

	public void setSpSelected(ServiceProvider spSelected) {
		this.spSelected = spSelected;
	}

	public boolean isCustomKeyLevel() {
		return isCustomKeyLevel;
	}

	public ConfigApp getAppSelected() {
		return appSelected;
	}

	public void setAppSelected(ConfigApp appSelected) {
		this.appSelected = appSelected;
	}
	
	public boolean isUseCustomAppKey() {
		return this.cbUserCustomKey.isChecked();
	}

}
