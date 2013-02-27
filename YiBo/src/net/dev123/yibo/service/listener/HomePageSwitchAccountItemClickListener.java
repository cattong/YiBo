package net.dev123.yibo.service.listener;

import net.dev123.yibo.HomePageActivity;
import net.dev123.yibo.YiBoApplication;
import net.dev123.yibo.db.LocalAccount;
import net.dev123.yibo.service.adapter.AdapterUtil;
import net.dev123.yibo.widget.AccountSelectorWindow;
import net.dev123.yibo.widget.Skeleton;
import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;

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
        YiBoApplication yibo = (YiBoApplication)context.getApplicationContext();
        yibo.setCurrentAccount(account);
        
        HomePageActivity activity = (HomePageActivity)context;
        Skeleton skeleton = activity.getSkeleton();
        if (skeleton != null) {
        	skeleton.setCurrentAccount(account, true);
        	skeleton.setContentType(skeleton.getContentType());
        }
        
        selectorWindow.dismiss();
	}

}
