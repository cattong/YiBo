package net.dev123.yibo.service.listener;

import net.dev123.yibo.YiBoApplication;
import net.dev123.yibo.common.SelectMode;
import net.dev123.yibo.db.LocalAccount;
import net.dev123.yibo.widget.AccountSelectorWindow;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;

public class HomePageAccountClickListener implements OnClickListener {
	private AccountSelectorWindow selectorWindow;
	public HomePageAccountClickListener(Context context, View anchor) {
		this.selectorWindow = new AccountSelectorWindow(context, anchor, SelectMode.Single, false);
		HomePageSwitchAccountItemClickListener itemClickListener =
			new HomePageSwitchAccountItemClickListener(selectorWindow);
		selectorWindow.setOnItemClickListener(itemClickListener);

		YiBoApplication yibo = (YiBoApplication)context.getApplicationContext();
		selectorWindow.addSelectedAccount(yibo.getCurrentAccount());
	}

	@Override
	public void onClick(View v) {
		if (selectorWindow.isShowing()) {
            selectorWindow.dismiss();
		} else {
			selectorWindow.show();
		}
	}

	public void setSelectedAccount(LocalAccount account) {
		selectorWindow.addSelectedAccount(account);
	}
}
