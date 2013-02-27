package net.dev123.yibo.service.listener;

import net.dev123.mblog.entity.Group;
import net.dev123.yibo.R;
import net.dev123.yibo.common.CacheManager;
import net.dev123.yibo.db.LocalAccount;
import net.dev123.yibo.service.adapter.AdapterUtil;
import net.dev123.yibo.service.adapter.GroupStatusesListAdapter;
import net.dev123.yibo.service.adapter.HomePageGroupListAdapter;
import net.dev123.yibo.service.cache.AdapterCollectionCache;
import net.dev123.yibo.service.cache.Cache;
import net.dev123.yibo.widget.ListChooseDialog;
import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

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
