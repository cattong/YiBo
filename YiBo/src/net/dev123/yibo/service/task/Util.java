package net.dev123.yibo.service.task;

import java.util.List;

import net.dev123.commons.util.ListUtil;
import net.dev123.exception.LibException;
import net.dev123.mblog.MicroBlog;
import net.dev123.mblog.entity.Status;
import net.dev123.mblog.sina.Sina;
import net.dev123.mblog.sohu.Sohu;
import net.dev123.yibo.common.Constants;

public class Util {
    private static int COUNT_BATCH_MAX_NUM = 100;
    
	static boolean getResponseCounts(List<Status> listStatus, MicroBlog microBlog) {
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
			if (Constants.DEBUG) e.printStackTrace();
		}
		
		return isSuccess;
	}
	
	public static void getResponseCountsAsync(List<Status> listStatus, MicroBlog microBlog) {
		if (ListUtil.isEmpty(listStatus) || microBlog == null) {
			return;
		}
		
		QueryBatchStatusCountTask task = new QueryBatchStatusCountTask(listStatus, microBlog);
		task.execute();
	}
}
