package net.dev123.mblog;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import net.dev123.commons.Paging;
import net.dev123.commons.util.ListUtil;
import net.dev123.exception.ExceptionCode;
import net.dev123.exception.LibException;
import net.dev123.mblog.entity.User;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

//已经完成基本的测试用例
//@Ignore
public class SocialGraphMethods {
	private static MicroBlog mBlog = null;

	@BeforeClass
	public static void beforClass() {
        mBlog = Config.getMicroBlog(Config.currentProvider);
	}

	@AfterClass
	public static void afterClass() {
	}

	//翻页：单向(SigleStyle)
	@Test
	public void getFriendsIDs() {
		try {
			List<String> listID = null;
			Paging<String> paging = new Paging<String>();

			while (paging.hasNext()) {
				paging.moveToNext();
			    listID = mBlog.getFriendsIDs(paging);
			    if (paging.hasNext()) {
			        assertTrue(ListUtil.isNotEmpty(listID));
			    }
			    TestUtil.sleep();
			}
		} catch (LibException e) {
			e.printStackTrace();
			if (e.getExceptionCode() != ExceptionCode.UNSUPPORTED_API) {
			    assertTrue(false);
			}
		}
	}

	@Test
	public void getFriendsIDs_param() {
		try {
			mBlog.getFriendsIDs(null);
			assertTrue(false);
		} catch (LibException e) {
			e.printStackTrace();
			assertTrue(true);
		}
	}

	//翻页：单向(SigleStyle)
	@Test
	public void getFriendsIDsByIdentifyName() {
		List<String> listID = null;
		Paging<String> paging = new Paging<String>();
		Paging<User> userPaging = new Paging<User>();

		try {
			List<User> listUser = mBlog.getFriends(userPaging);
			assertTrue(ListUtil.isNotEmpty(listUser));

			User user = listUser.get(0);
			assertNotNull(user);

			String userId = user.getId();
			listID = mBlog.getFriendsIDs(userId, paging);
			assertTrue(ListUtil.isNotEmpty(listID));
		} catch (LibException e) {
			e.printStackTrace();
			if (e.getExceptionCode() != ExceptionCode.UNSUPPORTED_API) {
			    assertTrue(false);
			}
		}
	}

	//翻页：单向(SigleStyle)
	@Test
	public void getFollowersIDs() {
		try {
			List<String> listID = null;
			Paging<String> paging = new Paging<String>();

			while (paging.hasNext()) {
				paging.moveToNext();
			    listID = mBlog.getFollowersIDs(paging);
			    if (paging.hasNext()) {
			        assertTrue(ListUtil.isNotEmpty(listID));
			    }
			    TestUtil.sleep();
			}
		} catch (LibException e) {
			e.printStackTrace();
			if (e.getExceptionCode() != ExceptionCode.UNSUPPORTED_API) {
			    assertTrue(false);
			}
		}
	}

	@Test
	public void getFollowersIDs_param() {
		try {
            mBlog.getFollowersIDs(null);
            assertTrue(false);
		} catch (LibException e) {
			assertTrue(true);
		}
	}

	//翻页：单向(SigleStyle)
	@Test
	public void getFollowersIDsByIdentifyName() {
		List<String> listID = null;
		Paging<String> paging = new Paging<String>();
		Paging<User> userPaging = new Paging<User>();

		try {
			List<User> listUser = mBlog.getFollowers(userPaging);
			assertTrue(ListUtil.isNotEmpty(listUser));

			User user = listUser.get(0);
			assertNotNull(user);

			String userId = user.getId();
			listID = mBlog.getFollowersIDs(userId, paging);
			assertTrue(ListUtil.isNotEmpty(listID));
		} catch (LibException e) {
			e.printStackTrace();
			if (e.getExceptionCode() != ExceptionCode.UNSUPPORTED_API) {
			    assertTrue(false);
			}
		}
	}
}
