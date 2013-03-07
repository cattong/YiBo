package com.shejiaomao.weibo.service.listener;

import android.view.View;

import com.shejiaomao.weibo.activity.EditCommentActivity;
import com.shejiaomao.weibo.widget.EmotionViewController;

/**
 * @author Weiping Ye
 * @version 创建时间：2011-8-30 下午5:48:40
 **/
public class EditCommentEmotionClickListener extends
		EditMicroBlogEmotionClickListener {
	private EditCommentActivity editCommentActivity;
	private EmotionViewController emotionViewController;
	public EditCommentEmotionClickListener(EditCommentActivity context) {
		super(context);
		this.editCommentActivity = context;
		this.emotionViewController = new EmotionViewController(context);
	}
	
	@Override
	public void onClick(View v) {
		if (emotionViewController.getEmotionViewVisibility() == View.VISIBLE) {
			editCommentActivity.displayOptions(true);
		} else {
			editCommentActivity.displayOptions(false);
		}
		
		//基类表情控制
        super.onClick(v);
	}
}
