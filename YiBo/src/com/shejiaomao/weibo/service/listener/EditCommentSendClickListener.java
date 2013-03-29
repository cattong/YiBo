package com.shejiaomao.weibo.service.listener;

import com.shejiaomao.maobo.R;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.cattong.commons.ServiceProvider;
import com.cattong.commons.util.StringUtil;
import com.cattong.entity.Comment;
import com.cattong.entity.Status;
import com.shejiaomao.weibo.SheJiaoMaoApplication;
import com.shejiaomao.weibo.activity.EditCommentActivity;
import com.shejiaomao.weibo.common.Constants;
import com.shejiaomao.weibo.db.LocalAccount;
import com.shejiaomao.weibo.service.task.RetweetTask;
import com.shejiaomao.weibo.service.task.UpdateCommentTask;

public class EditCommentSendClickListener implements OnClickListener {
    private EditCommentActivity context;

	private LocalAccount account;
	public EditCommentSendClickListener(EditCommentActivity context) {
		this.context = context;

		SheJiaoMaoApplication sheJiaoMao = (SheJiaoMaoApplication)context.getApplication();
		account = sheJiaoMao.getCurrentAccount();
	}

	@Override
	public void onClick(View v) {
		EditText etComment = (EditText) context.findViewById(R.id.etText);
		String text = etComment.getText().toString().trim();
		if (StringUtil.isEmpty(text)
			&& etComment.getHint() != null) {
			text = etComment.getHint().toString();
		}
		if (StringUtil.isEmpty(text)) {
        	Toast.makeText(v.getContext(), R.string.msg_comment_empty, Toast.LENGTH_LONG).show();
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
		inputMethodManager.hideSoftInputFromWindow(etComment.getWindowToken(), 0);

	    UpdateCommentTask commentTask = null;
	    Comment recomment = context.getRecomment();
		if (recomment == null) {
			commentTask = new UpdateCommentTask(context, text, context.getStatus().getStatusId(), account);
		} else {
			//String recommentText = text.substring(text.indexOf(":") + 1); //截断评论前的hint
			String recommentText = text;
			if (account.getServiceProvider() == ServiceProvider.Sohu) {
				recommentText = text;
			}
			if (StringUtil.isEmpty(recommentText)) {
	        	Toast.makeText(context, R.string.msg_comment_empty, Toast.LENGTH_LONG).show();
	        	v.setEnabled(true);
	        	return;
			}
		    commentTask = new UpdateCommentTask(
			    v.getContext(),     recommentText,
			    context.getStatus().getStatusId(), recomment.getCommentId(),
				account
			);
		}
		commentTask.setShowDialog(true);
		commentTask.execute();

		if (context.isRetweet()) {
			String retweetText = text;
			if (context.getRecomment() != null) {
				retweetText += " //" + context.getRecomment().getUser().getMentionName() +
				    ":" + context.getRecomment().getText();
			}
			if (context.getStatus().getRetweetedStatus() != null) {
				retweetText += " //" + context.getStatus().getUser().getMentionName() +
				    ":" + context.getStatus().getText();
			}
			byteLen = StringUtil.getLengthByByte(retweetText);
			if (byteLen > Constants.STATUS_TEXT_MAX_LENGTH * 2) {
				retweetText = StringUtil.subStringByByte(retweetText, 0, Constants.STATUS_TEXT_MAX_LENGTH * 2);
			}

			RetweetTask retweetTask = new RetweetTask(
				context, context.getStatus().getStatusId(), retweetText, account
			);
			retweetTask.setShowDialog(false);
			retweetTask.execute();
		}

	    if (context.isCommentToOrigin()) {
	    	String ctoText = text + " #" + context.getString(R.string.app_name) + "#";
	    	Status retweetedStatus = context.getStatus().getRetweetedStatus();
	    	UpdateCommentTask commenToOriginTask = new UpdateCommentTask(
	    		context, ctoText, retweetedStatus.getStatusId(), account
	    	);
	    	commenToOriginTask.setShowDialog(false);
	    	commenToOriginTask.execute();
	    }
	}
}
