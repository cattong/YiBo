package net.dev123.yibo;

import java.net.URI;
import java.util.Map;

import net.dev123.commons.ServiceProvider;
import net.dev123.commons.http.auth.OAuth2AuthorizeHelper;
import net.dev123.commons.oauth.OAuth;
import net.dev123.commons.oauth2.OAuth2;
import net.dev123.commons.util.StringUtil;
import net.dev123.commons.util.UrlUtil;
import net.dev123.yibo.common.Constants;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class AuthorizeActivity extends BaseActivity {

	private static final String TAG = AuthorizeActivity.class.getSimpleName();
	private static final String IPHONE_USERAGENT =
        "Mozilla/5.0 (iPhone; U; CPU iPhone OS 3_0 like Mac OS X; en-us)"
        + " AppleWebKit/528.18 (KHTML, like Gecko) Version/4.0"
        + " Mobile/7A341 Safari/528.16";

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
					if (serviceProvider.isSns()) {
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
							if (Constants.DEBUG) {
								Log.d(TAG, e.getMessage(), e);
							}
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
