package com.shejiaomao.weibo.service.adapter;

import java.util.List;

import com.cattong.commons.Paging;
import com.cattong.commons.util.StringUtil;
import com.cattong.commons.util.TimeSpanUtil;
import com.cattong.weibo.entity.DirectMessage;
import com.cattong.entity.User;
import android.content.Context;
import android.text.Spannable;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.shejiaomao.maobo.R;
import com.shejiaomao.weibo.common.EmotionLoader;
import com.shejiaomao.weibo.common.GlobalVars;
import com.shejiaomao.weibo.common.theme.ThemeUtil;
import com.shejiaomao.weibo.db.LocalAccount;
import com.shejiaomao.weibo.db.LocalDirectMessage;
import com.shejiaomao.weibo.service.cache.ConversationCache;
import com.shejiaomao.weibo.service.cache.wrap.DirectMessageWrap;
import com.shejiaomao.weibo.service.task.ConversationReadLocalTask;
import com.shejiaomao.weibo.service.task.ImageLoad4HeadTask;
import com.shejiaomao.weibo.service.task.InitAdapterTask;

public class ConversationListAdapter extends CacheAdapter<DirectMessage> {	
	private ConversationCache cache = null;

	public ConversationListAdapter(Context context, LocalAccount account, User conversationUser) {
		super(context, account);
		this.paging = new Paging<DirectMessage>();

        cache = new ConversationCache(context, account, conversationUser);

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
		Object obj = getItem(position);
		DirectMessage message = (DirectMessage)obj;
		int itemViewType = getItemViewType(position);
		if (itemViewType == ITEM_VIEW_TYPE_DATA) {			
			convertView = fillInView(convertView, message);
		} else {
			convertView = fillInDividerView(convertView, message, position);
		}		

		return convertView;
	}

	private View fillInView(View convertView, DirectMessage message) {
		DirectMessageHolder holder = null;
		if (convertView == null || !isDirectMessageView(convertView)) {
			convertView = inflater.inflate(R.layout.list_item_conversation, null);
			holder = new DirectMessageHolder(convertView);
			convertView.setTag(holder);
		} else {
            holder = (DirectMessageHolder)convertView.getTag();
		}
		
        if (holder == null) {
        	return null;
        }
        holder.reset();

        String myId = account.getUser().getUserId();
        User targetUser = message.getSender();
        ImageView ivProfilePicture = holder.ivProfilePicture;
        if (myId.equals(targetUser.getUserId())) {
        	ivProfilePicture = holder.ivMyProfilePicture;
        }
        if (GlobalVars.IS_SHOW_HEAD) {
        	ivProfilePicture.setVisibility(View.VISIBLE);
        	String profileUrl = targetUser.getProfileImageUrl();
			if (StringUtil.isNotEmpty(profileUrl)) {
				ImageLoad4HeadTask headTask = new ImageLoad4HeadTask(ivProfilePicture, profileUrl, true);
		        holder.headTask = headTask;
				headTask.execute();
			}
        }

        holder.tvScreenName.setText(targetUser.getScreenName());
		holder.tvCreateAt.setText(TimeSpanUtil.toTimeSpanString(message.getCreatedAt()));

		Spannable textSpan = EmotionLoader.getEmotionSpannable(message.getServiceProvider(), message.getText());
		holder.tvMessageText.setText(textSpan);

		return convertView;
	}

	private View fillInDividerView(View convertView, DirectMessage message, final int position) {
		if (message == null 
			|| !(message instanceof LocalDirectMessage)) {
	        return null;
	    }

		final LocalDirectMessage divider = (LocalDirectMessage)message;
		if (!divider.isLocalDivider()) {
			convertView = inflater.inflate(R.layout.list_item_gap, null);
			ThemeUtil.setListViewGap(convertView);
			if (divider.isLoading()) {
				convertView.findViewById(R.id.llLoadingState).setVisibility(View.VISIBLE);
			}
			convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					v.setClickable(false);
					v.findViewById(R.id.llLoadingState).setVisibility(View.VISIBLE);

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
				convertView.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
						if (divider.isLoading()) {
							return;
						}
	                    v.setClickable(false);
	                    v.findViewById(R.id.llLoadingState).setVisibility(View.VISIBLE);
	                    v.findViewById(R.id.tvFooter).setVisibility(View.GONE);

	                    DirectMessage max = (DirectMessage)getItem(position - 1);
	                    DirectMessage since = (DirectMessage)getItem(position + 1);

	                    ConversationReadLocalTask readLocalTask= new ConversationReadLocalTask(
	                    	ConversationListAdapter.this, cache, divider
	                    );
	                    readLocalTask.execute(max, since);
					}
				});
			} else {
				((TextView)convertView.findViewById(R.id.tvFooter)).setText(R.string.label_no_more);
			}
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


	public LocalAccount getAccount() {
		return account;
	}

	public void setAccount(LocalAccount account) {
		this.account = account;
	}

	@Override
	public boolean addCacheToFirst(List<DirectMessage> list) {
		return false;
	}

	@Override
	public boolean addCacheToDivider(DirectMessage value,
			List<DirectMessage> list) {
		return false;
	}

	@Override
	public boolean addCacheToLast(List<DirectMessage> list) {
		return false;
	}
	
	@Override
	public DirectMessage getMax() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DirectMessage getMin() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public boolean remove(DirectMessage message) {
		if (message == null) {
			return false;
		}
		DirectMessageWrap wrap = new DirectMessageWrap(message);
		cache.remove(wrap);
		this.notifyDataSetChanged();
		return true;
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
