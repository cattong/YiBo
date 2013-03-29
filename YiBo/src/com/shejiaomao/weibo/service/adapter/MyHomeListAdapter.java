package com.shejiaomao.weibo.service.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.cattong.commons.util.ListUtil;
import com.cattong.entity.Status;
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Adapter;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.shejiaomao.maobo.R;
import com.shejiaomao.weibo.SheJiaoMaoApplication;
import com.shejiaomao.weibo.common.Constants;
import com.shejiaomao.weibo.common.NotificationEntity;
import com.shejiaomao.weibo.common.theme.ThemeUtil;
import com.shejiaomao.weibo.db.LocalAccount;
import com.shejiaomao.weibo.db.LocalStatus;
import com.shejiaomao.weibo.service.cache.MyHomeCache;
import com.shejiaomao.weibo.service.cache.ReclaimLevel;
import com.shejiaomao.weibo.service.cache.wrap.StatusWrap;
import com.shejiaomao.weibo.service.task.FlushTask;
import com.shejiaomao.weibo.service.task.MyHomePageDownTask;
import com.shejiaomao.weibo.service.task.MyHomePageUpTask;
import com.shejiaomao.weibo.service.task.MyHomeReadLocalTask;
import com.shejiaomao.weibo.widget.Skeleton;

public class MyHomeListAdapter extends CacheAdapter<Status> {
	private MyHomeCache cache;
	private List<Status> listNewBlogs;
	private int newCount = 0; //新微博数量;

	public MyHomeListAdapter(Context context, LocalAccount account) {
		super(context, account);
		SheJiaoMaoApplication sheJiaoMao = (SheJiaoMaoApplication)context.getApplicationContext();
        if (account == null) {
        	this.account = sheJiaoMao.getCurrentAccount();
        }

		cache = new MyHomeCache(context, account);
		listNewBlogs = new ArrayList<Status>(Constants.PAGING_DEFAULT_COUNT * 2);
        listNewBlogs = Collections.synchronizedList(listNewBlogs);

		paging.moveToNext();
		List<StatusWrap> listWrap = cache.read(paging);
		cache.addAll(listWrap);
		Status max = getMax();
		paging.setGlobalMax(max);

		if (max == null) {
			MyHomePageUpTask task = new MyHomePageUpTask(this);
			task.execute();
		}
	}

	@Override
	public int getCount() {
		return cache.size();
	}

	@Override
	public Object getItem(int position) {
		if (position < 0 || position > getCount() - 1) {
			return null;
		}
		Object obj = getItemWrap(position);
		if (obj != null) {
			obj = ((StatusWrap)obj).getWrap();
		}
		return obj;
	}

	private Object getItemWrap(int position) {
		if (position < 0) {
			position = 0;
		}
        if (position > getCount() - 1) {
        	position = getCount() - 1;
        }
		return cache.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Object obj = getItemWrap(position);
		StatusWrap statusWrap = (StatusWrap) obj;
		if (statusWrap == null) {
			return convertView;
		}

		int viewType = getItemViewType(position);
		if (viewType == ITEM_VIEW_TYPE_LOCAL_DIVIDER
			|| viewType == ITEM_VIEW_TYPE_REMOTE_DIVIDER) {
			convertView = fillInDividerView(convertView, statusWrap, position);
		} else {
		    convertView = StatusUtil.initConvertView(
		    	context, convertView, account.getServiceProvider());
		    StatusUtil.fillConvertView(convertView, statusWrap);
		}

		return convertView;
	}

	private View fillInDividerView(View convertView, StatusWrap wrap, final int position) {
        if (wrap == null || !(wrap.getWrap() instanceof LocalStatus)) {
        	return null;
        }

        final LocalStatus divider = (LocalStatus)wrap.getWrap();
        if (getItemViewType(position) == ITEM_VIEW_TYPE_REMOTE_DIVIDER) {
        	if (convertView == null) {
        	    convertView = inflater.inflate(R.layout.list_item_gap, null);
        	    ThemeUtil.setListViewGap(convertView);
        	}
        	View llLoadingState = convertView.findViewById(R.id.llLoadingState);
			if (divider.isLoading()) {
				llLoadingState.setVisibility(View.VISIBLE);
			} else {
				llLoadingState.setVisibility(View.GONE);
			}
			convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					v.setClickable(false);
					v.findViewById(R.id.llLoadingState).setVisibility(View.VISIBLE);

					Status max = (Status)getItem(position - 1);
					if (max instanceof LocalStatus
						&& ((LocalStatus)max).isDivider()) {
						max = null;
					}
					Status since = (Status)getItem(position + 1);
					if (since instanceof LocalStatus
						&& ((LocalStatus)since).isDivider()) {
						since = null;
					}
					MyHomePageDownTask task;
					task = new MyHomePageDownTask(MyHomeListAdapter.this, divider);
                    task.execute(max, since);
				}
			});
        } else {
        	if (convertView == null) {
        	    convertView = inflater.inflate(R.layout.list_item_more, null);
        	    ThemeUtil.setListViewMore(convertView);
        	}
        	if (divider.isLoading()) {
        		convertView.findViewById(R.id.llLoadingState).setVisibility(View.VISIBLE);
        		convertView.findViewById(R.id.tvFooter).setVisibility(View.GONE);
        	} else {
        		convertView.findViewById(R.id.llLoadingState).setVisibility(View.GONE);
        		convertView.findViewById(R.id.tvFooter).setVisibility(View.VISIBLE);
        	}
			if (paging.hasNext()) {
				convertView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (divider.isLoading()) {
							return;
						}
						v.setClickable(false);

	                    v.findViewById(R.id.llLoadingState).setVisibility(View.VISIBLE);
	                    v.findViewById(R.id.tvFooter).setVisibility(View.GONE);

	                    Status max = (Status)getItem(position - 1);
	                    Status since = (Status)getItem(position + 1);
	                    MyHomeReadLocalTask readLocaTask = new MyHomeReadLocalTask(
							MyHomeListAdapter.this, cache, divider
						);
	                    readLocaTask.execute(max, since);
					}
				});
			} else {
				((TextView)convertView.findViewById(R.id.tvFooter)).setText(R.string.label_no_more);
			}
        }

		return convertView;
	}

	@Override
	public Status getMax() {
		Status max = null;
		if (cache != null && cache.size() > 0) {
			StatusWrap wrap = cache.get(0);
			max = wrap.getWrap();
		}

		if (listNewBlogs != null && listNewBlogs.size() > 0) {
			Status temp = listNewBlogs.get(0);
			if (max != null && max.getCreatedAt() != null) {
				max = max.getCreatedAt().before(temp.getCreatedAt()) ? temp : max;
			} else {
				max = temp;
			}
		}

		return max;
	}

	@Override
	public Status getMin() {
		Status max = null;
		if (cache != null && cache.size() > 0) {
			StatusWrap wrap = cache.get(cache.size() - 1);
			max = wrap.getWrap();
		}

		return max;
	}

	@Override
	public boolean addCacheToFirst(List<Status> list) {
		if (list == null || list.size() == 0) {
			return false;
		}

		List<StatusWrap> listStatusWrap = new ArrayList<StatusWrap>(list.size());
		Date currentDate = new Date();
		StatusWrap wrap = null;
		for (Status status : list) {
			wrap = toWrap(status, currentDate);
			listStatusWrap.add(wrap);
		}

		cache.addAll(0, listStatusWrap);
		this.notifyDataSetChanged();

		FlushTask task = new FlushTask(cache);
		task.execute();

		return true;
	}

	@Override
	public boolean addCacheToDivider(Status value, List<Status> list) {
		if (list == null || list.size() == 0) {
			return false;
		}

		int i = cache.indexOf(new StatusWrap(value));
		if (i == -1) {
			return false;
		}
		cache.remove(i);

		List<StatusWrap> listWrap = new ArrayList<StatusWrap>(list.size());
		Date currentDate = new Date();
		StatusWrap wrap = null;
		for (Status temp : list) {
			wrap = toWrap(temp, currentDate);
			listWrap.add(wrap);
		}

		cache.addAll(i, listWrap);
		this.notifyDataSetChanged();

		FlushTask task = new FlushTask(cache);
		task.execute();

		return true;
	}

	@Override
	public boolean addCacheToLast(List<Status> list) {
		if (list == null || list.size() == 0) {
			return false;
		}

		List<StatusWrap> listStatusWrap = new ArrayList<StatusWrap>(list.size());
		Date currentDate = new Date();
		StatusWrap wrap = null;
		for (Status status : list) {
			wrap = toWrap(status, currentDate);
			listStatusWrap.add(wrap);
		}

		cache.addAll(listStatusWrap);
		this.notifyDataSetChanged();

		FlushTask task = new FlushTask(cache);
		task.execute();

		return true;
	}

	@Override
	public boolean remove(int position) {
		if (position < 0 || position >= getCount()) {
			return false;
		}
		cache.remove(position);
		this.notifyDataSetChanged();

		return true;
	}

	@Override
	public boolean remove(Status status) {
		if (status == null) {
			return false;
		}
		cache.remove(new StatusWrap(status));
		this.notifyDataSetChanged();
		return true;
	}

	@Override
	public void clear() {
		refresh();

		cache.clear();
		this.notifyDataSetChanged();
	}

	@Override
	public void reclaim(ReclaimLevel level) {
		boolean isReclaim = cache.reclaim(level);
		if (isReclaim) {
			try {
			    this.notifyDataSetChanged();
			} catch(Exception e) {
				this.notifyDataSetInvalidated();
			}
		}
	}

	public void addNewBlogs(List<Status> list) {
		if (list == null || list.size() == 0) {
			return;
		}

		listNewBlogs.addAll(0, list);
	}

	public NotificationEntity getNotificationEntity() {
		newCount = listNewBlogs.size();
		if (newCount > 0
			&& listNewBlogs.get(newCount - 1) instanceof LocalStatus) {
        	newCount--;
        }
		NotificationEntity entity = new NotificationEntity();
		entity.setTickerText(context.getString(R.string.msg_myhome_ticker_text));
		String accountName = account.getUser().getScreenName();
		entity.setContentTitle(context.getString(
			R.string.msg_myhome_content_title,
			accountName, newCount));
		entity.setContentText(account.getServiceProvider().getSpName() + ": " 
			+ getNewBlogsDesc(newCount));
		entity.setContentType(Skeleton.TYPE_MY_HOME);
		return entity;
	}
	public String getNewBlogsDesc(int newCount) {
		StringBuffer desc = new StringBuffer();
		if (ListUtil.isEmpty(listNewBlogs)) {
			return desc.toString();
		}

        List<String> listScreenName = new ArrayList<String>();
        String screenName = null;
        for (int i = 0; i < newCount && listScreenName.size() < 3; i++) {
        	Status status = listNewBlogs.get(i);
        	if (status == null
        		|| status instanceof LocalStatus
        		|| status.getUser() == null) {
        		continue;
        	}
        	screenName = status.getUser().getScreenName();
        	if (!listScreenName.contains(screenName)) {
        		listScreenName.add(screenName);
        	}
        }

		if (listScreenName.size() >= 3) {
			desc.append(
				context.getString(
					R.string.msg_myhome_content_text_3, listScreenName.get(0),
					listScreenName.get(1), listScreenName.get(2)
				)
			);
		} else if (listScreenName.size() == 2) {
			desc.append(
				context.getString(
					R.string.msg_myhome_content_text_2, listScreenName.get(0), listScreenName.get(1)
				)
			);
		} else if (listScreenName.size() == 1){
			desc.append(context.getString(R.string.msg_myhome_content_text_1, listScreenName.get(0)));
		}

		return desc.toString();
	}

	public boolean refresh() {
		if (listNewBlogs == null || listNewBlogs.size() == 0) {
			return false;
		}

		addCacheToFirst(listNewBlogs);
		int offset = listNewBlogs.size();
		listNewBlogs.clear();

		ListView lvMicroBlog = (ListView)((Activity)context).findViewById(R.id.lvMicroBlog);
		if (lvMicroBlog == null) {
			return true;
		}
		Adapter adapter = lvMicroBlog.getAdapter();
		if (adapter instanceof HeaderViewListAdapter) {
			adapter = ((HeaderViewListAdapter)adapter).getWrappedAdapter();
		}
		if (adapter == this) {
			int position = lvMicroBlog.getFirstVisiblePosition();
			View view = lvMicroBlog.getChildAt(0);
			int y = 0;
			if (view != null && position >= 1) {
			    y = view.getTop();
			    //System.out.println("y:" + y + " position:" + position);
			    position += offset;
			    lvMicroBlog.setSelectionFromTop(position, y);
			}
		}

		return true;
	}


	public StatusWrap toWrap(Status status, Date date) {
		if (status == null) {
			return null;
		}

		StatusWrap wrap = new StatusWrap(status);
		wrap.setReadedTime(date);

		return wrap;
	}

	public List<Status> getListNewBlogs() {
		return listNewBlogs;
	}

	public void setListNewBlogs(List<Status> listNewBlogs) {
		this.listNewBlogs = listNewBlogs;
	}

	public LocalAccount getAccount() {
		return account;
	}

	@Override
	public int getItemViewType(int position) {
		Status status = (Status)getItem(position);
		if (status == null) {
			return ITEM_VIEW_TYPE_REMOTE_DIVIDER;
		}
		if (!(status instanceof LocalStatus)) {
			return ITEM_VIEW_TYPE_DATA;
		}

		LocalStatus localStatus = (LocalStatus)status;
		if (!localStatus.isDivider()) {
			return ITEM_VIEW_TYPE_DATA;
		}

		if (localStatus.isLocalDivider()) {
			return ITEM_VIEW_TYPE_LOCAL_DIVIDER;
		}
		return ITEM_VIEW_TYPE_REMOTE_DIVIDER;
	}

	@Override
	public int getViewTypeCount() {
		return 3;
	}
}
