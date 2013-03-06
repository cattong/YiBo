package com.cattong.weibo;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.cattong.commons.LibException;
import com.cattong.commons.LibResultCode;
import com.cattong.commons.Paging;
import com.cattong.commons.util.ListUtil;
import com.cattong.entity.Status;
import com.cattong.entity.User;
import com.cattong.weibo.Weibo;

//已经完成基本的测试用例
//@Ignore
public class FaviriteMethods {
	private static Weibo mBlog = null;

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

			Status favorite = mBlog.createFavorite(status.getStatusId());
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

			Status favorite = mBlog.createFavorite(status.getStatusId());
			assertNotNull(favorite);

			TestUtil.sleep();

			Status destroyFavorite = mBlog.destroyFavorite(favorite.getStatusId());
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
			    listStatus = mBlog.getFavorites(user.getUserId(), paging);
			    assertTrue(ListUtil.isNotEmpty(listStatus));
	        }
		} catch (LibException e) {
			e.printStackTrace();
			if (e.getErrorCode() != LibResultCode.API_UNSUPPORTED) {
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
			if (e.getErrorCode() != LibResultCode.API_UNSUPPORTED) {
				assertTrue(true);
			}
		}

		try {
			mBlog.getFavorites("12", null);
			assertTrue(false);
		} catch (LibException e) {
			e.printStackTrace();
			if (e.getErrorCode() != LibResultCode.API_UNSUPPORTED) {
				assertTrue(true);
			}
		}
	}
}
