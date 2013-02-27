package net.dev123.yibo.service.listener;

import net.dev123.mblog.entity.User;
import net.dev123.yibo.ProfileActivity;
import net.dev123.yibo.service.adapter.AdapterUtil;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;

public class SocialGraphItemClickListener implements OnItemClickListener {
	private Activity context;
	public SocialGraphItemClickListener(Activity context) {
		this.context = context;
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		BaseAdapter adapter = AdapterUtil.getAdapter(parent.getAdapter());
		if (adapter == null || position >= adapter.getCount()) {
			return;
		}
		
		User user = (User)adapter.getItem(position);
		if (user == null) {
            return;
        }

		Intent intent = new Intent();
		intent.putExtra("USER", user);
		intent.setClass(parent.getContext(), ProfileActivity.class);
		context.startActivity(intent);
	}

}
