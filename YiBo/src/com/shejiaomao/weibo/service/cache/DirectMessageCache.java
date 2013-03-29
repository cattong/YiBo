package com.shejiaomao.weibo.service.cache;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.cattong.commons.Paging;
import com.cattong.commons.util.ListUtil;
import com.cattong.commons.util.StringUtil;
import com.cattong.weibo.entity.DirectMessage;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.shejiaomao.maobo.R;
import com.shejiaomao.weibo.db.BaseDao;
import com.shejiaomao.weibo.db.DirectMessageDao;
import com.shejiaomao.weibo.db.LocalAccount;
import com.shejiaomao.weibo.db.LocalDirectMessage;
import com.shejiaomao.weibo.service.cache.wrap.DirectMessageWrap;

public class DirectMessageCache implements ListCache<DirectMessageWrap, DirectMessage> {
	private static final int REMAIN_LEVEL_LIGHT_COUNT = 40;
	private static final int REMAIN_LEVEL_MODERATE_COUNT = 20;
	private static final int REMAIN_LEVEL_WEIGHT_COUNT = 10;

    private Context context = null;
	private LocalAccount account = null;

	private List<DirectMessageWrap> listCache = null;

	public DirectMessageCache(Context context, LocalAccount account) {
		this.context = context;
		this.account = account;

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
            listMessage.add(wrap.getWrap());
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

	public void addAll(int i, List<DirectMessageWrap> values) {
		if (i < 0 || i > size() || ListUtil.isEmpty(values)) {
			return;
		}

		DirectMessage last = null;
		if (size() > 0) {
			last = get(size() - 1).getWrap();
		}
		if (last != null 
			&& last instanceof LocalDirectMessage
			&& ((LocalDirectMessage)last).isDivider()) {
			remove(size() - 1);
		}

        String myId = account.getUser().getUserId();
        int insertPos = 0;
        boolean isInsert = true;
		for (int j = 0; j < values.size(); j++) {
			DirectMessageWrap newWrap = values.get(j);
			DirectMessage newMessage = newWrap.getWrap();
			if (newMessage instanceof LocalDirectMessage
				&& ((LocalDirectMessage)newMessage).isDivider()) {
				continue;
			}
			String targetId = newMessage.getSenderId();
			if (targetId.equals(myId)) {
				targetId = newMessage.getRecipientId();
			}

			insertPos = listCache.size();
			isInsert = true;
			for (int k = 0; k < listCache.size(); k++) {
				DirectMessageWrap oldWrap = listCache.get(k);
				DirectMessage oldMessage = oldWrap.getWrap();
				if (newMessage.getCreatedAt().after(oldMessage.getCreatedAt())
					&& insertPos == listCache.size()) {
					insertPos = k;
				}
				if (!targetId.equals(oldMessage.getSenderId()) 
					&& !targetId.equals(oldMessage.getRecipientId())) {
                    continue;
				}

				oldWrap.setCoversationCount(oldWrap.getCoversationCount() + 1);
				if (newMessage.getCreatedAt().after(oldMessage.getCreatedAt())) {
				    newWrap.setCoversationCount(oldWrap.getCoversationCount());
				    listCache.remove(k);				    
				} else {
					isInsert = false;
				}
				break;
			}
			if (isInsert) {
			    listCache.add(insertPos, newWrap);
			}
		}

	    LocalDirectMessage divider = new LocalDirectMessage();
	    divider.setDivider(true);
		divider.setLocalDivider(true);
		DirectMessageWrap wrap = new DirectMessageWrap(divider);
		listCache.add(wrap);
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
		
		String myId = account.getUser().getUserId();
		Resources res = context.getResources();
        String[] querySqls = res.getStringArray(R.array.db_query_direct_message_sql);
		String sql = String.format(
			querySqls[0], myId, account.getAccountId(), myId, account.getAccountId()
		);

		StringDao stringDao = new StringDao(context);
		List<String> listConversationId = stringDao.find(sql, page.getPageIndex(), page.getPageSize());
        if (ListUtil.isEmpty(listConversationId)) {
        	return listWrap;
        }

		//如果最后一条刚好是间隔时，再读取一条
        String lastId = listConversationId.get(listConversationId.size() - 1);
		if (lastId == null) {
			listConversationId = stringDao.find(sql, page.getPageIndex(), page.getPageSize() + 1);
		}
		
		Date currentDate = new Date();
		listWrap = new ArrayList<DirectMessageWrap>(listConversationId.size());
		DirectMessageDao dao = new DirectMessageDao(context);
		for (String coversationId : listConversationId) {
			if (StringUtil.isEmpty(coversationId)) {
				continue;
			}
			sql = String.format(querySqls[1], coversationId, coversationId, account.getAccountId());
            DirectMessage lastMessage = dao.query(sql);
            if (lastMessage == null) {
            	continue;
            }
			DirectMessageWrap wrap = new DirectMessageWrap(lastMessage);
			wrap.setLocalCached(true);
			wrap.setReadedTime(currentDate);
            wrap.setReaded(true);

            sql = String.format(querySqls[2], coversationId, coversationId, account.getAccountId());
            String count = stringDao.query(sql);
            wrap.setCoversationCount(Integer.parseInt(count));

            listWrap.add(wrap);
		}

		if (ListUtil.isNotEmpty(listWrap)) {
		    LocalDirectMessage divider = new LocalDirectMessage();
		    divider.setDivider(true);
		    divider.setLocalDivider(true);
		    DirectMessageWrap wrap = new DirectMessageWrap(divider);
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

	public void write(List<DirectMessage> messageList) {
		if (ListUtil.isEmpty(messageList)) {
			return;
		}
		
		List<DirectMessage> filterList = new ArrayList<DirectMessage>();
		for (DirectMessage message : messageList) {
			if (message instanceof LocalDirectMessage
				&& ((LocalDirectMessage)message).isLocalDivider()) {
				continue;
			}
			filterList.add(message);
		}
		
		DirectMessageDao dao = new DirectMessageDao(context);
		dao.batchSave(filterList, account);
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

	public DirectMessage getInboxMax() {
		DirectMessageDao dao = new DirectMessageDao(context);
		String myId = account.getUser().getUserId();
		long accountId = account.getAccountId();
		String sql = "select * from Direct_Message where Account_ID = " +
		    accountId + " and Recipient_ID = '" + myId + "' order by Created_At desc limit 1";
		DirectMessage message = dao.query(sql);
		return message;
	}

	public DirectMessage getOutboxMax() {
		DirectMessageDao dao = new DirectMessageDao(context);
		String myId = account.getUser().getUserId();
		long accountId = account.getAccountId();
		String sql = "select * from Direct_Message where Account_ID = " +
		    accountId + " and Sender_ID = '" + myId + "' order by Created_At desc limit 1";
		DirectMessage message = dao.query(sql);
		return message;
	}

	public DirectMessage getInboxMin() {
		DirectMessageDao dao = new DirectMessageDao(context);
		String myId = account.getUser().getUserId();
		String sql = "select * from Direct_Message where Account_ID = " +
		    account.getAccountId() + " and Recipient_ID = '" + myId + "' order by Created_At asc limit 1";
		DirectMessage message = dao.query(sql);
		return message;
	}

	public DirectMessage getOutboxMin() {
		DirectMessageDao dao = new DirectMessageDao(context);
		String myId = account.getUser().getUserId();
		String sql = "select * from Direct_Message where Account_ID = " +
		    account.getAccountId() + " and Sender_ID = '" + myId + "' order by Created_At asc limit 1";
		DirectMessage message = dao.query(sql);
		return message;
	}

	public class StringDao extends BaseDao<String> {

		public StringDao(Context context) {
			super(context);
		}

		@Override
		public String extractData(SQLiteDatabase sqLiteDatabase, Cursor cursor) {
			String str = cursor.getString(0);
			return str;
		}

	}
}
