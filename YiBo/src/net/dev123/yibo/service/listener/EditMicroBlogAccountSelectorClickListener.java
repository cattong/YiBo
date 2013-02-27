package net.dev123.yibo.service.listener;

import java.util.List;

import net.dev123.yibo.EditMicroBlogActivity;
import net.dev123.yibo.R;
import net.dev123.yibo.common.GlobalVars;
import net.dev123.yibo.common.SelectMode;
import net.dev123.yibo.db.LocalAccount;
import net.dev123.yibo.widget.AccountSelectorWindow;
import android.view.View;
import android.view.View.OnClickListener;

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
