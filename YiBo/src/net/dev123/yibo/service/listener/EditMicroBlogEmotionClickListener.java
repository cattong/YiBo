package net.dev123.yibo.service.listener;

import net.dev123.commons.Constants;
import net.dev123.yibo.service.adapter.EmotionsGridAdapter;
import net.dev123.yibo.service.task.EmotionUpdateTask;
import net.dev123.yibo.widget.EmotionViewController;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;

public class EditMicroBlogEmotionClickListener implements OnClickListener {
    private Context context;
    private EmotionViewController emotionViewController;
    private static boolean isRunEmotionUpdateTask = true;
    
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
        	
        	if (isRunEmotionUpdateTask) {
        		isRunEmotionUpdateTask = false;
        		if (Constants.DEBUG) Log.d(this.getClass().getSimpleName(), "run emotion update task");
        		EmotionUpdateTask emotiontask = new EmotionUpdateTask();
        		emotiontask.execute();
        	}
        }
	}
}
