package com.shejiaomao.weibo.activity;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shejiaomao.maobo.R;
import com.shejiaomao.weibo.SheJiaoMaoApplication;
import com.shejiaomao.weibo.common.theme.ThemeUtil;
import com.shejiaomao.weibo.service.listener.ImageViewerSaveClickListener;
import com.umeng.analytics.MobclickAgent;

public class ImageWebViewerActivity extends Activity {
	@SuppressWarnings("unused")
	private static final String TAG = ImageWebViewerActivity.class.getSimpleName();
    private static final int INTERNAL_DISTANCE = 8;
    private static String WEB_HTML = //"<img style=\"max-width: %1$d px;\" src=\"%2$s\"/>";
	    "<table style=height:100%%;width:100%%;margin:0;padding:0;border:0;>" +
 	    "<tr>" +
 		     "<td style=\"vertical-align:middle;text-align:center;\">" +
 			     "<img style=\"max-width:%1$dpx;\" src=\"%2$s\" />" +
 		     "</td>" +
 	    "</tr>" +
        "</table>";

    boolean isGif = false;
    private String realPath;
	private String imageUrl;
	private WebView webViewer;

	private int orientation;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.image_web_viewer);

		TextView tvTitle = (TextView) this.findViewById(R.id.tvTitle);
		tvTitle.setText(R.string.title_image_viewer);

		orientation = this.getResources().getConfiguration().orientation;

		initComponent(this.getIntent());
		bindEvent();
	}

	private void initComponent(Intent intent) {
		LinearLayout llHeaderBase = (LinearLayout)findViewById(R.id.llHeaderBase);
		ThemeUtil.setSecondaryImageHeader(llHeaderBase);
		
		if (intent == null) {
			return;
		}

		Bundle bundle = intent.getExtras();
		realPath = bundle.getString("image-path");
		imageUrl = Uri.fromFile(new File(realPath)).toString();

		String html = String.format(WEB_HTML, getMaxWidth(), imageUrl);

		webViewer = (WebView) findViewById(R.id.wvImageViewer);
	    webViewer.getSettings().setSupportZoom(true);
	    webViewer.getSettings().setBuiltInZoomControls(true);
	    webViewer.setBackgroundColor(Color.BLACK);
	    webViewer.setVerticalScrollBarEnabled(false);
	    webViewer.setHorizontalScrollBarEnabled(false);
        //webViewer.setOnTouchListener(new ImageWebViewDoubleClickListener());
        
	    webViewer.loadDataWithBaseURL("", html, "text/html", "UTF-8", "");
	}

	private void bindEvent() {
		Button back = (Button) this.findViewById(R.id.btnBack);
		back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				((Activity) v.getContext()).finish();
			}
		});

		Button btnSave = (Button) findViewById(R.id.btnOperate);
		btnSave.setVisibility(View.VISIBLE);
		btnSave.setText(R.string.btn_save);
		ImageViewerSaveClickListener saveListener =
			new ImageViewerSaveClickListener(realPath);
		btnSave.setOnClickListener(saveListener);
	}

	private int getMaxWidth() {
		int maxWidth = 0;
		if (orientation == Configuration.ORIENTATION_PORTRAIT) {
			maxWidth = SheJiaoMaoApplication.getDisplayWidth();
		} else {
			maxWidth = SheJiaoMaoApplication.getDisplayHeight();
		}
		maxWidth = maxWidth * DisplayMetrics.DENSITY_DEFAULT / 
		    SheJiaoMaoApplication.getDensityDpi() - INTERNAL_DISTANCE * 2;
		return maxWidth;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		orientation = newConfig.orientation;
		String html = String.format(WEB_HTML, getMaxWidth(), imageUrl);
		webViewer.loadDataWithBaseURL("", html, "text/html", "UTF-8", "");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (webViewer != null) {
			webViewer.setVisibility(View.GONE);
		    webViewer.clearCache(true);
		    webViewer.clearHistory();
		    webViewer.destroy();
		}
	}

	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.ECLAIR
            && keyCode == KeyEvent.KEYCODE_BACK
            && event.getRepeatCount() == 0) {
            // Take care of calling this method on earlier versions of
            // the platform where it doesn't exist.
            onBackPressed();
        }

        return super.onKeyDown(keyCode, event);
    }
	
    @Override
    public void onBackPressed() {
        // This will be called either automatically for you on 2.0
        // or later, or by the code above on earlier versions of the
        // platform.
    	finish();
    }

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
}
