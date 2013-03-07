package com.shejiaomao.weibo.service.listener;

import android.view.View;

import com.shejiaomao.weibo.activity.UserQuickSelectorActivity;
import com.shejiaomao.weibo.service.adapter.UserQuickSelectorListAdapter;
import com.shejiaomao.widget.TabButton.OnTabChangeListener;

public class UserQuickSelectorTabChangeListener implements OnTabChangeListener {
    private UserQuickSelectorActivity context;
	public UserQuickSelectorTabChangeListener(UserQuickSelectorActivity context) {
		this.context = context;
	}
	
	@Override
	public void onTabChange(View v, int which) {
		UserQuickSelectorListAdapter adapter = context.getSelectorAdapter();
		switch (which) {
		case 0:
			adapter.clear();
			
			break;
		case 1:
			adapter.clear();
			break;
		}
		adapter.notifyDataSetChanged();
		context.executeTask();
	}

}
