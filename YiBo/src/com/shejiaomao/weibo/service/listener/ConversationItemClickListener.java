package com.shejiaomao.weibo.service.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import com.cattong.weibo.entity.DirectMessage;
import com.cattong.entity.User;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.HeaderViewListAdapter;
import android.widget.AdapterView.OnItemClickListener;

import com.shejiaomao.maobo.R;
import com.shejiaomao.weibo.activity.EditDirectMessageActivity;
import com.shejiaomao.weibo.activity.ProfileActivity;
import com.shejiaomao.weibo.common.Constants;
import com.shejiaomao.weibo.db.LocalAccount;
import com.shejiaomao.weibo.db.LocalDirectMessage;
import com.shejiaomao.weibo.service.adapter.CacheAdapter;
import com.shejiaomao.weibo.service.task.DestroyDirectMessageTask;

public class ConversationItemClickListener implements OnItemClickListener {
    private Context context;
    private DirectMessage message;
	private List<String> listItem;
	
	public ConversationItemClickListener(Context context) {
		this.context = context;
		listItem = new ArrayList<String>();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Adapter adapter = parent.getAdapter();
		message = (DirectMessage)adapter.getItem(position);
        if (message == null 
        	|| (message instanceof LocalDirectMessage
        		&& ((LocalDirectMessage)message).isDivider())) {
        	return;
        }

        if (adapter instanceof HeaderViewListAdapter) {
        	adapter = ((HeaderViewListAdapter)adapter).getWrappedAdapter();
        }
		CacheAdapter<DirectMessage> cacheAdapter = (CacheAdapter<DirectMessage>)adapter;

        Dialog dialog = onCreateDialog(cacheAdapter, position);
        if (dialog != null) {
        	dialog.show();
        }
	}

	public Dialog onCreateDialog(final CacheAdapter<DirectMessage> cacheAdapter, final int position) {
		if (message == null) {
			return null;
		}
		listItem.clear();
		listItem.add(context.getString(R.string.menu_message_reply));
		listItem.add(context.getString(R.string.menu_message_user_info));
		listItem.add(context.getString(R.string.menu_message_destroy));

		Matcher m = Constants.URL_PATTERN.matcher(message.getText());
		while (m.find()) {
			String url = m.group();
			listItem.add(url);
		}

		String[] selectItems = new String[listItem.size()];
        listItem.toArray(selectItems);
        
		return new AlertDialog.Builder(context)
            .setTitle(R.string.menu_title_message)
            .setItems(selectItems, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                	LocalAccount account = cacheAdapter.getAccount(); 
                    String myId = account.getUser().getUserId();
                    User conversationUser = message.getSender();
                    if (myId.equals(message.getSenderId())) {
                    	conversationUser = message.getRecipient();
                    }
                    
              		Intent intent = new Intent();
            		Bundle bundle = new Bundle();
                    if (which == 0) {
                        String recipientName = conversationUser.getDisplayName();
                		bundle.putString("DISPLAY_NAME", recipientName);
                		bundle.putInt("TYPE", Constants.EDIT_TYPE_REMESSAGE);
                		intent.putExtras(bundle);

                		intent.setClass(context, EditDirectMessageActivity.class);
                		((Activity)context).startActivity(intent);
                    } else if (which == 1) {
                		bundle.putSerializable("USER", conversationUser);
                		intent.putExtras(bundle);

                		intent.setClass(context, ProfileActivity.class);
                		((Activity)context).startActivity(intent);
                    } else if (which == 2) {
                    	DestroyDirectMessageTask destroyTask = new DestroyDirectMessageTask(cacheAdapter, message);
                    	destroyTask.execute();
                    } else {
        				intent.setAction(Intent.ACTION_VIEW);
        				intent.setData(Uri.parse(getItem(which)));
        				context.startActivity(intent);
                    }
                }
            })
            .create();
	}

	private String getItem(int i) {
		return listItem.get(i);
	}

}
