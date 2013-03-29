package com.shejiaomao.weibo.service.listener;

import java.util.regex.Matcher;

import com.shejiaomao.maobo.R;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.Spannable;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.cattong.commons.Logger;
import com.cattong.commons.util.StringUtil;
import com.cattong.weibo.Emotions;
import com.shejiaomao.weibo.activity.EditCommentActivity;
import com.shejiaomao.weibo.common.Constants;
import com.shejiaomao.weibo.common.EmotionLoader;

public class MicroBlogTextWatcher implements TextWatcher {	
	private Context context = null;
	private Button btnTextCount = null;
	private EditText etText = null;
	private int length = Constants.STATUS_TEXT_MAX_LENGTH;	

	public MicroBlogTextWatcher(Context context) {
		this.context = context;
		this.btnTextCount = (Button)((Activity)context).findViewById(R.id.btnTextCount);
		this.etText = (EditText)((Activity)context).findViewById(R.id.etText);
	}
	
	@Override
	public void afterTextChanged(Editable s) {
        length = StringUtil.getLengthByByte(s.toString());
        int leavings = (int)Math.floor((double)(Constants.STATUS_TEXT_MAX_LENGTH * 2 - length) / 2);
        btnTextCount.setText((leavings < 0 ? "-" : "") + Math.abs(leavings));
        
        //回复评论时，删除评论前缀（回复@somebody:)时，去掉回复评论.
        if (length == 0 
        	&& context instanceof EditCommentActivity) {
        	((EditCommentActivity)context).setRecomment(null);
        }
        
        String content = s.toString();;
		Matcher m = Emotions.NORMALIZED_PATTERN.matcher(content);
		
		Drawable drawable = null;
		ImageSpan span = null;
		String emotionKey = null;
    	int currentPos = etText.getSelectionStart();
		int start = 0;
		int end = 0;
        while (m.find()) {
        	emotionKey = m.group(0);
        	start = m.start();
        	end = m.end();
        	if ((currentPos > start && currentPos < end)) {
        		//光标在表情里或者找不到表情时，不设置表情
        		continue;
        	}
            drawable = EmotionLoader.getDrawableByEmontion(emotionKey);
            if (drawable == null) {
            	continue;
            }
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            span = new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);
            s.setSpan(span, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        
        if (Logger.isDebug()) {
        	Log.d(this.getClass().getSimpleName(), s.toString());
        }
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
        
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
        
	}

}
