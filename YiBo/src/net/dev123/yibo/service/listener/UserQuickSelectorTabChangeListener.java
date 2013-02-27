package net.dev123.yibo.service.listener;

import net.dev123.yibo.UserQuickSelectorActivity;
import net.dev123.yibo.service.adapter.UserQuickSelectorListAdapter;
import net.dev123.yibo.widget.TabButton.OnTabChangeListener;
import android.view.View;

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
