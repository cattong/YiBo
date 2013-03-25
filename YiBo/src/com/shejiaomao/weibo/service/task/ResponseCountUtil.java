package com.shejiaomao.weibo.service.task;

import java.util.List;

import com.cattong.commons.LibException;
import com.cattong.commons.Logger;
import com.cattong.commons.util.ListUtil;
import com.cattong.entity.Status;
import com.cattong.weibo.Weibo;
import com.cattong.weibo.impl.sina.Sina;
import com.cattong.weibo.impl.sohu.Sohu;

public class ResponseCountUtil {
    private static int COUNT_BATCH_MAX_NUM = 100;
    
	static boolean getResponseCounts(List<Status> listStatus, Weibo microBlog) {
		boolean isSuccess = false;
		if (ListUtil.isEmpty(listStatus) || microBlog == null) {
			return isSuccess;
		}
		
		if (!(microBlog instanceof Sina) && !(microBlog instanceof Sohu)) {
			return isSuccess;
		}
		try {
			int times = (int)Math.ceil((double)listStatus.size() / COUNT_BATCH_MAX_NUM);
			int start = 0;
			int end = 0;
			for (int i = 0; i < times; i++) {
				start = i * COUNT_BATCH_MAX_NUM;
				end = start + COUNT_BATCH_MAX_NUM;
				if (end > listStatus.size()) {
					end = listStatus.size();
				}
			    microBlog.getResponseCountList(listStatus.subList(start, end));
		    }
			isSuccess = true;
		} catch (LibException e) {
			if (Logger.isDebug()) e.printStackTrace();
		}
		
		return isSuccess;
	}
	
	public static void getResponseCountsAsync(List<Status> listStatus, Weibo microBlog) {
		if (ListUtil.isEmpty(listStatus) || microBlog == null) {
			return;
		}
		
		QueryBatchStatusCountTask task = new QueryBatchStatusCountTask(listStatus, microBlog);
		task.execute();
	}
}
