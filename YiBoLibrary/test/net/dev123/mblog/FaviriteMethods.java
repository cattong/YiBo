package net.dev123.mblog;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import net.dev123.commons.Paging;
import net.dev123.commons.util.ListUtil;
import net.dev123.exception.ExceptionCode;
import net.dev123.exception.LibException;
import net.dev123.mblog.entity.Status;
import net.dev123.mblog.entity.User;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

//已经完成基本的测试用例
//@Ignore
public class FaviriteMethods {
	private static MicroBlog mBlog = null;

	@BeforeClass
	public static void beforClass() {
        mBlog = Config.getMicroBlog(Config.currentProvider);
	}

	@AfterClass
	public static void afterClass() {
	}

	@Test
	public void createFavorite() {
		try {
			List<Status> listStatus = mBlog.getPublicTimeline();
			assertTrue(ListUtil.isNotEmpty(listStatus));
			Status status = listStatus.get(0);
			assertNotNull(status);

			Status favorite = mBlog.createFavorite(status.getId());
			assertNotNull(favorite);
		} catch (LibException e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void destroyFavorite() {
		try {
			List<Status> listStatus = mBlog.getPublicTimeline();
			assertTrue(ListUtil.isNotEmpty(listStatus));
			Status status = listStatus.get(0);
			assertNotNull(status);

			Status favorite = mBlog.createFavorite(status.getId());
			assertNotNull(favorite);

			TestUtil.sleep();

			Status destroyFavorite = mBlog.destroyFavorite(favorite.getId());
			assertNotNull(destroyFavorite);
		} catch (LibException e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	//翻页：单向(SigleStyle)
	@Test
	public void getFavorites() {
		Paging<Status> paging = new Paging<Status>();

		try {
			List<Status> listStatus = null;
			while (paging.hasNext()) {
				paging.moveToNext();
			    listStatus = mBlog.getFavorites(paging);
			    assertTrue(ListUtil.isNotEmpty(listStatus) ||
			    	(ListUtil.isEmpty(listStatus) && paging.isLastPage())
			    );
			}
		} catch (LibException e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void getFavorites_param() {
		try {
			List<Status> listStatus = null;
			listStatus = mBlog.getFavorites(null);
			assertNull(listStatus);
			assertTrue(false);
		} catch (LibException e) {
			//e.printStackTrace();
			assertTrue(true);
		}
	}

	//翻页：单向(SigleStyle)
	@Test
	public void getFavoritesByIdentifyName() {

		try {
			Paging<User> userPaging = new Paging<User>();
			List<User> listUser = mBlog.getFollowers(userPaging);
			assertTrue(ListUtil.isNotEmpty(listUser));

			User user = listUser.get(0);
			assertNotNull(user);


			List<Status> listStatus = null;
			Paging<Status> paging = new Paging<Status>();

	        while (paging.hasNext()) {
				paging.moveToNext();
			    listStatus = mBlog.getFavorites(user.getId(), paging);
			    assertTrue(ListUtil.isNotEmpty(listStatus));
	        }
		} catch (LibException e) {
			e.printStackTrace();
			if (e.getExceptionCode() != ExceptionCode.UNSUPPORTED_API) {
				assertTrue(false);
			}
		}
	}

	@Test
	public void getFavoritesByIdentifyName_param() {
		try {
			Paging<Status> paging = new Paging<Status>();
			mBlog.getFavorites("", paging);
			assertTrue(false);
		} catch (LibException e) {
			e.printStackTrace();
			if (e.getExceptionCode() != ExceptionCode.UNSUPPORTED_API) {
				assertTrue(true);
			}
		}

		try {
			mBlog.getFavorites("12", null);
			assertTrue(false);
		} catch (LibException e) {
			e.printStackTrace();
			if (e.getExceptionCode() != ExceptionCode.UNSUPPORTED_API) {
				assertTrue(true);
			}
		}
	}
}
