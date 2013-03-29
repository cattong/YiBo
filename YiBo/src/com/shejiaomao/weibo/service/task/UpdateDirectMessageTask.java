package com.shejiaomao.weibo.service.task;

import java.util.List;

import com.shejiaomao.maobo.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.cattong.commons.LibException;
import com.cattong.commons.Logger;
import com.cattong.commons.util.ListUtil;
import com.cattong.commons.util.StringUtil;
import com.cattong.weibo.Weibo;
import com.cattong.weibo.entity.DirectMessage;
import com.shejiaomao.common.ResourceBook;
import com.shejiaomao.weibo.activity.ConversationActivity;
import com.shejiaomao.weibo.common.GlobalVars;
import com.shejiaomao.weibo.db.LocalAccount;

public class UpdateDirectMessageTask extends AsyncTask<List<String>, Integer, Integer> {
	private static final String LOG = "DestroyDirectMessageTask";

	private Context context = null;
	private Weibo microBlog = null;

	private String text = null;
	private ProgressDialog dialog;
	private int totalMessageCount = 0;
	private int successMessageCount = 0;
	private String resultMsg = "";
	public UpdateDirectMessageTask(Context context, String text, LocalAccount account) {
		this.context = context;
		this.text = text;
		this.microBlog  = GlobalVars.getMicroBlog(account);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();

	    dialog = ProgressDialog.show(context, null,
	    	context.getString(R.string.msg_message_sending, successMessageCount, totalMessageCount)
	    );
	    dialog.setCancelable(true);
	    dialog.setOnCancelListener(onCancelListener);
	}

	@Override
	protected Integer doInBackground(List<String>... params) {
        if (StringUtil.isEmpty(text)) {
        	return 0;
        }
		if (params == null || params.length != 1) {
			return 0;
		}
		List<String> listIdentifyName = params[0];
		if (ListUtil.isEmpty(listIdentifyName)) {
			return 0;
		}
		totalMessageCount = listIdentifyName.size();

        DirectMessage newMessage = null;
        publishProgress(successMessageCount);
        //不存在同时发送私信给多个人的情况 Modified by Weiping Ye, 2011-8-18 18:21:24
        String identifyName = listIdentifyName.get(0);
        try {
        	if (identifyName != null) {
    		    newMessage = microBlog.sendDirectMessage(identifyName, text);
        	}
		}  catch (LibException e) {
			if (Logger.isDebug())
				Log.e(LOG, "Task", e);
			
			if (StringUtil.isEmpty(resultMsg)) {
				resultMsg = ResourceBook.getResultCodeValue(e.getErrorCode(), context);
			}
		}
        if (newMessage != null) {
	    	successMessageCount++;
	    }
	    publishProgress(successMessageCount);

		return successMessageCount;
	}

	@Override
	protected void onPostExecute(Integer result) {
	    super.onPreExecute();
	    if (dialog != null) {
	    	try {
	    	    dialog.dismiss();
	    	} catch(Exception e){}
	    }

	    Button btnSend = (Button)((Activity)context).findViewById(R.id.btnOperate);
	    if (context instanceof ConversationActivity) {
	    	btnSend = (Button)((Activity)context).findViewById(R.id.btnSend);
	    }
	    if (btnSend != null) {
	        btnSend.setEnabled(true);
	    }
	    if (result == totalMessageCount) {
        	Toast.makeText(context, R.string.msg_message_send_success, Toast.LENGTH_LONG).show();

			((Activity)context).finish();
	    } else {
	    	Toast.makeText(
	    		context,
	    		context.getString(R.string.msg_message_failed, totalMessageCount - successMessageCount, resultMsg),
	    		Toast.LENGTH_LONG
	    	).show();
	    }

	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);

		dialog.setMessage(context.getString(
			R.string.msg_message_sending, successMessageCount,
			totalMessageCount)
		);
	}

	private OnCancelListener onCancelListener = new OnCancelListener() {
		public void onCancel(DialogInterface dialog) {
			UpdateDirectMessageTask.this.cancel(true);
		}
	};

}
