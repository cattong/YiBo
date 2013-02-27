package net.dev123.yibo.service.listener;

import net.dev123.mblog.entity.Status;
import net.dev123.yibo.MicroBlogActivity;
import net.dev123.yibo.R;
import net.dev123.yibo.common.CacheManager;
import net.dev123.yibo.db.LocalAccount;
import net.dev123.yibo.db.LocalStatus;
import net.dev123.yibo.service.adapter.MyHomeListAdapter;
import net.dev123.yibo.service.cache.AdapterCollectionCache;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class MicroBlogPreviewClickListener implements OnClickListener {
    private MyHomeListAdapter adapter;
    private int position;
	public MicroBlogPreviewClickListener(LocalAccount account, int position) {
		this.position = position;
		AdapterCollectionCache cache = (AdapterCollectionCache)
		    CacheManager.getInstance().getCache(account);
		if (cache != null) {
			adapter = cache.getMyHomeListAdapter();
		}
	}
	
	@Override
	public void onClick(View v) {
        if (adapter == null) {
        	return;
        }
        
        MicroBlogActivity context = (MicroBlogActivity)v.getContext();
        
        Status status = getStatus(v);
        if (status == null) {
        	return;
        }
		if (status instanceof LocalStatus
			&& ((LocalStatus)status).isDivider()) {
			status = getStatus(v);
		}
		
		if (status != null) {
		    context.fillInView(status);
		}
	}

    private Status getStatus(View v) {
    	Status status = null;
        
        if (v.getId() == R.id.btnPrevious) {
        	if (position <= 0) {
            	Toast.makeText(v.getContext(), "已经没有上一条微博了", Toast.LENGTH_SHORT).show();
            	return status;
            }
        	position--;
        } else {
        	if (position >= adapter.getCount() - 1) {
            	Toast.makeText(v.getContext(), "已经没有下一条微博了", Toast.LENGTH_SHORT).show();
            	return status;
            }
        	position++;
        }
        status = (Status)adapter.getItem(position);
        
        return status;
    }
}
