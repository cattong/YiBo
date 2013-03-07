package com.shejiaomao.weibo.service.listener;

import com.cattong.entity.Status;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;

import com.shejiaomao.weibo.db.LocalAccount;
import com.shejiaomao.weibo.service.adapter.MicroBlogMoreListAdapter;
import com.shejiaomao.weibo.widget.ListChooseDialog;

public class MicroBlogMoreClickListener implements OnClickListener {
    private Context context;
    private Status status;
    private ListChooseDialog chooseDialog;
    private MicroBlogMoreListAdapter listAdapter;
	public MicroBlogMoreClickListener(Context context, LocalAccount account) {
		this.context = context;
		this.listAdapter = new MicroBlogMoreListAdapter(context, account);
	}
	
	@Override
	public void onClick(View v) {
        if (chooseDialog == null) {
        	chooseDialog = new ListChooseDialog(context, v);
        	chooseDialog.setListAdapter(listAdapter);
        	MicroBlogMoreItemClickListener itemClickListener = 
        		new MicroBlogMoreItemClickListener(chooseDialog);
        	chooseDialog.setItemClickLitener(itemClickListener);
        }
        
        if (!chooseDialog.isShowing()) {
        	chooseDialog.show();
        }
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		if (status == null) {
			return;
		}
		this.status = status;
		
		listAdapter.setStatus(status);
	}

}
