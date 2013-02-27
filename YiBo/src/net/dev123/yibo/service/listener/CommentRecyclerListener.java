package net.dev123.yibo.service.listener;

import net.dev123.yibo.service.adapter.CommentHolder;
import android.view.View;
import android.widget.AbsListView.RecyclerListener;

public class CommentRecyclerListener implements RecyclerListener {

	@Override
	public void onMovedToScrapHeap(View view) {
		if (view == null) {
			return;
		}

		Object obj = view.getTag();
		if (obj == null ||
			!(obj instanceof CommentHolder)
		) {
		   return;	
		}
		
		CommentHolder holder = (CommentHolder)obj;
		holder.recycle();
	}

}
