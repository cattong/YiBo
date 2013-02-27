package net.dev123.yibo.service.listener;

import net.dev123.commons.util.StringUtil;
import net.dev123.yibo.EditRetweetActivity;
import net.dev123.yibo.R;
import net.dev123.yibo.YiBoApplication;
import net.dev123.yibo.common.Constants;
import net.dev123.yibo.db.LocalAccount;
import net.dev123.yibo.service.task.RetweetTask;
import net.dev123.yibo.service.task.UpdateCommentTask;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

public class EditRetweetSendClickListener implements OnClickListener {
	private EditRetweetActivity context;

	private LocalAccount currentAccount;
	public EditRetweetSendClickListener(EditRetweetActivity context) {
		this.context = context;

		YiBoApplication yibo = (YiBoApplication)context.getApplication();
		currentAccount = yibo.getCurrentAccount();
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
			context, context.getStatus().getId(),
		    text, currentAccount
		);
		task.setComment(isComment);
		task.setShowDialog(true);
        task.execute();
        
		if (context.isCommentToOrigin()) {
			UpdateCommentTask commentTask = new UpdateCommentTask(
				context, text,
				context.getRetweetedStatus().getId(), currentAccount
			);

			commentTask.execute();
		}
	}
}
