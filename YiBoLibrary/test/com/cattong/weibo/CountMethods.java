package com.cattong.weibo;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.cattong.commons.LibException;
import com.cattong.commons.LibResultCode;
import com.cattong.commons.http.auth.Authorization;
import com.cattong.commons.util.ListUtil;
import com.cattong.entity.Comment;
import com.cattong.entity.Status;
import com.cattong.entity.StatusUpdate;
import com.cattong.oauth.Config;
import com.cattong.weibo.entity.ResponseCount;
import com.cattong.weibo.entity.UnreadCount;
import com.cattong.weibo.entity.UnreadType;

//已经完成基本的测试用例
//@Ignore
public class CountMethods {
	private static Weibo weibo = null;

	@BeforeClass
	public static void beforClass() {
		Authorization auth = new Authorization(Config.SP);
		auth.setAccessToken(Config.ACCESS_TOKEN);
        auth.setAccessSecret(Config.ACCESS_SECRET);
		weibo = WeiboFactory.getInstance(auth);
	}

	@AfterClass
	public static void afterClass() {
	}

	@Test
	public void getResponseCount() {
		try {
			String text = "测试接口：getResponseCount，造原数据，" + System.currentTimeMillis();
			StatusUpdate sUpdate = new StatusUpdate(text);
			Status status = weibo.updateStatus(sUpdate);
			assertTrue(status != null);

			TestUtil.sleep();

			ResponseCount count = null;
			count = weibo.getResponseCount(status);
			assertNotNull(count);
			assertTrue(count.getCommentCount() == 0 && count.getRetweetCount() == 0);

			String retweetText = "测试接口：getResponseCount，造转发数据" + System.currentTimeMillis();
			boolean isComment = false;
			Status retweetStatus = weibo.retweetStatus(status.getStatusId(), retweetText, isComment);
			assertNotNull(retweetStatus);

			TestUtil.sleep();

			count = weibo.getResponseCount(status);
			assertNotNull(count);
			assertTrue(count.getCommentCount() == 0 && count.getRetweetCount() == 1);

			//评论
			String commentText = "测试接口：getResponseCount,造评论数据!" + System.currentTimeMillis();
			Comment comment = weibo.createComment(commentText, status.getStatusId());
			assertTrue(comment != null);

			TestUtil.sleep();

			count = weibo.getResponseCount(status);
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
			listStatus = weibo.getPublicTimeline();
			assertTrue(ListUtil.isNotEmpty(listStatus));

			List<ResponseCount> listCount = weibo.getResponseCountList(listStatus);
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
			UnreadCount count = weibo.getUnreadCount();
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
			Boolean isSuccess = weibo.resetUnreadCount(UnreadType.COMMENT);
			assertTrue(isSuccess);

			isSuccess = weibo.resetUnreadCount(UnreadType.DIRECT_MESSAGE);
			assertTrue(isSuccess);

			isSuccess = weibo.resetUnreadCount(UnreadType.FOLLOWER);
			assertTrue(isSuccess);

			isSuccess = weibo.resetUnreadCount(UnreadType.MENTION);
			assertTrue(isSuccess);

		} catch (LibException e) {
			if (e.getErrorCode() != LibResultCode.API_UNSUPPORTED) {
				e.printStackTrace();
			    assertTrue(false);
			}
		}
	}
}
