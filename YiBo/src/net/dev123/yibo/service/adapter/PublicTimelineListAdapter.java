package net.dev123.yibo.service.adapter;

import net.dev123.mblog.entity.Status;
import net.dev123.yibo.PublicTimelineActivity;
import net.dev123.yibo.R;
import net.dev123.yibo.db.LocalAccount;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class PublicTimelineListAdapter extends ArrayAdapter<Status> {
	private LocalAccount account = null;
	private PublicTimelineActivity context;

	public PublicTimelineListAdapter(PublicTimelineActivity context, LocalAccount account) {
		super(context, R.layout.list_item_status);
		this.account = account;
		this.context = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Status status = (Status)getItem(position);
		if (status == null) {
			return null;
		}

		convertView = StatusUtil.initConvertView(context, convertView, account.getServiceProvider());
	    StatusUtil.fillConvertView(convertView, status);

		return convertView;
	}

	public LocalAccount getAccount() {
		return account;
	}

	public void setAccount(LocalAccount account) {
		this.account = account;
	}

}
