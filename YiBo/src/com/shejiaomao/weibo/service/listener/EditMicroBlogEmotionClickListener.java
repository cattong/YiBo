package com.shejiaomao.weibo.service.listener;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;

import com.shejiaomao.weibo.service.adapter.EmotionsGridAdapter;
import com.shejiaomao.weibo.widget.EmotionViewController;

public class EditMicroBlogEmotionClickListener implements OnClickListener {
    private Context context;
    private EmotionViewController emotionViewController;
    
	public EditMicroBlogEmotionClickListener(Context context) {
		this.context = context;
		emotionViewController = new EmotionViewController((Activity)context);
		
		EmotionsGridAdapter emotionsGridAdapter = new EmotionsGridAdapter(context);
		
		emotionViewController.setEmotionGridViewAdapter(emotionsGridAdapter);
    	EditMicroBlogEmotionItemClickListener itemClickListener =
            new EditMicroBlogEmotionItemClickListener(context);
    	emotionViewController.setEmotionGridViewOnItemClickListener(itemClickListener);
	}
	
	@Override
	public void onClick(View v) {
		if (emotionViewController.getEmotionViewVisibility() == View.VISIBLE) {
			emotionViewController.hideEmotionView();
        } else {
            InputMethodManager imm =
            	(InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        	imm.hideSoftInputFromWindow(
    			((Activity) context).getCurrentFocus().getWindowToken(),
				InputMethodManager.HIDE_NOT_ALWAYS);
        	emotionViewController.showEmotionView();
        }
	}
}
