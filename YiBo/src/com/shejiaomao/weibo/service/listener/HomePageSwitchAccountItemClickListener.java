package com.shejiaomao.weibo.service.listener;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.AdapterView.OnItemClickListener;

import com.shejiaomao.weibo.SheJiaoMaoApplication;
import com.shejiaomao.weibo.activity.HomePageActivity;
import com.shejiaomao.weibo.db.LocalAccount;
import com.shejiaomao.weibo.service.adapter.AdapterUtil;
import com.shejiaomao.weibo.widget.AccountSelectorWindow;
import com.shejiaomao.weibo.widget.Skeleton;

public class HomePageSwitchAccountItemClickListener implements OnItemClickListener {
	private AccountSelectorWindow selectorWindow;
	public HomePageSwitchAccountItemClickListener(AccountSelectorWindow selectorWindow) {
        this.selectorWindow = selectorWindow;
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
        BaseAdapter adapter = AdapterUtil.getAdapter(parent.getAdapter());
        LocalAccount account = (LocalAccount)adapter.getItem(position);        
        if (selectorWindow.isSelected(account)) {
        	selectorWindow.removeSelectedAccount(account);
        } else {
        	selectorWindow.addSelectedAccount(account);
        }
        
        Context context = view.getContext();
        SheJiaoMaoApplication sheJiaoMao = (SheJiaoMaoApplication)context.getApplicationContext();
        sheJiaoMao.setCurrentAccount(account);
        
        HomePageActivity activity = (HomePageActivity)context;
        Skeleton skeleton = activity.getSkeleton();
        if (skeleton != null) {
        	skeleton.setCurrentAccount(account, true);
        	skeleton.setContentType(skeleton.getContentType());
        }
        
        selectorWindow.dismiss();
	}

}
