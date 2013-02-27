package net.dev123.yibo.service.listener;

import net.dev123.yibo.service.adapter.UserHolder;
import android.view.View;
import android.widget.AbsListView.RecyclerListener;

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
