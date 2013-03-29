package com.shejiaomao.weibo.service.cache;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.shejiaomao.maobo.R;
import android.content.Context;
import android.content.res.Resources;

import com.cattong.commons.Paging;
import com.cattong.commons.util.ListUtil;
import com.cattong.entity.Comment;
import com.shejiaomao.weibo.db.CommentDao;
import com.shejiaomao.weibo.db.LocalAccount;
import com.shejiaomao.weibo.db.LocalComment;
import com.shejiaomao.weibo.service.cache.wrap.CommentWrap;

public class CommentCache implements ListCache<CommentWrap, Comment> {
	private static final int REMAIN_LEVEL_LIGHT_COUNT = 40;
	private static final int REMAIN_LEVEL_MODERATE_COUNT = 20;
	private static final int REMAIN_LEVEL_WEIGHT_COUNT = 10;

    private Context context = null;
    private LocalAccount account = null;

	private List<CommentWrap> listCache = null;
	public CommentCache(Context context, LocalAccount account) {
		this.context = context;
		this.account = account;
		listCache = new ArrayList<CommentWrap>();
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

        LocalComment divider = new LocalComment();
        divider.setDivider(true);
        divider.setLocalDivider(true);
        CommentWrap wrap = new CommentWrap(divider);
        listCache.add(wrap);

        return true;
	}

	@Override
	public void flush() {
		CommentWrap wrap = null;
		List<Comment> listComment = new ArrayList<Comment>();
		for (int i = 0; i < listCache.size(); i++) {
			try {
			    wrap = listCache.get(i);
			} catch(Exception e) {
				break;
			}
			if (wrap == null || wrap.isLocalCached()) {
				continue;
			}
			
			Comment comment = wrap.getWrap();
			if (comment instanceof LocalComment
				&& ((LocalComment)comment).isLocalDivider()) {
				continue;
			}
			listComment.add(wrap.getWrap());
			wrap.setLocalCached(true);
		}

		if (listComment.size() > 0) {
		    CommentDao dao = new CommentDao(context);
		    dao.batchSave(listComment, account);
		}
	}

	@Override
	public void add(int i, CommentWrap value) {
		if (i < 0 || i > size()) {
			return;
		}

		listCache.add(i, value);
	}

	@Override
	public void add(CommentWrap value) {
		listCache.add(0, value);
	}

	@Override
	public void addAll(int i, List<CommentWrap> value) {
		if (i < 0 ||
			i > size() ||
			value == null ||
			value.size() == 0
		) {
			return;
		}

		listCache.addAll(i, value);
	}

	@Override
	public void addAll(List<CommentWrap> value) {
		if (value == null ||
			value.size() == 0
		) {
			return;
		}
		addAll(0, value);
	}

	@Override
	public CommentWrap get(int i) {
		if (i < 0 || i >= size()) {
			return null;
		}

		return listCache.get(i);
	}

	@Override
	public List<CommentWrap> read(Paging<Comment> page) {
		List<CommentWrap> listWrap = null;
		if (page == null || page.getPageSize() < 0 || page.getPageIndex() <= 0) {
			return listWrap;
		}
		Resources res = context.getResources();
        String[] querySqls = res.getStringArray(R.array.db_query_comment_sql);
        
		Comment max = page.getMax();
		Comment since = page.getSince();
		StringBuffer condition = new StringBuffer("");
		if (max != null) {
			long maxCreatedAt = max.getCreatedAt().getTime();
			String formatSegement = String.format(querySqls[1], maxCreatedAt);
			condition.append(" " + formatSegement);
		}
		if (since != null) {
			long sinceCreatedAt = since.getCreatedAt().getTime();
			String formatSegement = String.format(querySqls[2], sinceCreatedAt);
			condition.append(" " + formatSegement);
		}

		String sql = String.format(
	        querySqls[0], account.getAccountId(), condition.toString());
		
		CommentDao dao = new CommentDao(context);
		List<Comment> listComment = dao.find(sql.toString(), 1, page.getPageSize());
		if (ListUtil.isEmpty(listComment)) {
			return listWrap;
		}
		
		Comment last = null;
		last = listComment.get(listComment.size() - 1);
		//如果最后一条刚好是间隔时，再读取一条
		if (last instanceof LocalComment
			&& ((LocalComment)last).isDivider()
			&& listComment.size() > 1) {
			long maxCreatedAt = last.getCreatedAt().getTime();
			String formatSegement = String.format(querySqls[1], maxCreatedAt);
			sql = String.format(querySqls[0], account.getAccountId(), formatSegement);
			List<Comment> listTemp = dao.find(sql.toString(), 1, 1);
			if (ListUtil.isNotEmpty(listTemp)) {
				listComment.addAll(listTemp);
			}
		}
		
		Date currentDate = new Date();
		listWrap = new ArrayList<CommentWrap>(listComment.size());
		CommentWrap wrap;
		for (Comment comment : listComment) {
			wrap = new CommentWrap(comment);
			wrap.setLocalCached(true);
			wrap.setReadedTime(currentDate);
            wrap.setReaded(true);

            listWrap.add(wrap);
		}

		//处理旧列表最后一条数据;
//		last = null;
//		if (size() > 0) {
//			last = get(size() - 1).getWrap();
//		}
//		if (last != null 
//			&& last instanceof LocalComment
//			&& ((LocalComment)last).isDivider()) {
//			remove(size() - 1);
//		}

		last = listComment.get(listComment.size() - 1);
		if (!(last instanceof LocalComment)
			|| !((LocalComment)last).isDivider()) {
			LocalComment divider = new LocalComment();
			divider.setDivider(true);
			divider.setLocalDivider(true);
			wrap = new CommentWrap(divider);			
			listWrap.add(wrap);
		}

		return listWrap;
	}

	@Override
	public int indexOf(CommentWrap value) {
		int index = -1;
		if (value == null || value.getWrap() == null) {
			return index;
		}

		for (int i = 0; i < size(); i++) {
			CommentWrap temp = get(i);
			if (temp == null) {
				continue;
			}
			Comment wrap = temp.getWrap();
			if (wrap != null && wrap.equals(value.getWrap())) {
				index = i;
				break;
			}
		}
		return index;
	}

	@Override
	public void remove(int i) {
		if (i < 0 || i >= size()) {
			return;
		}

		CommentWrap cWrap = get(i);
		if (cWrap == null) {
			return;
		}
		listCache.remove(i);

		if (cWrap.getWrap() == null) {
			return;
		}
		CommentDao dao = new CommentDao(context);
		dao.delete(cWrap.getWrap(), account);
	}

	@Override
	public void remove(CommentWrap value) {
		if (value == null || value.getWrap() == null) {
			return;
		}

		int i = indexOf(value);
		if (i != -1) {
			remove(i);
		}
	}

	@Override
	public int size() {
		return listCache.size();
	}

	@Override
	public void write(CommentWrap value) {
		if (value == null || value.getWrap() == null) {
			return;
		}

		CommentDao dao = new CommentDao(context);
		dao.save(value.getWrap(), account);
		value.setLocalCached(true);
	}
}
