package com.shejiaomao.weibo.service.listener;

import android.view.View;
import android.widget.ListView;

public class Back2TopDoubleClickListener extends OnDoubleClickListener {
    private ListView listView;
	public Back2TopDoubleClickListener(ListView listView) {
		this.listView = listView;
	}
	
	@Override
	public void onDoubleClick(View v) {
		if (listView == null) {
			return;
		}
		listView.setSelection(0);
	}
}
