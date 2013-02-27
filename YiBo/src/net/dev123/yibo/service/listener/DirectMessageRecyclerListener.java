package net.dev123.yibo.service.listener;

import net.dev123.yibo.service.adapter.DirectMessageHolder;
import android.view.View;
import android.widget.AbsListView.RecyclerListener;

public class DirectMessageRecyclerListener implements RecyclerListener {

	@Override
	public void onMovedToScrapHeap(View view) {
		if (view == null) {
			return;
		}

		Object obj = view.getTag();
		if (obj == null ||
			!(obj instanceof DirectMessageHolder)
		) {
		   return;	
		}
		
		DirectMessageHolder holder = (DirectMessageHolder)obj;
		holder.recycle();
	}

}
