package com.shejiaomao.weibo.activity;

import com.shejiaomao.maobo.R;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.cattong.commons.ServiceProvider;
import com.cattong.commons.http.auth.Authorization;
import com.cattong.commons.oauth.OAuth;
import com.cattong.commons.oauth.OAuth2;
import com.cattong.entity.Passport;
import com.shejiaomao.weibo.BaseActivity;
import com.shejiaomao.weibo.common.Constants;
import com.shejiaomao.weibo.common.theme.ThemeUtil;
import com.shejiaomao.weibo.db.ConfigSystemDao;
import com.shejiaomao.weibo.service.adapter.ConfigAppSpinnerAdapter;
import com.shejiaomao.weibo.service.adapter.ServiceProviderSpinnerAdapter;
import com.shejiaomao.weibo.service.listener.AddAccountAuthorizeClickListener;
import com.shejiaomao.weibo.service.listener.AddAccountConfigAppItemSelectedListener;
import com.shejiaomao.weibo.service.listener.AddAccountSpItemSelectedListener;
import com.shejiaomao.weibo.service.listener.AddAccountTextWatcher;
import com.shejiaomao.weibo.service.listener.GoBackClickListener;
import com.shejiaomao.weibo.service.task.OAuth2RetrieveAccessTokenTask;
import com.shejiaomao.weibo.service.task.OAuthRetrieveAccessTokenTask;

public class AddAccountActivity extends BaseActivity {
	private boolean isCustomKeyLevel;
	
	private ServiceProvider sp;
	private Authorization auth;

	private Spinner spConfigApp;
	private Spinner spServiceProvider;
	private EditText etUsername;
	private EditText etPassword;
	private CheckBox cbMakeDefault;
	private CheckBox cbFollowOffical;
	
	private CheckBox cbUseProxy;
	private EditText etRestProxy;
	private EditText etSearchProxy;

	private Button btnAuthorize;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.add_account);

		//默认不弹出输入法
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		initComponents();
		bindEvent();
		
	}
	
	private void initComponents() {
    	LinearLayout llHeaderBase = (LinearLayout)findViewById(R.id.llHeaderBase);
    	ScrollView svContentPanel = (ScrollView)findViewById(R.id.svContentPanel);
    	spServiceProvider = (Spinner) findViewById(R.id.spServiceProvider);
    	spConfigApp = (Spinner) findViewById(R.id.spConfigApp);
    	etUsername = (EditText) findViewById(R.id.etUsername);
		etPassword = (EditText) findViewById(R.id.etPassword);
		cbMakeDefault = (CheckBox) findViewById(R.id.cbDefault);
		cbFollowOffical = (CheckBox) findViewById(R.id.cbFollowOffical);
		cbUseProxy = (CheckBox) findViewById(R.id.cbUseApiProxy);
		etRestProxy = (EditText) findViewById(R.id.etRestProxy);
		etSearchProxy = (EditText) findViewById(R.id.etSearchProxy);
		btnAuthorize = (Button) findViewById(R.id.btnAuthorize);
		LinearLayout llOAuthIntro = (LinearLayout)findViewById(R.id.llOAuthIntro);
		TextView tvOAuthIntro = (TextView)findViewById(R.id.tvOAuthIntro);

    	LinearLayout llFooterAction = (LinearLayout)findViewById(R.id.llFooterAction);

    	ThemeUtil.setSecondaryHeader(llHeaderBase);
    	ThemeUtil.setContentBackground(svContentPanel);
    	spServiceProvider.setBackgroundDrawable(theme.getDrawable("selector_btn_dropdown"));
    	spConfigApp.setBackgroundDrawable(theme.getDrawable("selector_btn_dropdown"));
    	int padding2 = theme.dip2px(2);
    	spServiceProvider.setPadding(padding2, padding2, padding2, padding2);
    	spConfigApp.setPadding(padding2, padding2, padding2, padding2);
    	int content = theme.getColor("content");
    	etUsername.setBackgroundDrawable(theme.getDrawable("selector_input_frame"));
    	etUsername.setTextColor(content);
    	etPassword.setBackgroundDrawable(theme.getDrawable("selector_input_frame"));
    	etPassword.setTextColor(content);
    	cbMakeDefault.setButtonDrawable(theme.getDrawable("selector_checkbox"));
    	cbMakeDefault.setTextColor(content);
    	cbFollowOffical.setButtonDrawable(theme.getDrawable("selector_checkbox"));
    	cbFollowOffical.setTextColor(content);
    	cbUseProxy.setButtonDrawable(theme.getDrawable("selector_checkbox"));
    	cbUseProxy.setTextColor(content);
    	etRestProxy.setBackgroundDrawable(theme.getDrawable("selector_input_frame"));
    	etRestProxy.setTextColor(content);
    	etSearchProxy.setBackgroundDrawable(theme.getDrawable("selector_input_frame"));
    	etSearchProxy.setTextColor(content);
    	

    	llOAuthIntro.setBackgroundDrawable(theme.getDrawable("bg_frame_normal"));
    	int padding8 = theme.dip2px(8);
    	llOAuthIntro.setPadding(padding8, padding8, padding8, padding8);
    	tvOAuthIntro.setTextColor(theme.getColor("quote"));

    	llFooterAction.setBackgroundDrawable(theme.getDrawable("bg_footer_action"));
    	llFooterAction.setPadding(padding8, padding8, padding8, padding8);
    	llFooterAction.setGravity(Gravity.CENTER);
    	ThemeUtil.setBtnActionPositive(btnAuthorize);
    	
		TextView tvTitle = (TextView) this.findViewById(R.id.tvTitle);
		tvTitle.setText(R.string.title_add_account);
		
		ConfigSystemDao configDao = new ConfigSystemDao(this);
		Passport passport = configDao.getPassport();
		if (passport != null) {
			isCustomKeyLevel = passport.getPointsLevel().getPoints() 
			    >= Constants.POINTS_CUSTOM_SOURCE_LEVEL ;
		}
		isCustomKeyLevel = true; // 调试用
		if (isCustomKeyLevel) {

		}
	}

	private void bindEvent() {
		Button btnBack = (Button) this.findViewById(R.id.btnBack);
		btnBack.setOnClickListener(new GoBackClickListener(R.anim.slide_in_right));

		spServiceProvider.setAdapter(new ServiceProviderSpinnerAdapter(this));
		OnItemSelectedListener spItemListener = new AddAccountSpItemSelectedListener(this);
		spServiceProvider.setOnItemSelectedListener(spItemListener);
		
		
		spConfigApp.setAdapter(new ConfigAppSpinnerAdapter(this));
		OnItemSelectedListener configAppItemSelectedListener = null;
		configAppItemSelectedListener = new AddAccountConfigAppItemSelectedListener(this);
		spConfigApp.setOnItemSelectedListener(configAppItemSelectedListener);
		
		TextWatcher editTextWatcher = new AddAccountTextWatcher(this);
		etUsername.addTextChangedListener(editTextWatcher);
		etPassword.addTextChangedListener(editTextWatcher);
		etRestProxy.addTextChangedListener(editTextWatcher);
		etSearchProxy.addTextChangedListener(editTextWatcher);

		AddAccountAuthorizeClickListener authorizeClickListener = null;
		authorizeClickListener = new AddAccountAuthorizeClickListener(this);
		btnAuthorize.setOnClickListener(authorizeClickListener);

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

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Constants.RESULT_CODE_SUCCESS) {
			this.resetAuthToken();
			return;
		}
		
		if (requestCode == Constants.REQUEST_CODE_CONFIG_APP_ADD) {
			ConfigAppSpinnerAdapter adapter = (ConfigAppSpinnerAdapter)spConfigApp.getAdapter();
			adapter.setServiceProvider(this.getSp());
			return;
		}
		
		if (requestCode == Constants.REQUEST_CODE_OAUTH_AUTHORIZE) {

			String spStr = data.getStringExtra("ServiceProvider");
			ServiceProvider sp = ServiceProvider.valueOf(spStr);
			if (sp == null) {
				return ;
			}
			if (sp.isSns() 
				|| auth.getoAuthConfig().getAuthVersion() == Authorization.AUTH_VERSION_OAUTH_2) {
				String code = data.getStringExtra(OAuth2.CODE);
				OAuth2RetrieveAccessTokenTask oauth2TokenTask
			    	= new OAuth2RetrieveAccessTokenTask(AddAccountActivity.this);
				oauth2TokenTask.execute(code, spStr);
			} else {
				String verifier = data.getStringExtra(OAuth.OAUTH_VERIFIER);
				String token = data.getStringExtra(OAuth.OAUTH_TOKEN);
				OAuthRetrieveAccessTokenTask task
				    = new OAuthRetrieveAccessTokenTask(this);
				task.execute(token, verifier, spStr);
			}
		}
	};

	public ServiceProvider getSp() {
		return sp;
	}

	public void setSp(ServiceProvider sp) {
		this.sp = sp;
		if (sp == null) {
			return;
		}
		
		auth = new Authorization(sp);
	}

	public boolean isCustomKeyLevel() {
		return isCustomKeyLevel;
	}


	public void resetAuthToken() {
		if (auth != null) {
			auth.setAccessToken(null);
			auth.setAccessSecret(null);
		}
	}
	
	public void resetAuthConfigApp() {
		if (sp != null) {
			auth = new Authorization(sp);
		}
	}
	
	public Authorization getAuth() {
		return auth;
	}

}
