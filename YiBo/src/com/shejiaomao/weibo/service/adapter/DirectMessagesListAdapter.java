package com.shejiaomao.weibo.service.adapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.cattong.commons.util.ListUtil;
import com.cattong.commons.util.StringUtil;
import com.cattong.commons.util.TimeSpanUtil;
import com.cattong.weibo.entity.DirectMessage;
import com.cattong.weibo.entity.UnreadCount;
import com.cattong.entity.User;
import android.app.Activity;
import android.content.Context;
import android.text.Spannable;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Adapter;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.shejiaomao.maobo.R;
import com.shejiaomao.weibo.common.EmotionLoader;
import com.shejiaomao.weibo.common.GlobalResource;
import com.shejiaomao.weibo.common.GlobalVars;
import com.shejiaomao.weibo.common.NotificationEntity;
import com.shejiaomao.weibo.common.theme.ThemeUtil;
import com.shejiaomao.weibo.db.LocalAccount;
import com.shejiaomao.weibo.db.LocalDirectMessage;
import com.shejiaomao.weibo.service.cache.DirectMessageCache;
import com.shejiaomao.weibo.service.cache.ReclaimLevel;
import com.shejiaomao.weibo.service.cache.wrap.DirectMessageWrap;
import com.shejiaomao.weibo.service.task.DirectMessageReadLocalTask;
import com.shejiaomao.weibo.service.task.ImageLoad4HeadTask;
import com.shejiaomao.weibo.service.task.InitAdapterTask;
import com.shejiaomao.weibo.widget.Skeleton;

public class DirectMessagesListAdapter extends CacheAdapter<DirectMessage> {
	private DirectMessageCache cache = null;
	private List<DirectMessage> newInboxList;
	private List<DirectMessage> newOutboxList;
	private int newCount = 0;

	public DirectMessagesListAdapter(Context context, LocalAccount account) {
		super(context, account);

		cache = new DirectMessageCache(context, account);
		newInboxList = new ArrayList<DirectMessage>();
		newOutboxList = new ArrayList<DirectMessage>();

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
			obj = ((DirectMessageWrap)obj).getWrap();
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
		DirectMessageWrap messageWrap = (DirectMessageWrap)obj;
		if (messageWrap == null) {
			return convertView;
		}
		int itemViewType = getItemViewType(position);
		if (itemViewType == ITEM_VIEW_TYPE_DATA) {
			convertView = fillInView(convertView, messageWrap);
		} else if (itemViewType == ITEM_VIEW_TYPE_REMOTE_DIVIDER)  {
			convertView = fillInRemoteDividerView(convertView, messageWrap, position);
		} else {
			convertView = fillInLocalDividerView(convertView, messageWrap, position);
		}

		return convertView;
	}

	private View fillInView(View convertView, DirectMessageWrap messageWrap) {
		DirectMessageHolder holder;
		if (convertView == null || !isDirectMessageView(convertView)) {
			convertView = inflater.inflate(R.layout.list_item_direct_message, null);
			holder = new DirectMessageHolder(convertView);
			convertView.setTag(holder);
	    } else {
	    	holder = (DirectMessageHolder)convertView.getTag();
	    	holder.reset();
	    }
		
		DirectMessage message = messageWrap.getWrap();

        String myId = account.getUser().getUserId();
        User targetUser = message.getSender();
        if (myId.equals(message.getSenderId())) {
        	targetUser = message.getRecipient();
        }
        if (GlobalVars.IS_SHOW_HEAD) {
        	holder.ivProfilePicture.setVisibility(View.VISIBLE);
			String profileUrl = targetUser.getProfileImageUrl();
			if (StringUtil.isNotEmpty(profileUrl)) {
				ImageLoad4HeadTask headTask = new ImageLoad4HeadTask(holder.ivProfilePicture, profileUrl, true);
		        holder.headTask = headTask;
				headTask.execute();
			}
        }

		holder.tvScreenName.setText(targetUser.getScreenName() + "(" + messageWrap.getCoversationCount() + ")");
		holder.tvCreateAt.setText(TimeSpanUtil.toTimeSpanString(message.getCreatedAt()));
		messageWrap.hit();
		if (!messageWrap.isReaded()) {
			holder.tvCreateAt.setTextColor(GlobalResource.getStatusTimelineUnreadColor(context));
			if (messageWrap.getHit() > 2) {
				messageWrap.setReaded(true);
			}
		}

		Spannable textSpan = EmotionLoader.getEmotionSpannable(
			message.getServiceProvider(), message.getText());
		holder.tvMessageText.setText(textSpan);

		return convertView;
	}

	private View fillInRemoteDividerView(View convertView, DirectMessageWrap wrap, final int position) {
		if (wrap == null 
			|| !(wrap.getWrap() instanceof LocalDirectMessage)) {
	        return convertView;
	    }

		final LocalDirectMessage divider = (LocalDirectMessage)wrap.getWrap();
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

//				DirectMessage max = (DirectMessage)getItem(position - 1);
//				DirectMessage since = (DirectMessage)getItem(position + 1);
//				DirectMessagePageDownTask task;
//				task = new DirectMessagePageDownTask(InboxListAdapter.this, divider);
//                task.execute(max, since);
			}
		});


		return convertView;
	}

	private View fillInLocalDividerView(View convertView, DirectMessageWrap wrap, final int position) {
		if (wrap == null 
			|| !(wrap.getWrap() instanceof LocalDirectMessage)) {
	        return null;
	    }

		final LocalDirectMessage divider = (LocalDirectMessage)wrap.getWrap();
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

                    DirectMessage inboxMax = cache.getInboxMin();
                    DirectMessage outboxMax = cache.getOutboxMin();

                    DirectMessageReadLocalTask readLocalTask = new DirectMessageReadLocalTask(
                        DirectMessagesListAdapter.this, cache, divider
                    );
                    readLocalTask.execute(inboxMax, outboxMax);
				}
			});
		} else {
			((TextView)convertView.findViewById(R.id.tvFooter)).setText(R.string.label_no_more);
		}
		

		return convertView;
	}

	private boolean isDirectMessageView(View convertView) {
		boolean isStatusView = false;
		try {
		     View view = convertView.findViewById(R.id.ivProfilePicture);
		     if (view != null) {
		    	 isStatusView = true;
		     }
		} catch(Exception e) {
		}

		return isStatusView;
	}

	public void clear() {
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

	public void addNewInbox(List<DirectMessage> list) {
		if (ListUtil.isEmpty(list)) {
			return;
		}

		newInboxList.addAll(0, list);
	}

	public void addNewOutbox(List<DirectMessage> list) {
		if (ListUtil.isEmpty(list)) {
			return;
		}

		newOutboxList.addAll(0, list);
	}
	
	public NotificationEntity getNotificationEntity(UnreadCount unreadCount) {
		if (unreadCount != null) {
			newCount += unreadCount.getDireceMessageCount();
		} else {
			newCount = getNewInboxSize();
		}
		NotificationEntity entity = new NotificationEntity();
		entity.setTickerText(context.getString(R.string.msg_direct_messages_ticker_text));
		String accountName = account.getUser().getScreenName();
		entity.setContentTitle(context.getString(
			R.string.msg_direct_messages_content_title,
			accountName, newCount));
		entity.setContentText(account.getServiceProvider().getSpName() + ": " 
			+ getNewBlogsDesc(newCount));
		entity.setContentType(Skeleton.TYPE_DIRECT_MESSAGE);
		return entity;
	}
	
	public String getNewBlogsDesc(int newCount) {
		StringBuffer desc = new StringBuffer();
		if (ListUtil.isEmpty(newInboxList) || newCount <= 0) {
			return desc.toString();
		}

        List<String> listScreenName = new ArrayList<String>();
        String screenName = null;
        int showScreenNameCount = 3;
        if (showScreenNameCount > newCount) {
        	showScreenNameCount = newCount;
        }
        for (int i = 0; i < newInboxList.size() && listScreenName.size() < showScreenNameCount; i++) {
        	DirectMessage message = newInboxList.get(i);
        	if (message == null 
        		|| message instanceof LocalDirectMessage) {
        		continue;
        	}
        	screenName = message.getSenderScreenName();
        	if (!listScreenName.contains(screenName)) {
        		listScreenName.add(screenName);
        	}
        }

		if (listScreenName.size() >= 3) {
			desc.append(
				context.getString(
					R.string.msg_direct_messages_content_text_3, listScreenName.get(0),
					listScreenName.get(1), listScreenName.get(2)
				)
			);
		} else if (listScreenName.size() == 2) {
			desc.append(
				context.getString(
					R.string.msg_direct_messages_content_text_2, listScreenName.get(0), listScreenName.get(1)
				)
			);
		} else {
			desc.append(context.getString(R.string.msg_direct_messages_content_text_1, listScreenName.get(0)));
		}

		return desc.toString();
	}

	public boolean refresh() {
		if (ListUtil.isEmpty(newInboxList) && ListUtil.isEmpty(newOutboxList)) {
			return false;
		}

		addCacheToFirst(newInboxList);
		addCacheToFirst(newOutboxList);
		int offset = newInboxList.size() + newOutboxList.size();
		newInboxList.clear();
		newOutboxList.clear();
		newCount = 0;

		ListView lvMicroBlog = (ListView)((Activity)context).findViewById(R.id.lvMicroBlog);
		if (lvMicroBlog == null) {
			return true;
		}
		Adapter adapter = lvMicroBlog.getAdapter();
		if (adapter instanceof HeaderViewListAdapter) {
			adapter = ((HeaderViewListAdapter)adapter).getWrappedAdapter();
		}
		if (lvMicroBlog != null 
			&& adapter == this) {
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
	public boolean addCacheToFirst(List<DirectMessage> list) {
		if (ListUtil.isEmpty(list)) {
			return false;
		}

		List<DirectMessageWrap> listStatusWrap = new ArrayList<DirectMessageWrap>(list.size());
		Date currentDate = new Date();
		DirectMessageWrap wrap = null;
		for (DirectMessage message : list) {
			wrap = toWrap(message, currentDate);
			listStatusWrap.add(wrap);
		}

		cache.addAll(0, listStatusWrap);
		this.notifyDataSetChanged();

//		FlushTask task = new FlushTask(cache);
//		task.execute();
        cache.write(list);
        
		return true;
	}

	@Override
	public boolean addCacheToDivider(DirectMessage value, List<DirectMessage> list) {
		if (ListUtil.isEmpty(list)) {
			return false;
		}

		int i = cache.indexOf(new DirectMessageWrap(value));
		if (i == -1) {
			return false;
		}
		cache.remove(i);

		List<DirectMessageWrap> listWrap = new ArrayList<DirectMessageWrap>(list.size());
		Date currentDate = new Date();
		DirectMessageWrap wrap = null;
		for (DirectMessage message : list) {
			wrap = toWrap(message, currentDate);
			listWrap.add(wrap);
		}

		cache.addAll(i, listWrap);
		this.notifyDataSetChanged();

//		FlushTask task = new FlushTask(cache);
//		task.execute();
		cache.write(list);
		
		return true;
	}

	@Override
	public boolean addCacheToLast(List<DirectMessage> list) {
		if (ListUtil.isEmpty(list)) {
			return false;
		}

		List<DirectMessageWrap> listStatusWrap = new ArrayList<DirectMessageWrap>(list.size());
		Date currentDate = new Date();
		DirectMessageWrap wrap = null;
		for (DirectMessage message : list) {
			wrap = toWrap(message, currentDate);
			listStatusWrap.add(wrap);
		}

		cache.addAll(cache.size(), listStatusWrap);
		this.notifyDataSetChanged();

//		FlushTask task = new FlushTask(cache);
//		task.execute();
		cache.write(list);
		
		return true;
	}
	
	private DirectMessageWrap toWrap(DirectMessage message, Date currentDate) {
		if (message == null) {
			return null;
		}

		DirectMessageWrap wrap = new DirectMessageWrap(message);
		wrap.setReadedTime(currentDate);

		return wrap;
	}

	//设计获得发件箱最大的，收件箱最大的
	@Override
	public DirectMessage getMax() {
		DirectMessage max = null;
		if (cache != null && cache.size() > 0) {
			DirectMessageWrap wrap = cache.get(0);
			max = wrap.getWrap();
		}

		if (ListUtil.isNotEmpty(newInboxList)) {
			DirectMessage temp = newInboxList.get(0);
			if (max != null && max.getCreatedAt() != null) {
				max = max.getCreatedAt().before(temp.getCreatedAt()) ? temp : max;
			} else {
				max = temp;
			}
		}

		return max;
	}

	public DirectMessage getInboxMax() {
		DirectMessage max = cache.getInboxMax();
		if (ListUtil.isEmpty(newInboxList)) {
			return max;
		}

		DirectMessage message = null;
		for (int i = 0; i < newInboxList.size(); i++) {
			DirectMessage temp = newInboxList.get(i);
		    if (StringUtil.isEquals(temp.getRecipientId(), account.getUserId())) {
		    	message = temp;
		    	break;
		    }
		}
		if (max != null && max.getCreatedAt() != null && message != null) {
			max = max.getCreatedAt().before(message.getCreatedAt()) ? message : max;
		} else {
			max = message;
		}
		
		return max;
	}

	public DirectMessage getOutboxMax() {
		DirectMessage max = cache.getOutboxMax();
		if (ListUtil.isEmpty(newOutboxList)) {
			return max;
		}
		
		DirectMessage message = null;
		for (int i = 0; i < newOutboxList.size(); i++) {
			DirectMessage temp = newOutboxList.get(i);
			if (StringUtil.isEquals(temp.getSenderId(), account.getUserId())) {
				message = temp;
				break;
			}
		}
		if (max != null && max.getCreatedAt() != null && message != null) {
			max = max.getCreatedAt().before(message.getCreatedAt()) ? message : max;
		} else {
			max = message;
		}
		
        return max;
	}

	@Override
	public DirectMessage getMin() {
		DirectMessage min = null;
		if (cache != null && cache.size() > 0) {
			DirectMessageWrap wrap = cache.get(cache.size() - 1);
			min = wrap.getWrap();
		}
		return min;
	}

	public DirectMessage getInboxMin() {
		return cache.getInboxMin();
	}

	public DirectMessage getOutboxMin() {
		return cache.getOutboxMin();
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
	public boolean remove(DirectMessage message) {
		if (message == null) {
			return false;
		}
		cache.remove(new DirectMessageWrap(message));
		this.notifyDataSetChanged();
		return true;
	}

	public LocalAccount getAccount() {
		return account;
	}

	public void setAccount(LocalAccount account) {
		this.account = account;
	}

	public List<DirectMessage> getNewInboxList() {
		return newInboxList;
	}

	public List<DirectMessage> getNewOutboxList() {
		return newOutboxList;
	}
	
	public int getNewInboxSize() {
		int size = newInboxList.size();
		if (size > 0 
			&& newInboxList.get(size - 1) instanceof LocalDirectMessage) {
			size--;
		}
		return size;
	}
	
	public int getNewOutboxSize() {
		int size = newOutboxList.size();
		if (size > 0 
			&& newOutboxList.get(size - 1) instanceof LocalDirectMessage) {
			size--;
		}
		return size;
	}
	
	@Override
	public int getItemViewType(int position) {
		DirectMessage message = (DirectMessage)getItem(position);
		if (message == null) {
			return ITEM_VIEW_TYPE_REMOTE_DIVIDER;
		}
		if (!(message instanceof LocalDirectMessage)) {
			return ITEM_VIEW_TYPE_DATA;
		}
		
		LocalDirectMessage localMessage = (LocalDirectMessage)message;
		if (!localMessage.isDivider()) {
			return ITEM_VIEW_TYPE_DATA;
		}
		if (localMessage.isLocalDivider()) {
			return ITEM_VIEW_TYPE_LOCAL_DIVIDER;
		}
		return ITEM_VIEW_TYPE_REMOTE_DIVIDER;
	}

	@Override
	public int getViewTypeCount() {
		return 3;
	}
}
