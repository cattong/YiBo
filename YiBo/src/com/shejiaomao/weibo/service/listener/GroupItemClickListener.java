package com.shejiaomao.weibo.service.listener;

import com.cattong.commons.util.StringUtil;
import com.cattong.weibo.entity.Group;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.AdapterView.OnItemClickListener;

import com.shejiaomao.maobo.R;
import com.shejiaomao.weibo.activity.GroupActivity;
import com.shejiaomao.weibo.activity.GroupMemberActivity;
import com.shejiaomao.weibo.common.SelectMode;
import com.shejiaomao.weibo.service.adapter.AdapterUtil;
import com.shejiaomao.weibo.service.adapter.CacheAdapter;
import com.shejiaomao.weibo.service.adapter.GroupListAdapter;
import com.shejiaomao.weibo.service.task.GroupAddTask;

public class GroupItemClickListener implements OnItemClickListener {
    private GroupActivity context;
	public GroupItemClickListener(GroupActivity context) {
		this.context = context;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Adapter adapter = parent.getAdapter();
		final CacheAdapter<Group> cacheAdapter =
			(CacheAdapter<Group>)AdapterUtil.getCacheAdapter(adapter);
		if (position >= cacheAdapter.getCount()) {
			return;
		}

		//final LocalAccount account = cacheAdapter.getAccount();
		Group group = (Group)cacheAdapter.getItem(position);

		if (position == 0) {
			final EditText editText = new EditText(context);
			new AlertDialog.Builder(context)
            .setIcon(R.drawable.icon_group)
            .setTitle(R.string.title_dialog_add_group)
            .setView(editText)
            .setPositiveButton(R.string.btn_confirm, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    String groupName = editText.getEditableText().toString();
                    if (StringUtil.isNotEmpty(groupName)) {
                    	GroupAddTask task = new GroupAddTask(
                    		(GroupListAdapter)cacheAdapter, groupName);
                        task.execute();
                    }
                }
            })
            .setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {

                    /* User clicked cancel so do some stuff */
                }
            })
            .create().show();
		} else {
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putString("SELECT_MODE", SelectMode.Multiple.toString());
			bundle.putSerializable("GROUP", group);
			intent.putExtras(bundle);

			intent.setClass(context, GroupMemberActivity.class);
			context.startActivity(intent);
		}
	}

}
