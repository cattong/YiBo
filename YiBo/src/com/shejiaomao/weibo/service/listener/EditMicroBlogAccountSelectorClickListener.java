package com.shejiaomao.weibo.service.listener;

import java.util.List;

import android.view.View;
import android.view.View.OnClickListener;

import com.shejiaomao.maobo.R;
import com.shejiaomao.weibo.activity.EditMicroBlogActivity;
import com.shejiaomao.weibo.common.GlobalVars;
import com.shejiaomao.weibo.common.SelectMode;
import com.shejiaomao.weibo.db.LocalAccount;
import com.shejiaomao.weibo.widget.AccountSelectorWindow;

public class EditMicroBlogAccountSelectorClickListener implements
		OnClickListener {
    private EditMicroBlogActivity context;
    List<LocalAccount> listUpdateAccount;
    List<LocalAccount> listAllAccount;

    private AccountSelectorWindow selectorWindow;
	public EditMicroBlogAccountSelectorClickListener(EditMicroBlogActivity context) {
		this.context = context;
		listUpdateAccount = context.getListUpdateAccount();
		listAllAccount = GlobalVars.getAccountList(context, false);

		View llHeaderBase = context.findViewById(R.id.llHeaderBase);
		this.selectorWindow = new AccountSelectorWindow(context, llHeaderBase, SelectMode.Multiple, true);
	    EditMicroBlogAccountSelectorItemClickListener itemClickListener =
	    	new EditMicroBlogAccountSelectorItemClickListener(selectorWindow);
	    selectorWindow.setOnItemClickListener(itemClickListener);

	    selectorWindow.addSelectedAccounts(listUpdateAccount);
	}

	@Override
	public void onClick(View v) {
		if (selectorWindow.isShowing()) {
            selectorWindow.dismiss();
		} else {
			selectorWindow.show();
		}
	}
}
