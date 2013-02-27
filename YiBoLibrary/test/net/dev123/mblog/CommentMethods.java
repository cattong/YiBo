package net.dev123.mblog;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;
import java.util.List;

import net.dev123.commons.Paging;
import net.dev123.commons.ServiceProvider;
import net.dev123.commons.util.ListUtil;
import net.dev123.entity.StatusUpdate;
import net.dev123.exception.ExceptionCode;
import net.dev123.exception.LibException;
import net.dev123.mblog.entity.Comment;
import net.dev123.mblog.entity.Status;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

//已经完成基本的测试用例
//@Ignore
public class CommentMethods {
	private static MicroBlog mBlog = null;

	@BeforeClass
	public static void beforClass() {
        mBlog = Config.getMicroBlog(Config.currentProvider);
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
			Status status = mBlog.updateStatus(sUpdate);
			assertTrue(status != null);

			TestUtil.sleep();

			//评论
			String commentText = "测试评论接口：createComment,写评论!" + System.currentTimeMillis();
			comment = mBlog.createComment(commentText, status.getId());
			assertTrue(comment != null);

			TestUtil.sleep();
			//回复评论
			String replyCommentText = "回复@yibom9: 测试回复评论接口：createComment,回复评论!" + System.currentTimeMillis();
			Comment replayComment = mBlog.createComment(replyCommentText, status.getId(), comment.getId());
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
			Status status = mBlog.updateStatus(sUpdate);
			assertTrue(status != null);

			TestUtil.sleep();
			String commentText = "测试删除评论接口：destroyComment，造评论数据" + System.currentTimeMillis();
			Comment comment = mBlog.createComment(commentText, status.getId());
			assertTrue(comment != null);
			assertNotNull(comment.getId());

			Comment destroyComment = mBlog.destroyComment(comment.getId());
			assertTrue(destroyComment != null);
			if (comment.getServiceProvider() != ServiceProvider.NetEase) {
			    assertNotNull(destroyComment.getId());
			    assertTrue(destroyComment.getId().equals(comment.getId()));
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
			Status status = mBlog.updateStatus(sUpdate);
			assertTrue(status != null);

			TestUtil.sleep();
			//评论
			String commentText = "测试获得微博评论列表的接口：getCommentsOfStatus，造评论数据，" + System.currentTimeMillis();
			Comment comment = mBlog.createComment(commentText, status.getId());
			assertTrue(comment != null);

			TestUtil.sleep();
			//回复评论
			String replyCommentText = "回复@yibom9: 测试获得微博评论列表的接口：getCommentsOfStatus，造回复评论数据，" + System.currentTimeMillis();
			Comment replayComment = mBlog.createComment(replyCommentText, status.getId(), comment.getId());
			assertTrue(replayComment != null);

			TestUtil.sleep();
			listComment = mBlog.getCommentsOfStatus(status.getId(), paging);
			assertTrue(ListUtil.isNotEmpty(listComment));
			assertTrue(listComment.size() == 2);

			TestUtil.sleep();
			//分页测试:
			paging = new Paging<Comment>();
			paging.setPageSize(1);
			int i = 0;
			while (paging.hasNext() && i < 2) {
				paging.moveToNext();
				listComment = mBlog.getCommentsOfStatus(status.getId(), paging);
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
			listComment = mBlog.getCommentsOfStatus("", paging);
			assertTrue(false);
		} catch (LibException e) {
			assertTrue(true);
		}

		try {
			listComment = mBlog.getCommentsOfStatus("123", null);
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
			listComment = mBlog.getCommentsTimeline(paging);
			assertTrue(ListUtil.isNotEmpty(listComment));
		} catch (LibException e) {
			e.printStackTrace();
			if (e.getExceptionCode() == ExceptionCode.UNSUPPORTED_API) {
				return;
			}
			assertTrue(false);
		}

		Method method = null;
		try {
			method = MicroBlog.class.getMethod("getCommentsTimeline", Paging.class);
		} catch (SecurityException e1) {
			e1.printStackTrace();
			assertTrue(false);
		} catch (NoSuchMethodException e1) {
			e1.printStackTrace();
			assertTrue(false);
		}

        //测试上翻
		PagingTest.pageUp(listComment, mBlog, method);

		//测试下翻
		PagingTest.pageDown(listComment, mBlog, method);

		//测试中间展开
        PagingTest.pageExpand(listComment, mBlog, method);
	}

	//@Ignore
	@Test
	public void getCommentTimeline_param() {
		List<Comment> listComment = null;

		try {
			listComment = mBlog.getCommentsTimeline(null);
			assertTrue(false);
			assertNull(listComment);
		} catch (LibException e) {
			if (e.getExceptionCode() == ExceptionCode.UNSUPPORTED_API) {
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
			listComment = mBlog.getCommentsByMe(paging);
			assertTrue(ListUtil.isNotEmpty(listComment));
		} catch (LibException e) {
			e.printStackTrace();
			if (e.getExceptionCode() == ExceptionCode.UNSUPPORTED_API) {
				return;
			}
			assertTrue(false);
		}

		Method method = null;
		try {
			method = MicroBlog.class.getMethod("getCommentsByMe", Paging.class);
		} catch (SecurityException e1) {
			e1.printStackTrace();
			assertTrue(false);
		} catch (NoSuchMethodException e1) {
			e1.printStackTrace();
			assertTrue(false);
		}

        //测试上翻
		PagingTest.pageUp(listComment, mBlog, method);

		//测试下翻
		PagingTest.pageDown(listComment, mBlog, method);

		//测试中间展开
        PagingTest.pageExpand(listComment, mBlog, method);
	}

	//@Ignore
	@Test
	public void getCommentByMe_param() {
		List<Comment> listComment = null;

		try {
			listComment = mBlog.getCommentsByMe(null);
			assertTrue(false);
			assertNull(listComment);
		} catch (LibException e) {
			if (e.getExceptionCode() == ExceptionCode.UNSUPPORTED_API) {
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
			listComment = mBlog.getCommentsToMe(paging);
			assertTrue(ListUtil.isNotEmpty(listComment));
		} catch (LibException e) {
			e.printStackTrace();
			if (e.getExceptionCode() == ExceptionCode.UNSUPPORTED_API) {
				return;
			}
			assertTrue(false);
		}

		Method method = null;
		try {
			method = MicroBlog.class.getMethod("getCommentsToMe", Paging.class);
		} catch (SecurityException e1) {
			e1.printStackTrace();
			assertTrue(false);
		} catch (NoSuchMethodException e1) {
			e1.printStackTrace();
			assertTrue(false);
		}

        //测试上翻
		PagingTest.pageUp(listComment, mBlog, method);

		//测试下翻
		PagingTest.pageDown(listComment, mBlog, method);

		//测试中间展开
        PagingTest.pageExpand(listComment, mBlog, method);
	}

	//@Ignore
	@Test
	public void getCommentsToMe_param() {
		List<Comment> listComment = null;

		try {
			listComment = mBlog.getCommentsToMe(null);
			assertTrue(false);
			assertNull(listComment);
		} catch (LibException e) {
			if (e.getExceptionCode() == ExceptionCode.UNSUPPORTED_API) {
				return;
			}
			assertTrue(true);
		}
	}
}
