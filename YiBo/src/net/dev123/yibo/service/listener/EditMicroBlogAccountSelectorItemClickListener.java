package net.dev123.yibo.service.listener;

import net.dev123.yibo.EditMicroBlogActivity;
import net.dev123.yibo.db.LocalAccount;
import net.dev123.yibo.service.adapter.AccountSelectorListAdapter;
import net.dev123.yibo.service.adapter.AdapterUtil;
import net.dev123.yibo.widget.AccountSelectorWindow;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;

public class EditMicroBlogAccountSelectorItemClickListener implements
		OnItemClickListener {
	private AccountSelectorWindow selectorWindow;
	public EditMicroBlogAccountSelectorItemClickListener(AccountSelectorWindow selectorWindow) {
		this.selectorWindow = selectorWindow;
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		BaseAdapter adapter = AdapterUtil.getAdapter(parent.getAdapter());
		if (!(adapter instanceof AccountSelectorListAdapter)) {
			return;
		}
		
		LocalAccount account = (LocalAccount)adapter.getItem(position);
        if (selectorWindow.isSelected(account)) {
        	selectorWindow.removeSelectedAccount(account);
        } else {
        	selectorWindow.addSelectedAccount(account);
        }
        
        EditMicroBlogActivity context = (EditMicroBlogActivity)parent.getContext();
        context.setListUpdateAccount(selectorWindow.getSelectedAccounts());
        context.updateSelectorText();
	}

}
