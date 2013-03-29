package com.shejiaomao.weibo.activity;

import java.net.URI;
import java.util.Map;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.cattong.commons.Logger;
import com.cattong.commons.ServiceProvider;
import com.cattong.commons.http.auth.Authorization;
import com.cattong.commons.http.auth.OAuth2AuthorizeHelper;
import com.cattong.commons.oauth.OAuth;
import com.cattong.commons.oauth.OAuth2;
import com.cattong.commons.util.StringUtil;
import com.cattong.commons.util.UrlUtil;
import com.shejiaomao.weibo.BaseActivity;
import com.shejiaomao.maobo.R;
import com.shejiaomao.weibo.common.Constants;

public class AuthorizeActivity extends BaseActivity {
	private static final String IPHONE_USERAGENT =
        "Mozilla/5.0 (iPhone; U; CPU iPhone OS 3_0 like Mac OS X; en-us)"
        + " AppleWebKit/528.18 (KHTML, like Gecko) Version/4.0"
        + " Mobile/7A341 Safari/528.16";

	private Authorization auth;
	private String authorizeUrl;
	private String callbackUrl;
	private ServiceProvider serviceProvider;

	private WebView wvAuthorize;
	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_PROGRESS);
		
		setContentView(R.layout.authorize);
		initComponents();
	}

	private void initComponents() {
		Intent intent = getIntent();

		auth = (Authorization)intent.getSerializableExtra("Authorization");
		serviceProvider = auth.getServiceProvider();
		authorizeUrl = intent.getStringExtra("Authorize_Url");
		callbackUrl = intent.getStringExtra("Callback_Url");
		String spStr = intent.getStringExtra("ServiceProvider");
		serviceProvider = ServiceProvider.valueOf(spStr);

		wvAuthorize = (WebView) findViewById(R.id.wvAuthorize);
		WebSettings settings = wvAuthorize.getSettings();
		settings.setBuiltInZoomControls(true);
		settings.setJavaScriptEnabled(true);
		settings.setSupportZoom(true);
		settings.setUserAgentString(IPHONE_USERAGENT);

		WebViewClient wvClient = new WebViewClient() {

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}

			@Override
			public void onReceivedSslError(WebView view,
					SslErrorHandler handler, SslError error) {
				handler.proceed();
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				if (StringUtil.isEmpty(url)) {
					return;
				}
				
				if (StringUtil.isEquals(url, authorizeUrl)) {
					progressDialog = ProgressDialog.show(
				    	view.getContext(), null,
				    	view.getContext().getString(R.string.label_loading),
				    	true, true);
				} else if (url.indexOf(callbackUrl) == 0) {
					Intent intent = new Intent();
					intent.putExtra("ServiceProvider", serviceProvider.toString());
					if (serviceProvider.isSns()
						|| auth.getoAuthConfig().getAuthVersion() == Authorization.AUTH_VERSION_OAUTH_2) {
						String code = OAuth2AuthorizeHelper.retrieveAuthorizationCodeFromQueryString(url);
						intent.putExtra(OAuth2.CODE, code);
					} else {
						Map<String, String> parameters =
							UrlUtil.extractQueryStringParameters(URI.create(url));
						String oauthToken = parameters.get(OAuth.OAUTH_TOKEN);
						String verifier = parameters.get(OAuth.OAUTH_VERIFIER);
						intent.putExtra(OAuth.OAUTH_TOKEN, oauthToken);
						intent.putExtra(OAuth.OAUTH_VERIFIER, verifier);
					}

					AuthorizeActivity.this.setResult(Constants.RESULT_CODE_SUCCESS, intent);
					AuthorizeActivity.this.finish();
				}
			}

		};

		wvAuthorize.setWebViewClient(wvClient);
		wvAuthorize.setWebChromeClient(new WebChromeClient() {

			@Override
			public void onProgressChanged(WebView view, int progress) {
				if (progress == 100) {
					if (progressDialog != null && progressDialog.isShowing()) {
						try {
							progressDialog.dismiss();
						} catch (Exception e) {
							Logger.error("error", e);
						}
					}
				}
				AuthorizeActivity.this.setProgress(progress * 100);
            }

			@Override
			public void onReceivedTitle(WebView view, String title) {
				super.onReceivedTitle(view, title);
				if (StringUtil.isNotEmpty(title)) {
					AuthorizeActivity.this.setTitle(title);
				}
			}
		});

		wvAuthorize.loadUrl(authorizeUrl);
	}

	@Override
	public void finish() {
		// 清空授权后留下的Cookie
		CookieManager cookieManager = CookieManager.getInstance();
		cookieManager.removeAllCookie();
		super.finish();
	}

}
