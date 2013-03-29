package com.shejiaomao.weibo.service.listener;

import com.cattong.entity.Status;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.shejiaomao.maobo.R;
import com.shejiaomao.weibo.activity.MicroBlogActivity;
import com.shejiaomao.weibo.common.CacheManager;
import com.shejiaomao.weibo.db.LocalAccount;
import com.shejiaomao.weibo.db.LocalStatus;
import com.shejiaomao.weibo.service.adapter.MyHomeListAdapter;
import com.shejiaomao.weibo.service.cache.AdapterCollectionCache;

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
