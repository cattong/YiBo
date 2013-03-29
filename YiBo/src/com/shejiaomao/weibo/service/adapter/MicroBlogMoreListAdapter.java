package com.shejiaomao.weibo.service.adapter;

import java.util.ArrayList;
import java.util.List;

import com.cattong.entity.Status;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.shejiaomao.maobo.R;
import com.shejiaomao.weibo.db.LocalAccount;

public class MicroBlogMoreListAdapter extends BaseAdapter {
	public static final int ITEM_DELETE = 0;
	public static final int ITEM_COPY = 1;
	public static final int ITEM_SHARE_TO_ACCOUNTS = 2;

    private Context context;
    private LocalAccount account;
    private Status status;
    private List<ItemHolder> listItem;

	public MicroBlogMoreListAdapter(Context context, LocalAccount account) {
		this.context = context;
		this.account = account;
		this.listItem = new ArrayList<ItemHolder>();
	}

	@Override
	public int getCount() {
		return listItem.size();
	}

	@Override
	public Object getItem(int position) {
		return listItem.get(position);
	}

	@Override
	public long getItemId(int position) {
		ItemHolder holder = (ItemHolder)getItem(position);
		return holder.itemId;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(context);
			convertView = inflater.inflate(R.layout.list_item_dialog_list_choose, null);
		}

		ItemHolder holder = (ItemHolder)getItem(position);

		TextView tvItemName = (TextView)convertView.findViewById(R.id.tvItemName);
		tvItemName.setText(holder.item);

		return convertView;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		if (status == null) {
			return;
		}
		this.status = status;
		listItem.clear();

		String currentUserId = "";
		if (account != null && account.getUser() != null) {
			currentUserId = account.getUser().getUserId();
		}

		if (status.getUser() != null
			&& currentUserId.equals( status.getUser().getUserId())) {
			ItemHolder holder = new ItemHolder();
			holder.itemId = ITEM_DELETE;
			holder.item = context.getString(R.string.menu_blog_delete);
			listItem.add(holder);
		}

		ItemHolder copyholder = new ItemHolder();
		copyholder.itemId = ITEM_COPY;
		copyholder.item = context.getString(R.string.menu_blog_copy);
		listItem.add(copyholder);

		ItemHolder shareToAccountsHolder = new ItemHolder();
		shareToAccountsHolder.itemId = ITEM_SHARE_TO_ACCOUNTS;
		shareToAccountsHolder.item = context.getString(R.string.menu_blog_share_to_accounts);
		listItem.add(shareToAccountsHolder);

		this.notifyDataSetChanged();
	}

	private class ItemHolder {
		int itemId;
		String item;
	}
}
