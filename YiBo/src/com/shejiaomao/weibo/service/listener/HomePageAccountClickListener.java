package com.shejiaomao.weibo.service.listener;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;

import com.shejiaomao.weibo.SheJiaoMaoApplication;
import com.shejiaomao.weibo.common.SelectMode;
import com.shejiaomao.weibo.db.LocalAccount;
import com.shejiaomao.weibo.widget.AccountSelectorWindow;

public class HomePageAccountClickListener implements OnClickListener {
	private AccountSelectorWindow selectorWindow;
	public HomePageAccountClickListener(Context context, View anchor) {
		this.selectorWindow = new AccountSelectorWindow(context, anchor, SelectMode.Single, false);
		HomePageSwitchAccountItemClickListener itemClickListener =
			new HomePageSwitchAccountItemClickListener(selectorWindow);
		selectorWindow.setOnItemClickListener(itemClickListener);

		SheJiaoMaoApplication sheJiaoMao = (SheJiaoMaoApplication)context.getApplicationContext();
		selectorWindow.addSelectedAccount(sheJiaoMao.getCurrentAccount());
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
