package net.dev123.yibo.service.listener;

import net.dev123.yibo.service.adapter.StatusHolder;
import android.view.View;
import android.widget.AbsListView.RecyclerListener;

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
