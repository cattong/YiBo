package net.dev123.yibo.service.listener;

import net.dev123.yibo.EditMicroBlogActivity;
import net.dev123.yibo.R;
import net.dev123.yibo.widget.AccountSelectorWindow;
import android.view.View;
import android.view.View.OnClickListener;

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
