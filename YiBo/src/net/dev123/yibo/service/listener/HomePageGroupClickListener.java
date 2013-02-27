package net.dev123.yibo.service.listener;

import net.dev123.yibo.db.LocalAccount;
import net.dev123.yibo.service.adapter.HomePageGroupListAdapter;
import net.dev123.yibo.widget.ListChooseDialog;
import net.dev123.yibo.widget.Skeleton;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;

public class HomePageGroupClickListener implements OnClickListener {
	private Context context;
    private Skeleton skeleton;
    private ListChooseDialog chooseDialog;
    private HomePageGroupListAdapter listAdapter;
	public HomePageGroupClickListener(Skeleton skeleton) {
		this.skeleton = skeleton;
		this.context = skeleton.getContext();
		this.listAdapter = new HomePageGroupListAdapter(context);
	}
	
	@Override
	public void onClick(View v) {
        if (chooseDialog == null) {
        	chooseDialog = new ListChooseDialog(v.getContext(), v);
        	chooseDialog.setListAdapter(listAdapter);
        	HomePageGroupItemClickListener itemClickListener = 
        		new HomePageGroupItemClickListener(chooseDialog);
        	chooseDialog.setItemClickLitener(itemClickListener);
        }
        
        LocalAccount account = skeleton.getCurrentAccount();
        listAdapter.setAccount(account);
        
        if (!chooseDialog.isShowing()) {
        	chooseDialog.show();
        }
	}

}
