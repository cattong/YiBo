package com.shejiaomao.weibo.service.cache;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.cattong.commons.Paging;
import com.cattong.commons.util.ListUtil;
import com.cattong.weibo.entity.DirectMessage;
import com.cattong.entity.User;
import android.content.Context;
import android.content.res.Resources;

import com.shejiaomao.maobo.R;
import com.shejiaomao.weibo.db.DirectMessageDao;
import com.shejiaomao.weibo.db.LocalAccount;
import com.shejiaomao.weibo.db.LocalDirectMessage;
import com.shejiaomao.weibo.service.cache.wrap.DirectMessageWrap;

public class ConversationCache implements ListCache<DirectMessageWrap, DirectMessage> {
	private static final int REMAIN_LEVEL_LIGHT_COUNT = 40;
	private static final int REMAIN_LEVEL_MODERATE_COUNT = 20;
	private static final int REMAIN_LEVEL_WEIGHT_COUNT = 10;

    private Context context = null;
    private User conversationUser;
	private LocalAccount account = null;

	private List<DirectMessageWrap> listCache = null;

	public ConversationCache(Context context, LocalAccount account, User conversationUser) {
		this.context = context;
		this.account = account;
        this.conversationUser = conversationUser;
        
		listCache = new ArrayList<DirectMessageWrap>();
	}

	@Override
	public void clear() {
		flush();

		listCache.clear();
	}

	@Override
	public boolean reclaim(ReclaimLevel level) {
		int remainCount = size();
		if (remainCount >= REMAIN_LEVEL_LIGHT_COUNT) {
			level = ReclaimLevel.MODERATE;
		}
		switch(level) {
		case LIGHT: remainCount = REMAIN_LEVEL_LIGHT_COUNT; break;
		case MODERATE: remainCount = REMAIN_LEVEL_MODERATE_COUNT; break;
		case WEIGHT: remainCount = REMAIN_LEVEL_WEIGHT_COUNT; break;
		}

        if (size() <= remainCount) {
        	return false;
        }

        flush();
        while (size() > remainCount) {
        	listCache.remove(remainCount);
        }

        LocalDirectMessage divider = new LocalDirectMessage();
        divider.setDivider(true);
        divider.setLocalDivider(true);
        DirectMessageWrap wrap = new DirectMessageWrap(divider);
        listCache.add(wrap);

        return true;
	}

	@Override
	public void flush() {
		DirectMessageWrap wrap = null;
		List<DirectMessage> listMessage = new ArrayList<DirectMessage>();
		for (int i = 0; i < listCache.size(); i++) {
			try {
			     wrap = listCache.get(i);
			} catch(Exception e) {
				break;
			}
			if (wrap == null || wrap.isLocalCached()) {
				continue;
			}
			
			DirectMessage message = wrap.getWrap(); 
			if (message instanceof LocalDirectMessage 
				&& ((LocalDirectMessage)message).isLocalDivider()) {
				continue;
			}
            listMessage.add(message);
            wrap.setLocalCached(true);
		}

		if (listMessage.size() > 0) {
		    DirectMessageDao dao = new DirectMessageDao(context);
		    dao.batchSave(listMessage, account);
		}
	}

	@Override
	public DirectMessageWrap get(int i) {
		if (i < 0 || i >= size()) {
			return null;
		}

		return listCache.get(i);
	}

	@Override
	public void add(int i, DirectMessageWrap value) {
		if (i < 0 || i > size()) {
			return;
		}

		listCache.add(i, value);
	}

	/** 默认是加前面 **/
	@Override
	public void add(DirectMessageWrap value) {
		listCache.add(0, value);
	}

	public void addAll(int i, List<DirectMessageWrap> value) {
		if (i < 0 || i > size() || ListUtil.isEmpty(value)) {
			return;
		}

		listCache.addAll(i, value);
	}

	public void addAll(List<DirectMessageWrap> value) {
		if (ListUtil.isEmpty(value)) {
			return;
		}
		addAll(0, value);
	}

	@Override
	public void remove(int i) {
		if (i < 0 || i >= size()) {
			return;
		}

		DirectMessageWrap dWrap = get(i);
		if (dWrap == null) {
			return;
		}

		listCache.remove(i);
		if (dWrap.getWrap() == null) {
			return;
		}
		DirectMessageDao dao = new DirectMessageDao(context);
		dao.delete(dWrap.getWrap(), account);
	}

	@Override
	public void remove(DirectMessageWrap value) {
		if (value == null || value.getWrap() == null) {
			return;
		}

		int i = indexOf(value);
		if (i != -1) {
			remove(i);
		}
	}

	@Override
	public List<DirectMessageWrap> read(Paging<DirectMessage> page) {
		List<DirectMessageWrap> listWrap = null;
		if (page == null || page.getPageSize() < 0 || page.getPageIndex() <= 0) {
			return listWrap;
		}
		DirectMessageDao dao = new DirectMessageDao(context);
		Resources res = context.getResources();
		String[] querySqls = res.getStringArray(R.array.db_query_direct_message_sql);

		StringBuffer segement = new StringBuffer();
		DirectMessage max = page.getMax();
		DirectMessage since = page.getSince();
		if (max != null) {
			long maxCreatedAt = max.getCreatedAt().getTime();
			segement.append(" and Created_At < " + maxCreatedAt);
		}
		if (since != null) {
			long sinceCreatedAt = max.getCreatedAt().getTime();
			segement.append(" and Created_At > " + sinceCreatedAt);
		}
        segement.append(" and Is_Divider = 0");
        
		String userId = conversationUser.getUserId();
		String sql = String.format(
			querySqls[3], userId, userId, account.getAccountId(), segement.toString());
		List<DirectMessage> messageList = dao.find(sql, 1, page.getPageSize());

		if (ListUtil.isNotEmpty(messageList)) {
			Date currentDate = new Date();
			listWrap = new ArrayList<DirectMessageWrap>(messageList.size());
			DirectMessageWrap wrap;
			for (DirectMessage message : messageList) {
				wrap = new DirectMessageWrap(message);
				wrap.setLocalCached(true);
				wrap.setReadedTime(currentDate);
                wrap.setReaded(true);

                listWrap.add(wrap);
			}

			LocalDirectMessage divider = new LocalDirectMessage();
			divider.setDivider(true);
			divider.setLocalDivider(true);
			wrap = new DirectMessageWrap(divider);
			listWrap.add(wrap);
		}

		return listWrap;
	}

	@Override
	public void write(DirectMessageWrap value) {
		if (value == null) {
			return;
		}
		DirectMessageDao dao = new DirectMessageDao(context);
		dao.save(value.getWrap(), account);
		value.setLocalCached(true);
	}

	@Override
	public int indexOf(DirectMessageWrap value) {
		int index = -1;
		if (value == null || value.getWrap() == null) {
			return index;
		}

		for (int i = 0; i < size(); i++) {
			DirectMessageWrap temp = get(i);
			if (temp == null) {
				continue;
			}
			DirectMessage wrap = temp.getWrap();
			if (wrap != null && wrap.equals(value.getWrap())) {
				index = i;
				break;
			}
		}
		return index;
	}

	@Override
	public int size() {
		return listCache.size();
	}
}
