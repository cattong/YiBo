package net.dev123.yibo.service.listener;

import net.dev123.mblog.entity.Status;
import net.dev123.yibo.MicroBlogActivity;
import net.dev123.yibo.common.Constants;
import net.dev123.yibo.db.LocalStatus;
import net.dev123.yibo.service.adapter.AdapterUtil;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;

public class MicroBlogItemLongClickListener implements OnItemLongClickListener {
    private Context context;
    
	public MicroBlogItemLongClickListener(Context context) {
		this.context = context;
	}
	
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
        BaseAdapter adapter = AdapterUtil.getAdapter(parent.getAdapter());
        Status status = (Status)adapter.getItem(position);
		if (status == null 
			|| (status instanceof LocalStatus
				&& ((LocalStatus)status).isDivider())) {
			return false;
		}
		
		Intent intent = new Intent();
		Bundle bundle = new Bundle();

		bundle.putSerializable("STATUS", status);
		intent.putExtras(bundle);

		intent.setClass(parent.getContext(), MicroBlogActivity.class);

		((Activity)context).startActivityForResult(intent, Constants.REQUEST_CODE_MY_HOME);
		
		return true;
	}

}
