package com.shejiaomao.weibo.service.listener;

import android.view.View;
import android.widget.AbsListView.RecyclerListener;

import com.shejiaomao.weibo.service.adapter.UserHolder;

public class UserRecyclerListener implements RecyclerListener {

	@Override
	public void onMovedToScrapHeap(View view) {
		if (view == null) {
			return;
		}

		Object obj = view.getTag();
		if (obj == null 
			|| !(obj instanceof UserHolder)) {
		   return;	
		}
		
		UserHolder holder = (UserHolder)obj;
		holder.recycle();
	}

}
