package net.dev123.yibo.service.listener;

import net.dev123.mblog.entity.Status;
import net.dev123.yibo.db.LocalAccount;
import net.dev123.yibo.service.adapter.MicroBlogMoreListAdapter;
import net.dev123.yibo.widget.ListChooseDialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;

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
