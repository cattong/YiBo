package com.cattong.weibo;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.cattong.commons.LibException;
import com.cattong.commons.LibResultCode;
import com.cattong.commons.Paging;
import com.cattong.commons.ServiceProvider;
import com.cattong.commons.http.auth.Authorization;
import com.cattong.commons.util.ListUtil;
import com.cattong.entity.Comment;
import com.cattong.entity.Status;
import com.cattong.entity.StatusUpdate;
import com.cattong.oauth.Config;

//已经完成基本的测试用例
//@Ignore
public class CommentMethods {
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

	//@Ignore
	@Test
	public void createComment() {
		Comment comment = null;

		try {
			String text = "测试评论接口：createComment，造微博数据，" + System.currentTimeMillis();
			StatusUpdate sUpdate = new StatusUpdate(text);
			Status status = weibo.updateStatus(sUpdate);
			assertTrue(status != null);

			TestUtil.sleep();

			//评论
			String commentText = "测试评论接口：createComment,写评论!" + System.currentTimeMillis();
			comment = weibo.createComment(commentText, status.getStatusId());
			assertTrue(comment != null);

			TestUtil.sleep();
			//回复评论
			String replyCommentText = "回复@xxx: 测试回复评论接口：createComment,回复评论!" + System.currentTimeMillis();
			Comment replayComment = weibo.createComment(replyCommentText, status.getStatusId(), comment.getCommentId());
			assertTrue(replayComment != null);
		} catch (LibException e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	//@Ignore
	@Test
	public void destroyComment() {
		try {
			String text = "测试删除评论接口：destroyComment，造微博数据，" + System.currentTimeMillis();
			StatusUpdate sUpdate = new StatusUpdate(text);
			Status status = weibo.updateStatus(sUpdate);
			assertTrue(status != null);

			TestUtil.sleep();
			String commentText = "测试删除评论接口：destroyComment，造评论数据" + System.currentTimeMillis();
			Comment comment = weibo.createComment(commentText, status.getStatusId());
			assertTrue(comment != null);
			assertNotNull(comment.getCommentId());

			Comment destroyComment = weibo.destroyComment(comment.getCommentId());
			assertTrue(destroyComment != null);
			if (comment.getServiceProvider() != ServiceProvider.NetEase) {
			    assertNotNull(destroyComment.getCommentId());
			    assertTrue(destroyComment.getCommentId().equals(comment.getCommentId()));
			}
		} catch (LibException e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	//翻页：单向方式(SingleStyle)
	//@Ignore
	@Test
	public void getCommentsOfStatus() {
		List<Comment> listComment = null;
		Paging<Comment> paging = new Paging<Comment>();

		try {
			String text = "测试获得微博评论列表的接口：getCommentsOfStatus，造微博数据，" + System.currentTimeMillis();
			StatusUpdate sUpdate = new StatusUpdate(text);
			Status status = weibo.updateStatus(sUpdate);
			assertTrue(status != null);

			TestUtil.sleep();
			//评论
			String commentText = "测试获得微博评论列表的接口：getCommentsOfStatus，造评论数据，" + System.currentTimeMillis();
			Comment comment = weibo.createComment(commentText, status.getStatusId());
			assertTrue(comment != null);

			TestUtil.sleep();
			//回复评论
			String replyCommentText = "回复@xxxx: 测试获得微博评论列表的接口：getCommentsOfStatus，造回复评论数据，" + System.currentTimeMillis();
			Comment replayComment = weibo.createComment(replyCommentText, status.getStatusId(), comment.getCommentId());
			assertTrue(replayComment != null);

			TestUtil.sleep();
			listComment = weibo.getCommentsOfStatus(status.getStatusId(), paging);
			assertTrue(ListUtil.isNotEmpty(listComment));
			assertTrue(listComment.size() == 2);

			TestUtil.sleep();
			//分页测试:
			paging = new Paging<Comment>();
			paging.setPageSize(1);
			int i = 0;
			while (paging.hasNext() && i < 2) {
				paging.moveToNext();
				listComment = weibo.getCommentsOfStatus(status.getStatusId(), paging);
				assertTrue(ListUtil.isNotEmpty(listComment));
				assertTrue(listComment.size() == 1);
				i++;
			}
			assertTrue(i == 2);
		} catch (LibException e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	//@Ignore
	@Test
	public void getCommentsOfStatus_param() {
		List<Comment> listComment = null;
		Paging<Comment> paging = new Paging<Comment>();

		try {
			listComment = weibo.getCommentsOfStatus("", paging);
			assertTrue(false);
		} catch (LibException e) {
			assertTrue(true);
		}

		try {
			listComment = weibo.getCommentsOfStatus("123", null);
			assertTrue(false);
		} catch (LibException e) {
			assertTrue(true);
		}

		assertNull(listComment);
	}

	//翻页：自由方式(FreeStyle)
	//@Ignore
	@Test
	public void getCommentTimeline() {
		List<Comment> listComment = null;
		Paging<Comment> paging = new Paging<Comment>();

		try {
			listComment = weibo.getCommentTimeline(paging);
			assertTrue(ListUtil.isNotEmpty(listComment));
		} catch (LibException e) {
			e.printStackTrace();
			if (e.getErrorCode() == LibResultCode.API_UNSUPPORTED) {
				return;
			}
			assertTrue(false);
		}

		Method method = null;
		try {
			method = Weibo.class.getMethod("getCommentsTimeline", Paging.class);
		} catch (SecurityException e1) {
			e1.printStackTrace();
			assertTrue(false);
		} catch (NoSuchMethodException e1) {
			e1.printStackTrace();
			assertTrue(false);
		}

        //测试上翻
		PagingTest.pageUp(listComment, weibo, method);

		//测试下翻
		PagingTest.pageDown(listComment, weibo, method);

		//测试中间展开
        PagingTest.pageExpand(listComment, weibo, method);
	}

	//@Ignore
	@Test
	public void getCommentTimeline_param() {
		List<Comment> listComment = null;

		try {
			listComment = weibo.getCommentTimeline(null);
			assertTrue(false);
			assertNull(listComment);
		} catch (LibException e) {
			if (e.getErrorCode() == LibResultCode.API_UNSUPPORTED) {
				return;
			}
			assertTrue(true);
		}
	}

	//翻页：自由方式(FreeStyle)
	//@Ignore
	@Test
	public void getCommentByMe() {
		List<Comment> listComment = null;
		Paging<Comment> paging = new Paging<Comment>();

		try {
			listComment = weibo.getCommentsByMe(paging);
			assertTrue(ListUtil.isNotEmpty(listComment));
		} catch (LibException e) {
			e.printStackTrace();
			if (e.getErrorCode() == LibResultCode.API_UNSUPPORTED) {
				return;
			}
			assertTrue(false);
		}

		Method method = null;
		try {
			method = Weibo.class.getMethod("getCommentsByMe", Paging.class);
		} catch (SecurityException e1) {
			e1.printStackTrace();
			assertTrue(false);
		} catch (NoSuchMethodException e1) {
			e1.printStackTrace();
			assertTrue(false);
		}

        //测试上翻
		PagingTest.pageUp(listComment, weibo, method);

		//测试下翻
		PagingTest.pageDown(listComment, weibo, method);

		//测试中间展开
        PagingTest.pageExpand(listComment, weibo, method);
	}

	//@Ignore
	@Test
	public void getCommentByMe_param() {
		List<Comment> listComment = null;

		try {
			listComment = weibo.getCommentsByMe(null);
			assertTrue(false);
			assertNull(listComment);
		} catch (LibException e) {
			if (e.getErrorCode() == LibResultCode.API_UNSUPPORTED) {
				return;
			}
			assertTrue(true);
		}
	}

	//翻页：自由方式(FreeStyle)
	//@Ignore
	@Test
	public void getCommentsToMe() {
		List<Comment> listComment = null;
		Paging<Comment> paging = new Paging<Comment>();

		try {
			listComment = weibo.getCommentsToMe(paging);
			assertTrue(ListUtil.isNotEmpty(listComment));
		} catch (LibException e) {
			e.printStackTrace();
			if (e.getErrorCode() == LibResultCode.API_UNSUPPORTED) {
				return;
			}
			assertTrue(false);
		}

		Method method = null;
		try {
			method = Weibo.class.getMethod("getCommentsToMe", Paging.class);
		} catch (SecurityException e1) {
			e1.printStackTrace();
			assertTrue(false);
		} catch (NoSuchMethodException e1) {
			e1.printStackTrace();
			assertTrue(false);
		}

        //测试上翻
		PagingTest.pageUp(listComment, weibo, method);

		//测试下翻
		PagingTest.pageDown(listComment, weibo, method);

		//测试中间展开
        PagingTest.pageExpand(listComment, weibo, method);
	}

	//@Ignore
	@Test
	public void getCommentsToMe_param() {
		List<Comment> listComment = null;

		try {
			listComment = weibo.getCommentsToMe(null);
			assertTrue(false);
			assertNull(listComment);
		} catch (LibException e) {
			if (e.getErrorCode() == LibResultCode.API_UNSUPPORTED) {
				return;
			}
			assertTrue(true);
		}
	}
}
