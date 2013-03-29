package com.shejiaomao.weibo.service.listener;

import android.view.View;
import android.view.View.OnClickListener;

import com.shejiaomao.maobo.R;
import com.shejiaomao.weibo.activity.EditMicroBlogActivity;
import com.shejiaomao.weibo.widget.AccountSelectorWindow;

public class AccountSelectorOperateClickListener implements OnClickListener {
	private AccountSelectorWindow selectorWindow;
	public AccountSelectorOperateClickListener(AccountSelectorWindow selectorWindow) {
		this.selectorWindow = selectorWindow;
	}
	
	@Override
	public void onClick(View v) {
		if (selectorWindow == null) {
			return;
		}
        if (v.getId() == R.id.btnSelectAll) {
        	selectorWindow.selectAll();
        } else if (v.getId() == R.id.btnSelectInverse) {
        	selectorWindow.selectInverse();
        }
        
        if (v.getContext() instanceof EditMicroBlogActivity) {
            EditMicroBlogActivity context = (EditMicroBlogActivity)v.getContext();
            context.setListUpdateAccount(selectorWindow.getSelectedAccounts());
            context.updateSelectorText();
        }
	}

}
