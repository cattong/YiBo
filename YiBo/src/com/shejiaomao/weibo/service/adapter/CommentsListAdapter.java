package com.shejiaomao.weibo.service.adapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.shejiaomao.maobo.R;
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.cattong.commons.util.ListUtil;
import com.cattong.entity.Comment;
import com.cattong.weibo.entity.UnreadCount;
import com.shejiaomao.weibo.common.NotificationEntity;
import com.shejiaomao.weibo.common.theme.ThemeUtil;
import com.shejiaomao.weibo.db.LocalAccount;
import com.shejiaomao.weibo.db.LocalComment;
import com.shejiaomao.weibo.service.cache.CommentCache;
import com.shejiaomao.weibo.service.cache.ReclaimLevel;
import com.shejiaomao.weibo.service.cache.wrap.CommentWrap;
import com.shejiaomao.weibo.service.task.CommentsPageDownTask;
import com.shejiaomao.weibo.service.task.CommentsReadLocalTask;
import com.shejiaomao.weibo.service.task.FlushTask;
import com.shejiaomao.weibo.service.task.InitAdapterTask;
import com.shejiaomao.weibo.widget.Skeleton;

public class CommentsListAdapter extends CacheAdapter<Comment> {
	private CommentCache cache;
	private List<Comment> listNewComments;
	private int newCount = 0; //新评论数量;

	public CommentsListAdapter(Context context, LocalAccount account) {
		super(context, account);

		cache = new CommentCache(context, account);
		listNewComments = new ArrayList<Comment>();

		InitAdapterTask task = new InitAdapterTask(cache, this);
		task.execute();
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
			obj = ((CommentWrap)obj).getWrap();
		}
		return obj;
	}

	public Object getItemWrap(int position) {
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
		CommentWrap commentWrap = (CommentWrap)obj;
		if (commentWrap == null) {
			return convertView;
		}
		int itemViewType = getItemViewType(position);
		if (itemViewType == ITEM_VIEW_TYPE_DATA) {
			convertView = CommentUtil.initConvertView(context, convertView);
			CommentUtil.fillConvertView(convertView, commentWrap);
		} else if (itemViewType == ITEM_VIEW_TYPE_REMOTE_DIVIDER) {
			convertView = fillInRemoteDividerView(convertView, commentWrap, position);
		} else {
			convertView = fillInLocalDividerView(convertView, commentWrap, position);
		}

		return convertView;
	}

	private View fillInRemoteDividerView(View convertView, CommentWrap wrap, final int position) {
		if (wrap == null || !(wrap.getWrap() instanceof LocalComment)) {
	        return convertView;
	    }

		final LocalComment divider = (LocalComment)wrap.getWrap();
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

				Comment max = (Comment)getItem(position - 1);
				if (max instanceof LocalComment
					&& ((LocalComment)max).isDivider()) {
					max = null;
				}
				Comment since = (Comment)getItem(position + 1);
				if (since instanceof LocalComment
					&& ((LocalComment)since).isDivider()) {
					since = null;
				}
			    CommentsPageDownTask task = new CommentsPageDownTask(CommentsListAdapter.this, divider);
				task.execute(max, since);
			}
		});

		return convertView;
	}

	private View fillInLocalDividerView(View convertView, CommentWrap wrap, final int position) {
		if (wrap == null || !(wrap.getWrap() instanceof LocalComment)) {
	        return null;
	    }

		final LocalComment divider = (LocalComment)wrap.getWrap();
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
			convertView.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					if (divider.isLoading()) {
						return;
					}
                    v.setClickable(false);
                    v.findViewById(R.id.llLoadingState).setVisibility(View.VISIBLE);
                    v.findViewById(R.id.tvFooter).setVisibility(View.GONE);

					Comment max = (Comment)getItem(position - 1);
					Comment since = (Comment)getItem(position + 1);
					CommentsReadLocalTask localCacheTask = new CommentsReadLocalTask(
						CommentsListAdapter.this, cache, divider
					);
					localCacheTask.execute(max, since);
				}
			});
		} else {
			((TextView)convertView.findViewById(R.id.tvFooter)).setText(R.string.label_no_more);
		}

		return convertView;
	}

	@Override
	public boolean addCacheToFirst(List<Comment> list) {
		if (list == null || list.size() == 0) {
			return false;
		}

		List<CommentWrap> listCommentWrap = new ArrayList<CommentWrap>(list.size());
		Date currentDate = new Date();
		for (Comment comment : list) {
			listCommentWrap.add(toWrap(comment, currentDate));
		}

		cache.addAll(0, listCommentWrap);
		this.notifyDataSetChanged();

		FlushTask task = new FlushTask(cache);
		task.execute();

		return true;
	}

	@Override
	public boolean addCacheToDivider(Comment value, List<Comment> list) {
		if (list == null || list.size() == 0) {
			return false;
		}

		int i = cache.indexOf(new CommentWrap(value));
		if (i == -1) {
			return false;
		}
		cache.remove(i);

		List<CommentWrap> listWrap = new ArrayList<CommentWrap>(list.size());
		Date currentDate = new Date();
		CommentWrap wrap = null;
		for (Comment temp : list) {
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
	public boolean addCacheToLast(List<Comment> list) {
		if (ListUtil.isEmpty(list)) {
			return false;
		}

		List<CommentWrap> listCommentWrap = new ArrayList<CommentWrap>(list.size());
		Date currentDate = new Date();
		for (Comment comment : list) {
			listCommentWrap.add(toWrap(comment, currentDate));
		}

		cache.addAll(cache.size(), listCommentWrap);
		this.notifyDataSetChanged();

		FlushTask task = new FlushTask(cache);
		task.execute();

		return true;
	}

	@Override
	public void clear() {
		cache.clear();
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

	public void addNewComments(List<Comment> list) {
		if (list == null || list.size() == 0) {
			return;
		}

		listNewComments.addAll(0, list);
	}

	public NotificationEntity getNotificationEntity(UnreadCount remindCount) {
		if (remindCount != null) {
			newCount += remindCount.getCommentCount();
		} else {
			newCount = listNewComments.size();
	       	if (newCount > 0
	       		&& listNewComments.get(newCount - 1) instanceof LocalComment) {
        		newCount--;
        	}
		}

		NotificationEntity entity = new NotificationEntity();
		entity.setTickerText(context.getString(R.string.msg_comments_ticker_text));
		String accountName = account.getUser().getScreenName();
		entity.setContentTitle(context.getString(
			R.string.msg_comments_content_title,
			accountName, newCount));
		entity.setContentText(account.getServiceProvider().getSpName() + ": "
			+ getNewBlogsDesc(newCount));
		entity.setContentType(Skeleton.TYPE_COMMENT);
		return entity;
	}
	public String getNewBlogsDesc(int newCount) {
		StringBuffer desc = new StringBuffer();
		if (ListUtil.isEmpty(listNewComments) || newCount <= 0) {
			return desc.toString();
		}

        List<String> listScreenName = new ArrayList<String>();
        String screenName = null;
        int showScreenNameCount = 3;
        if (showScreenNameCount > newCount) {
        	showScreenNameCount = newCount;
        }
        for (int i = 0; i < listNewComments.size() && listScreenName.size() < showScreenNameCount; i++) {
        	Comment comment = listNewComments.get(i);
        	if (comment == null
        		|| comment instanceof LocalComment
        		|| comment.getUser() == null) {
        		continue;
        	}
        	screenName = comment.getUser().getScreenName();
        	if (!listScreenName.contains(screenName)) {
        		listScreenName.add(screenName);
        	}
        }

		if (listScreenName.size() >= 3) {
			desc.append(
				context.getString(
					R.string.msg_comments_content_text_3, listScreenName.get(0),
					listScreenName.get(1), listScreenName.get(2)
				)
			);
		} else if (listScreenName.size() == 2) {
			desc.append(
				context.getString(
					R.string.msg_comments_content_text_2, listScreenName.get(0), listScreenName.get(1)
				)
			);
		} else if (listScreenName.size() == 1) {
			desc.append(context.getString(R.string.msg_comments_content_text_1, listScreenName.get(0)));
		}

		return desc.toString();
	}

	public boolean refresh() {
		if (ListUtil.isEmpty(listNewComments)) {
			return false;
		}

		addCacheToFirst(listNewComments);
		int offset = listNewComments.size();
		listNewComments.clear();
		newCount = 0;

		ListView lvMicroBlog = (ListView)((Activity)context).findViewById(R.id.lvMicroBlog);
		if (lvMicroBlog == null) {
			return true;
		}
		Adapter adapter = lvMicroBlog.getAdapter();
		if (adapter instanceof HeaderViewListAdapter) {
			adapter = ((HeaderViewListAdapter)adapter).getWrappedAdapter();
		}
		if (lvMicroBlog != null &&
			adapter == this
		) {
			int position = lvMicroBlog.getFirstVisiblePosition();
			View view = lvMicroBlog.getChildAt(0);
			int y = 0;
			if (view != null && position >= 1) {
			    y = view.getTop();
			    position += offset;
			    lvMicroBlog.setSelectionFromTop(position, y);
			}
		}

		return true;
	}

	@Override
	public Comment getMax() {
		Comment max = null;
		if (cache != null && cache.size() > 0) {
			CommentWrap wrap = cache.get(0);
			max = wrap.getWrap();
		}

		if (ListUtil.isNotEmpty(listNewComments)) {
			Comment temp = listNewComments.get(0);
			if (max != null && max.getCreatedAt() != null) {
				max = max.getCreatedAt().before(temp.getCreatedAt()) ? temp : max;
			} else {
				max = temp;
			}
		}

		return max;
	}

	@Override
	public Comment getMin() {
		Comment min = null;
		if (cache != null && cache.size() > 0) {
			CommentWrap wrap = cache.get(cache.size() - 1);
			min = wrap.getWrap();
		}

		return min;
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
	public boolean remove(Comment comment) {
		if (comment == null) {
			return false;
		}
		cache.remove(new CommentWrap(comment));
		this.notifyDataSetChanged();
		return true;
	}

	private CommentWrap toWrap(Comment comment, Date date) {
		if (comment == null) {
			return null;
		}

		CommentWrap wrap = new CommentWrap(comment);
		wrap.setReadedTime(date);
		return wrap;
	}

	public LocalAccount getAccount() {
		return account;
	}

	public void setAccount(LocalAccount account) {
		this.account = account;
	}

	public List<Comment> getListNewComments() {
		return listNewComments;
	}

	@Override
	public int getItemViewType(int position) {
		Comment comment = (Comment)getItem(position);
		if (comment == null) {
			return ITEM_VIEW_TYPE_REMOTE_DIVIDER;
		}
		if (!(comment instanceof LocalComment)) {
			return ITEM_VIEW_TYPE_DATA;
		}

		LocalComment localComment = (LocalComment)comment;
		if (!localComment.isDivider()) {
			return ITEM_VIEW_TYPE_DATA;
		}

		if (localComment.isLocalDivider()) {
			return ITEM_VIEW_TYPE_LOCAL_DIVIDER;
		}
		return ITEM_VIEW_TYPE_REMOTE_DIVIDER;
	}

	@Override
	public int getViewTypeCount() {
		return 3;
	}
}
