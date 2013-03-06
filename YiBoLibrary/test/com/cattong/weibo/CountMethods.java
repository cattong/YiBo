package com.cattong.weibo;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.cattong.commons.LibException;
import com.cattong.commons.LibResultCode;
import com.cattong.commons.util.ListUtil;
import com.cattong.entity.Comment;
import com.cattong.entity.Status;
import com.cattong.entity.StatusUpdate;
import com.cattong.weibo.entity.ResponseCount;
import com.cattong.weibo.entity.UnreadCount;
import com.cattong.weibo.entity.UnreadType;

//已经完成基本的测试用例
//@Ignore
public class CountMethods {
	private static Weibo mBlog = null;

	@BeforeClass
	public static void beforClass() {
        mBlog = Config.getMicroBlog(Config.currentProvider);
	}

	@AfterClass
	public static void afterClass() {
	}

	@Test
	public void getResponseCount() {
		try {
			String text = "测试接口：getResponseCount，造原数据，" + System.currentTimeMillis();
			StatusUpdate sUpdate = new StatusUpdate(text);
			Status status = mBlog.updateStatus(sUpdate);
			assertTrue(status != null);

			TestUtil.sleep();

			ResponseCount count = null;
			count = mBlog.getResponseCount(status);
			assertNotNull(count);
			assertTrue(count.getCommentCount() == 0 && count.getRetweetCount() == 0);

			String retweetText = "测试接口：getResponseCount，造转发数据" + System.currentTimeMillis();
			boolean isComment = false;
			Status retweetStatus = mBlog.retweetStatus(status.getStatusId(), retweetText, isComment);
			assertNotNull(retweetStatus);

			TestUtil.sleep();

			count = mBlog.getResponseCount(status);
			assertNotNull(count);
			assertTrue(count.getCommentCount() == 0 && count.getRetweetCount() == 1);

			//评论
			String commentText = "测试接口：getResponseCount,造评论数据!" + System.currentTimeMillis();
			Comment comment = mBlog.createComment(commentText, status.getStatusId());
			assertTrue(comment != null);

			TestUtil.sleep();

			count = mBlog.getResponseCount(status);
			assertNotNull(count);
			assertTrue(count.getCommentCount() == 1 && count.getRetweetCount() == 1);
		} catch (LibException e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void getResponseCountList() {
		try {
			List<Status> listStatus = null;
			listStatus = mBlog.getPublicTimeline();
			assertTrue(ListUtil.isNotEmpty(listStatus));

			List<ResponseCount> listCount = mBlog.getResponseCountList(listStatus);
			assertTrue(ListUtil.isNotEmpty(listCount));
			for (ResponseCount count : listCount) {
				assertNotNull(count);
				assertTrue(count.getCommentCount()>= 0 && count.getRetweetCount() >= 0);
			}
		} catch (LibException e) {
			if (e.getErrorCode() != LibResultCode.API_UNSUPPORTED) {
			    assertTrue(false);
			    e.printStackTrace();
			}
		}

	}

	@Test
	public void getUnreadCount() {
		try {
			UnreadCount count = mBlog.getUnreadCount();
			assertNotNull(count);
			assertTrue(count.getCommentCount() >= 0);
			assertTrue(count.getDireceMessageCount() >= 0);
			assertTrue(count.getFollowerCount() >= 0);
			assertTrue(count.getMetionCount() >= 0);
			assertTrue(count.getStatusCount() >= 0);
		} catch (LibException e) {
			if (e.getErrorCode() != LibResultCode.API_UNSUPPORTED) {
			    e.printStackTrace();
			    assertTrue(false);
			}
		}
	}

	@Test
	public void resetUnreadCount() {

		try {
			Boolean isSuccess = mBlog.resetUnreadCount(UnreadType.COMMENT);
			assertTrue(isSuccess);

			isSuccess = mBlog.resetUnreadCount(UnreadType.DIRECT_MESSAGE);
			assertTrue(isSuccess);

			isSuccess = mBlog.resetUnreadCount(UnreadType.FOLLOWER);
			assertTrue(isSuccess);

			isSuccess = mBlog.resetUnreadCount(UnreadType.MENTION);
			assertTrue(isSuccess);

		} catch (LibException e) {
			if (e.getErrorCode() != LibResultCode.API_UNSUPPORTED) {
				e.printStackTrace();
			    assertTrue(false);
			}
		}
	}
}
