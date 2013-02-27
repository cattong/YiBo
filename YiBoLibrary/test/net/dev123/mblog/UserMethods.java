package net.dev123.mblog;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import net.dev123.commons.Paging;
import net.dev123.commons.util.ListUtil;
import net.dev123.exception.LibException;
import net.dev123.mblog.entity.User;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

//已经完成基本的测试用例
//@Ignore
public class UserMethods {
	private static MicroBlog mBlog = null;

	@BeforeClass
	public static void beforClass() {
        mBlog = Config.getMicroBlog(Config.currentProvider);
	}

	@AfterClass
	public static void afterClass() {
	}


	@Test
	public void showUser() {
		try {
			Paging<User> paging = new Paging<User>();
			List<User> listUser = mBlog.getFriends(paging);
			assertTrue(ListUtil.isNotEmpty(listUser));

			User user = listUser.get(0);
			assertNotNull(user);

			User showUser = mBlog.showUser(user.getId());
			assertNotNull(showUser);
		} catch (LibException e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void showUser_param() {
		try {
			User showUser = mBlog.showUser("");
			assertNotNull(showUser);
			assertTrue(false);
		} catch (LibException e) {
			//e.printStackTrace();
		}

		try {
			User showUser = mBlog.showUser(null);
			assertNotNull(showUser);
			assertTrue(false);
		} catch (LibException e) {
			//e.printStackTrace();
		}
	}

	//翻页：单向(SigleStyle)
	@Test
	public void searchUsers(){
		List<User> listUser = null;
		Paging<User> paging = new Paging<User>();

		try {
			listUser = mBlog.searchUsers("Neo", paging);
			assertTrue(ListUtil.isNotEmpty(listUser));

			paging.moveToNext();
			assertTrue(ListUtil.isNotEmpty(listUser));
		} catch (LibException e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void searchUsers_param(){
		List<User> listUser = null;
		Paging<User> paging = new Paging<User>();

		try {
			listUser = mBlog.searchUsers("", paging);
			assertTrue(ListUtil.isNotEmpty(listUser));

			assertTrue(false);
		} catch (LibException e) {
			//e.printStackTrace();
		}

		try {
			listUser = mBlog.searchUsers("Neo", null);
			assertTrue(ListUtil.isNotEmpty(listUser));

			assertTrue(false);
		} catch (LibException e) {
			//e.printStackTrace();
		}
	}

	//翻页：单向(SigleStyle)
	@Test
	public void getFriends() {
		List<User> listUser = null;
		Paging<User> paging = new Paging<User>();

		try {
			while (paging.hasNext()) {
				paging.moveToNext();
			    listUser = mBlog.getFriends(paging);
			    assertTrue(ListUtil.isNotEmpty(listUser) ||
			    	(ListUtil.isEmpty(listUser) && paging.isLastPage())
			    );
			}
		} catch (LibException e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void getFriends_param() {
		List<User> listUser = null;

		try {
			listUser = mBlog.getFriends(null);
			assertTrue(ListUtil.isNotEmpty(listUser));
			assertTrue(false);
		} catch (LibException e) {
			//e.printStackTrace();
			assertTrue(true);
		}
	}

	//翻页：单向(SigleStyle)
	@Test
	public void getUserFriends() {
		List<User> listUser = null;
		Paging<User> paging = new Paging<User>();

		try {
			listUser = mBlog.getFriends(paging);
			assertTrue(ListUtil.isNotEmpty(listUser));

			User user = listUser.get(0);
			assertNotNull(user);

			paging = new Paging<User>();
			while (paging.hasNext()) {
				paging.moveToNext();
			    listUser = mBlog.getUserFriends(user.getId(), paging);
			    assertTrue(ListUtil.isNotEmpty(listUser) ||
			    	(ListUtil.isEmpty(listUser) && paging.isLastPage())
			    );
			}
		} catch (LibException e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void getUserFriends_param() {
		List<User> listUser = null;
		Paging<User> paging = new Paging<User>();

		try {
			listUser = mBlog.getUserFriends("", paging);
			assertTrue(ListUtil.isNotEmpty(listUser));
			assertTrue(false);
		} catch (LibException e) {
			//e.printStackTrace();
			assertTrue(true);
		}

		try {
			listUser = mBlog.getUserFriends("yibo", null);
			assertTrue(ListUtil.isNotEmpty(listUser));
			assertTrue(false);
		} catch (LibException e) {
			//e.printStackTrace();
			assertTrue(true);
		}
	}

	//翻页：单向(SigleStyle)
	@Test
	public void getFollowers() {
		List<User> listUser = null;
		Paging<User> paging = new Paging<User>();

		try {
			while (paging.hasNext()) {
				paging.moveToNext();
			    listUser = mBlog.getFollowers(paging);
			    assertTrue(ListUtil.isNotEmpty(listUser) ||
			    	(ListUtil.isEmpty(listUser) && paging.isLastPage())
			    );
			}
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void getFollowers_param() {
		List<User> listUser = null;

		try {
			listUser = mBlog.getFriends(null);
			assertTrue(ListUtil.isNotEmpty(listUser));
			assertTrue(false);
		} catch (LibException e) {
			//e.printStackTrace();
			assertTrue(true);
		}
	}

	public void getUserFollowers() {
		List<User> listUser = null;
		Paging<User> paging = new Paging<User>();

		try {
			listUser = mBlog.getFollowers(paging);
			assertTrue(ListUtil.isNotEmpty(listUser));

			User user = listUser.get(0);
			assertNotNull(user);

			paging = new Paging<User>();
			while (paging.hasNext()) {
				paging.moveToNext();
			    listUser = mBlog.getUserFollowers(user.getId(), paging);
			    assertTrue(ListUtil.isNotEmpty(listUser) ||
			    	(ListUtil.isEmpty(listUser) && paging.isLastPage())
			    );
			}

		} catch (LibException e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void getUserFollowers_param() {
		List<User> listUser = null;
		Paging<User> paging = new Paging<User>();

		try {
			listUser = mBlog.getUserFollowers("", paging);
			assertTrue(ListUtil.isNotEmpty(listUser));
			assertTrue(false);
		} catch (LibException e) {
			//e.printStackTrace();
			assertTrue(true);
		}

		try {
			listUser = mBlog.getUserFollowers("yibome", null);
			assertTrue(ListUtil.isNotEmpty(listUser));
			assertTrue(false);
		} catch (LibException e) {
			//e.printStackTrace();
			assertTrue(true);
		}
	}

	@Test
	public void getProfile() {
		try {
			User user = mBlog.verifyCredentials();
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}
}
