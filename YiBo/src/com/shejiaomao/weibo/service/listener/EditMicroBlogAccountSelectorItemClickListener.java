package com.shejiaomao.weibo.service.listener;

import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.AdapterView.OnItemClickListener;

import com.shejiaomao.weibo.activity.EditMicroBlogActivity;
import com.shejiaomao.weibo.db.LocalAccount;
import com.shejiaomao.weibo.service.adapter.AccountSelectorListAdapter;
import com.shejiaomao.weibo.service.adapter.AdapterUtil;
import com.shejiaomao.weibo.widget.AccountSelectorWindow;

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
