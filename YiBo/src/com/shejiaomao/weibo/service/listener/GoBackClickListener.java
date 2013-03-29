package com.shejiaomao.weibo.service.listener;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;

import com.shejiaomao.common.CompatibilityUtil;
import com.shejiaomao.maobo.R;

public class GoBackClickListener implements OnClickListener {
	private int enterAnim = -1;
    public GoBackClickListener() {
    	enterAnim = R.anim.slide_in_left;//slide in from left
    }
    
    public GoBackClickListener(int backAnim) {
    	enterAnim = R.anim.slide_in_right;//slide in from right
    }
    
	@Override
	public void onClick(View v) {
		InputMethodManager inputMethodManager = (InputMethodManager)v.getContext().
            getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
        
        Activity activity = (Activity)v.getContext();
	    activity.finish();
	    if (enterAnim != -1) {
	    	CompatibilityUtil.overridePendingTransition(
	    		activity, enterAnim, android.R.anim.fade_out);
	    }
	}

}
