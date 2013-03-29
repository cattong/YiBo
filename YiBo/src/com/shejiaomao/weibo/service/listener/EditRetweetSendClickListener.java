package com.shejiaomao.weibo.service.listener;

import com.shejiaomao.maobo.R;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.cattong.commons.util.StringUtil;
import com.shejiaomao.weibo.SheJiaoMaoApplication;
import com.shejiaomao.weibo.activity.EditRetweetActivity;
import com.shejiaomao.weibo.common.Constants;
import com.shejiaomao.weibo.db.LocalAccount;
import com.shejiaomao.weibo.service.task.RetweetTask;
import com.shejiaomao.weibo.service.task.UpdateCommentTask;

public class EditRetweetSendClickListener implements OnClickListener {
	private EditRetweetActivity context;

	private LocalAccount currentAccount;
	public EditRetweetSendClickListener(EditRetweetActivity context) {
		this.context = context;

		SheJiaoMaoApplication sheJiaoMao = (SheJiaoMaoApplication)context.getApplication();
		currentAccount = sheJiaoMao.getCurrentAccount();
	}

	@Override
	public void onClick(View v) {
		EditText edText = (EditText)context.findViewById(R.id.etText);
		String text = edText.getText().toString().trim();
		if (StringUtil.isEmpty(text) 
			&& edText.getHint() != null 
			&& !edText.getHint().equals("")) {
			text = edText.getHint().toString();
		}
		if (StringUtil.isEmpty(text)) {
        	Toast.makeText(v.getContext(), R.string.msg_blog_empty, Toast.LENGTH_SHORT).show();
			return;
		}
		int byteLen = StringUtil.getLengthByByte(text);
		if (byteLen > Constants.STATUS_TEXT_MAX_LENGTH * 2) {			
			text = StringUtil.subStringByByte(text, 0, Constants.STATUS_TEXT_MAX_LENGTH * 2);
		}
		
		v.setEnabled(false);
		context.getEmotionViewController().hideEmotionView();
		context.displayOptions(true);
		//hide input method
		InputMethodManager inputMethodManager = (InputMethodManager)v.getContext().
		    getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(edText.getWindowToken(), 0);

        boolean isComment = context.isComment();
		RetweetTask task = new RetweetTask(
			context, context.getStatus().getStatusId(),
		    text, currentAccount
		);
		task.setComment(isComment);
		task.setShowDialog(true);
        task.execute();
        
		if (context.isCommentToOrigin()) {
			UpdateCommentTask commentTask = new UpdateCommentTask(
				context, text,
				context.getRetweetedStatus().getStatusId(), currentAccount
			);

			commentTask.execute();
		}
	}
}
