package com.shejiaomao.weibo.service.listener;

import com.cattong.weibo.entity.Group;

import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.shejiaomao.maobo.R;
import com.shejiaomao.weibo.common.CacheManager;
import com.shejiaomao.weibo.db.LocalAccount;
import com.shejiaomao.weibo.service.adapter.AdapterUtil;
import com.shejiaomao.weibo.service.adapter.GroupStatusesListAdapter;
import com.shejiaomao.weibo.service.adapter.HomePageGroupListAdapter;
import com.shejiaomao.weibo.service.cache.AdapterCollectionCache;
import com.shejiaomao.weibo.service.cache.Cache;
import com.shejiaomao.weibo.widget.ListChooseDialog;

public class HomePageGroupItemClickListener implements OnItemClickListener {
    private ListChooseDialog chooseDialog;
	public HomePageGroupItemClickListener(ListChooseDialog chooseDialog) {
		this.chooseDialog = chooseDialog;
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		HomePageGroupListAdapter listAdapter = 
			(HomePageGroupListAdapter)AdapterUtil.getAdapter(parent.getAdapter());
		Group group = null;
        if (listAdapter != null) {
        	group = (Group)listAdapter.getItem(position);
        }
        if (group == null) {
        	return;
        }
        
        Activity context = (Activity)view.getContext();
        LocalAccount account = listAdapter.getAccount();
        ListAdapter statusesListAdapter = null;
        if (position == 0) {
        	Cache cache = CacheManager.getInstance().getCache(account);
        	AdapterCollectionCache adapterCache = (AdapterCollectionCache)cache;
        	statusesListAdapter = adapterCache.getMyHomeListAdapter();
        } else {
        	statusesListAdapter = new GroupStatusesListAdapter(
            	context, account, group);
        }
        
        ListView lvMicroBlog = (ListView)context.findViewById(R.id.lvMicroBlog);
        if (lvMicroBlog != null) {   	
        	lvMicroBlog.setAdapter(statusesListAdapter);
        }
        TextView tvTitle = (TextView)context.findViewById(R.id.tvTitle);
        if (tvTitle != null) {
        	tvTitle.setText(group.getName());
        }
        
        chooseDialog.dismiss();
	}

}
