package com.shejiaomao.weibo.service.listener;

import android.view.View;
import android.widget.AbsListView.RecyclerListener;

import com.shejiaomao.weibo.service.adapter.StatusHolder;

public class StatusRecyclerListener implements RecyclerListener {

	@Override
	public void onMovedToScrapHeap(View view) {
		if (view == null) {
			return;
		}

		Object obj = view.getTag();
		if (obj == null 
			|| !(obj instanceof StatusHolder)) {
		   return;	
		}
		
		StatusHolder holder = (StatusHolder)obj;
		holder.recycle();
	}

}
