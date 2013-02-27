package net.dev123.yibo.service.listener;

import net.dev123.yibo.service.adapter.ThemeHolder;
import android.view.View;
import android.widget.AbsListView.RecyclerListener;

public class ThemeRecyclerListener implements RecyclerListener {

	@Override
	public void onMovedToScrapHeap(View view) {
		if (view == null) {
			return;
		}

		Object obj = view.getTag();
		if (obj == null 
			|| !(obj instanceof ThemeHolder)) {
		   return;	
		}
		
		ThemeHolder holder = (ThemeHolder)obj;
		holder.recycle();
	}

}
