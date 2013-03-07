package com.shejiaomao.weibo.service.listener;

import android.view.View;
import android.webkit.WebView;

public class ImageWebViewDoubleClickListener extends OnDoubleClickListener {
    private boolean isZoomIn = true;
    
	@Override
	public void onDoubleClick(View v) {
		if (!(v instanceof WebView)) {
			return;
		}

		WebView wv = (WebView)v;
		if (isZoomIn) {
			wv.zoomIn();
		} else {
			wv.zoomOut();
		}
		isZoomIn = !isZoomIn;
	}

}
