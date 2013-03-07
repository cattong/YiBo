package com.shejiaomao.weibo.service.listener;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;

import com.shejiaomao.weibo.db.LocalAccount;
import com.shejiaomao.weibo.service.adapter.HomePageGroupListAdapter;
import com.shejiaomao.weibo.widget.ListChooseDialog;
import com.shejiaomao.weibo.widget.Skeleton;

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
