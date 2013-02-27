package net.dev123.yibo.service.listener;

import net.dev123.yibo.EditCommentActivity;
import net.dev123.yibo.widget.EmotionViewController;
import android.view.View;

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
