package net.dev123.yibo.service.listener;

import net.dev123.yibo.service.adapter.UserSelectorHolder;
import android.view.View;
import android.widget.AbsListView.RecyclerListener;

public class UserSelectorRecyclerListener implements RecyclerListener {

	@Override
	public void onMovedToScrapHeap(View view) {
		if (view == null) {
			return;
		}

		Object obj = view.getTag();
		if (obj == null ||
			!(obj instanceof UserSelectorHolder)
		) {
		   return;	
		}
		
		UserSelectorHolder holder = (UserSelectorHolder)obj;
		holder.recycle();
	}

}
