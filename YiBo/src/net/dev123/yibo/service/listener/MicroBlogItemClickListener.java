package net.dev123.yibo.service.listener;

import net.dev123.mblog.entity.Status;
import net.dev123.yibo.MicroBlogActivity;
import net.dev123.yibo.R;
import net.dev123.yibo.common.CompatibilityUtil;
import net.dev123.yibo.common.Constants;
import net.dev123.yibo.db.LocalStatus;
import net.dev123.yibo.service.adapter.AdapterUtil;
import net.dev123.yibo.service.adapter.CacheAdapter;
import net.dev123.yibo.service.adapter.MyHomeListAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class MicroBlogItemClickListener implements OnItemClickListener {
	private Context context;
	
	public MicroBlogItemClickListener(Context context) {
		this.context = context;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Adapter adapter = parent.getAdapter();
        Status status = (Status)adapter.getItem(position);
		if (status == null 
			|| (status instanceof LocalStatus
				&& ((LocalStatus)status).isDivider())) {
			return;
		}
		
		Intent intent = new Intent();
		Bundle bundle = new Bundle();

		bundle.putSerializable("STATUS", status);
		CacheAdapter<?> cacheAdapter = AdapterUtil.getCacheAdapter(adapter);
		if (cacheAdapter instanceof MyHomeListAdapter) {
			bundle.putInt("SOURCE", Constants.REQUEST_CODE_MY_HOME);
			bundle.putInt("POSITION", position - 1);
		}
		intent.putExtras(bundle);

		intent.setClass(parent.getContext(), MicroBlogActivity.class);
		((Activity)context).startActivityForResult(intent, Constants.REQUEST_CODE_MICRO_BLOG);
		CompatibilityUtil.overridePendingTransition(
			(Activity)context, R.anim.slide_in_right, android.R.anim.fade_out
		);
	}


}
