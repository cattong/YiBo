package com.shejiaomao.weibo.service.listener;

import com.cattong.weibo.entity.Group;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;

import com.shejiaomao.maobo.R;
import com.shejiaomao.weibo.activity.GroupMemberActivity;
import com.shejiaomao.weibo.common.SelectMode;
import com.shejiaomao.weibo.service.adapter.GroupListAdapter;
import com.shejiaomao.weibo.service.task.GroupDeleteTask;

public class GroupContextMenuListener implements OnCreateContextMenuListener {
	private GroupListAdapter adapter;
	public GroupContextMenuListener(GroupListAdapter adapter) {
		this.adapter = adapter;
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
		int position = info.position;
		if (position < 0 || position >= adapter.getCount()) {
			return;
		}
		Group group = (Group)adapter.getItem(position);
		if (group == null) {
			return;
		}
		Context context = v.getContext();
		analyzeCommentMenu(group, menu, context);
	}

	private void analyzeCommentMenu(final Group group, ContextMenu menu, final Context context) {
		menu.setHeaderTitle(R.string.title_dialog_group);
		int order = 0;
		
		MenuItem replyMenu = menu.add(0, order, order++, R.string.menu_group_delete);
		replyMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				new AlertDialog.Builder(context)
			        .setTitle(R.string.title_dialog_alert)
			        .setMessage(R.string.msg_group_delete_confirm)
			        .setNegativeButton(R.string.btn_cancel,
					    new AlertDialog.OnClickListener() {
						    @Override
						    public void onClick(DialogInterface dialog, int which) {
						    }
					    })
				    .setPositiveButton(R.string.btn_confirm, 
					    new AlertDialog.OnClickListener() {
					        @Override
					        public void onClick(DialogInterface dialog, int which) {
					        	new GroupDeleteTask(adapter, group.getId()).execute();
					        }
				    }).show();
	        	return true;
			}
		});
		
        MenuItem profileMenu = menu.add(0, order, order++, R.string.menu_group_view);
        profileMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putString("SELECT_MODE", SelectMode.Multiple.toString());
				bundle.putSerializable("GROUP", group);
				intent.putExtras(bundle);

				intent.setClass(context, GroupMemberActivity.class);
	            context.startActivity(intent);
	        	return true;
			}
		});
      
	}
}
