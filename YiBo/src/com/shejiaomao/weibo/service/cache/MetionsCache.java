package com.shejiaomao.weibo.service.cache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.cattong.commons.Paging;
import com.cattong.commons.util.ListUtil;
import com.cattong.entity.Status;
import android.content.Context;
import android.content.res.Resources;

import com.shejiaomao.maobo.R;
import com.shejiaomao.weibo.common.GlobalVars;
import com.shejiaomao.weibo.common.StatusCatalog;
import com.shejiaomao.weibo.db.LocalAccount;
import com.shejiaomao.weibo.db.LocalStatus;
import com.shejiaomao.weibo.db.StatusDao;
import com.shejiaomao.weibo.service.cache.wrap.StatusWrap;
import com.shejiaomao.weibo.service.task.ResponseCountUtil;

public class MetionsCache implements ListCache<StatusWrap, Status> {
    private static final int REMAIN_LEVEL_LIGHT_COUNT = 40;
	private static final int REMAIN_LEVEL_MODERATE_COUNT = 20;
	private static final int REMAIN_LEVEL_WEIGHT_COUNT = 10;

    private Context context = null;
	private LocalAccount account = null;

	private List<StatusWrap> listCache = null;

	public MetionsCache(Context context, LocalAccount account) {
		listCache = new ArrayList<StatusWrap>();
        listCache = Collections.synchronizedList(listCache);
        
		this.context = context;
		this.account = account;
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

        while (size() > remainCount) {
        	listCache.remove(remainCount);
        }

        LocalStatus divider = new LocalStatus();
        divider.setDivider(true);
        divider.setLocalDivider(true);
        StatusWrap wrap = new StatusWrap(divider);
        listCache.add(wrap);

        return true;
	}

	@Override
	public void flush() {
		StatusWrap wrap = null;
		List<Status> listStatus = new ArrayList<Status>();
		for (int i = 0; i < listCache.size(); i++) {
			try {
			    wrap = listCache.get(i);
			} catch (Exception e) {
				break;
			}
			if (wrap == null || wrap.isLocalCached()) {
				continue;
			}
			Status status = wrap.getWrap();
			if (status instanceof LocalStatus
				&& ((LocalStatus)status).isLocalDivider()) {
				continue;
			}
            listStatus.add(status);
            wrap.setLocalCached(true);
		}

		if (listStatus.size() > 0) {
            StatusDao dao = new StatusDao(context);
            dao.batchSave(listStatus, StatusCatalog.Mentions, account);
		}
	}

	@Override
	public StatusWrap get(int i) {
		if (i < 0 || i >= size()) {
			return null;
		}

		return listCache.get(i);
	}

	@Override
	public void add(int i, StatusWrap value) {
		if (i < 0 || i > size()) {
			return;
		}

		listCache.add(i, value);
	}

	/** 默认是加前面 **/
	@Override
	public void add(StatusWrap value) {
		if (value == null) {
			return;
		}
		listCache.add(0, value);
	}

	public void addAll(int i, List<StatusWrap> value) {
		if (i < 0 
			|| i > size() 
			|| ListUtil.isEmpty(value)) {
			return;
		}


		listCache.addAll(i, value);
	}

	public void addAll(List<StatusWrap> value) {
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

		StatusWrap sWrap = get(i);
		if (sWrap == null) {
			return;
		}

		listCache.remove(i);
		if (sWrap.getWrap() == null) {
			return;
		}
		StatusDao dao = new StatusDao(context);
		dao.delete(sWrap.getWrap(), account);
	}

	@Override
	public void remove(StatusWrap value) {
		if (value == null || value.getWrap() == null) {
			return;
		}

		int i = indexOf(value);
		if (i != -1) {
			remove(i);
		}
	}

	@Override
	public List<StatusWrap> read(Paging<Status> page) {
		List<StatusWrap> listWrap = null;
		if (page == null 
			|| page.getPageSize() < 0
			|| page.getPageIndex() <= 0 
			|| account.getUser() == null) {
			return listWrap;
		}
		Resources res = context.getResources();
        String[] querySqls = res.getStringArray(R.array.db_query_status_sql);
        
		Status max = page.getMax();
		Status since = page.getSince();
		StringBuffer condition = new StringBuffer();
		if (max != null) {
			long maxCreatedAt = max.getCreatedAt().getTime();
			String formatSegement = String.format(querySqls[1], maxCreatedAt);
			condition.append(" " + formatSegement);
		}
		if (since != null) {
			long sinceCreatedAt = since.getCreatedAt().getTime();
			String formatSegement = String.format(querySqls[1], sinceCreatedAt);
			condition.append(" " + formatSegement);
		}

		String sql = String.format(
	        querySqls[0], account.getAccountId(), 
	        StatusCatalog.Mentions.getCatalogId(), 
	        condition.toString());
			
	    StatusDao dao = new StatusDao(context);
		List<Status> listStatus = dao.find(sql.toString(), 1, page.getPageSize());
		if (ListUtil.isEmpty(listStatus)) {
			return listWrap;
		}
		
		Status last = null;
		last = listStatus.get(listStatus.size() - 1);
		//如果最后一条刚好是间隔时，再读取一条
		if (last instanceof LocalStatus
			&& ((LocalStatus)last).isDivider()
			&& listStatus.size() > 1) {
			//last = listStatus.get(listStatus.size() - 2);
			long maxCreatedAt = last.getCreatedAt().getTime();
			String formatSegement = String.format(querySqls[1], maxCreatedAt);
			sql = String.format(
		        querySqls[0], account.getAccountId(), 
		        StatusCatalog.Mentions.getCatalogId(), 
		        formatSegement);
			List<Status> listTemp = dao.find(sql.toString(), 1, 1);
			if (ListUtil.isNotEmpty(listTemp)) {
				listStatus.addAll(listTemp);
			}
		}
		
		ResponseCountUtil.getResponseCountsAsync(listStatus, GlobalVars.getMicroBlog(account));
	
		Date currentDate = new Date();
		listWrap = new ArrayList<StatusWrap>(listStatus.size());
		StatusWrap wrap;
		for (Status status : listStatus) {
			wrap = new StatusWrap(status);
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
//			&& last instanceof LocalStatus
//			&& ((LocalStatus)last).isDivider()) {
//			remove(size() - 1);
//		}

		//是否添加一条分隔
		last = listStatus.get(listStatus.size() - 1);
		if (!(last instanceof LocalStatus)
			|| !((LocalStatus)last).isDivider()) {
			LocalStatus divider = new LocalStatus();
			divider.setDivider(true);
			divider.setLocalDivider(true);
			wrap = new StatusWrap(divider);
			listWrap.add(wrap);
		}

		return listWrap;
	}

	@Override
	public void write(StatusWrap value) {
		if (value == null) {
			return;
		}
		StatusDao dao = new StatusDao(context);
		dao.save(value.getWrap(),StatusCatalog.Mentions, account);
		value.setLocalCached(true);
	}

	@Override
	public int indexOf(StatusWrap value) {
		int index = -1;
		if (value == null || value.getWrap() == null) {
			return index;
		}

		for (int i = 0; i < size(); i++) {
			StatusWrap temp = get(i);
			if (temp == null) {
				continue;
			}
			Status wrap = temp.getWrap();
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
