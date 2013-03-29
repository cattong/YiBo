package com.shejiaomao.weibo.service.task;

import java.util.ArrayList;
import java.util.List;

import com.shejiaomao.maobo.R;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.cattong.commons.LibException;
import com.cattong.commons.Logger;
import com.cattong.commons.Paging;
import com.cattong.commons.util.ListUtil;
import com.cattong.entity.User;
import com.cattong.weibo.Weibo;
import com.cattong.weibo.entity.DirectMessage;
import com.shejiaomao.common.ResourceBook;
import com.shejiaomao.weibo.common.GlobalVars;
import com.shejiaomao.weibo.db.DirectMessageDao;
import com.shejiaomao.weibo.db.LocalAccount;
import com.shejiaomao.weibo.service.adapter.CacheAdapter;
import com.shejiaomao.weibo.service.adapter.DirectMessagesListAdapter;

public class DestroyDirectMessageTask extends AsyncTask<Void, DirectMessage, DirectMessage> {
	private static final String LOG = "DestroyDirectMessageTask";

	private Context context;
	private LocalAccount account;
	private CacheAdapter<DirectMessage> adapter;
	private Weibo microBlog;
	private DirectMessage message;
    private User conversationUser;

	private ProgressDialog dialog;
	private String resultMsg;
	private DirectMessageDao dao;
	public DestroyDirectMessageTask(CacheAdapter<DirectMessage> adapter, DirectMessage message) {
		this.context = adapter.getContext();
		this.adapter = adapter;
		this.message = message;
		this.account = adapter.getAccount();
        this.microBlog  = GlobalVars.getMicroBlog(account);

        dao = new DirectMessageDao(context);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();

	    dialog = ProgressDialog.show(context, null, context.getString(R.string.msg_message_destroying));
	    dialog.setCancelable(true);
	    dialog.setOnCancelListener(onCancelListener);
	}

	@Override
	protected DirectMessage doInBackground(Void... params) {
		DirectMessage result = null;
		if (microBlog == null || message == null) {
			return result;
		}

		String myId = account.getUser().getUserId();
		List<DirectMessage> listMessage = new ArrayList<DirectMessage>();
		if (adapter instanceof DirectMessagesListAdapter) {
            conversationUser = message.getSender();
            if (myId.equals(message.getSenderId())) {
            	conversationUser = message.getRecipient();
            }
		}

		if (conversationUser != null) {
			Paging<DirectMessage> page = new Paging<DirectMessage>();
			while (page.moveToNext()) {
			    List<DirectMessage> listTemp = dao.getConversations(conversationUser, account, page);
			    if (ListUtil.isNotEmpty(listTemp)) {
			        listMessage.addAll(listTemp);
			    }
			}
		} else {
			listMessage.add(message);
		}

        try {
        	for (DirectMessage message : listMessage) {
        		if (myId.equals(message.getSenderId())) {
        			result = microBlog.destroyOutboxDirectMessage(message.getId());
        		} else {
        			result = microBlog.destroyInboxDirectMessage(message.getId());
        		}
        		if (result != null) {
        			publishProgress(message);
        		}
        	}
		} catch (LibException e) {
			if (Logger.isDebug()) Log.e(LOG, "Task", e);
			resultMsg = ResourceBook.getResultCodeValue(e.getErrorCode(), context);
		}

		return result;
	}

	@Override
	protected void onProgressUpdate(DirectMessage... values) {
		super.onProgressUpdate(values);

		for (DirectMessage message : values) {
			dao.delete(message, account);
		}
	}

	@Override
	protected void onPostExecute(DirectMessage result) {
	    super.onPreExecute();
	    if (dialog != null &&
	        dialog.getContext() != null
	    ) {
	    	try {
	    	    dialog.dismiss();
	    	} catch (Exception e) {}
	    }

	    if (result != null) {
	    	adapter.remove(message);
        	Toast.makeText(context, R.string.msg_message_destroy_success, Toast.LENGTH_LONG).show();
	    } else {
	    	Toast.makeText(context, resultMsg, Toast.LENGTH_LONG).show();
	    }

	}

	private OnCancelListener onCancelListener = new OnCancelListener() {
		public void onCancel(DialogInterface dialog) {
			DestroyDirectMessageTask.this.cancel(true);
		}
	};
}
