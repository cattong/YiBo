package com.cattong.weibo;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.cattong.commons.LibException;
import com.cattong.commons.Paging;
import com.cattong.commons.http.auth.Authorization;
import com.cattong.commons.util.ListUtil;
import com.cattong.entity.User;
import com.cattong.oauth.Config;

//已经完成基本的测试用例
//@Ignore
public class UserMethods {
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
	public void showUser() {
		try {
			Paging<User> paging = new Paging<User>();
			List<User> listUser = weibo.getFriends(paging);
			assertTrue(ListUtil.isNotEmpty(listUser));

			User user = listUser.get(0);
			assertNotNull(user);

			User showUser = weibo.showUser(user.getUserId());
			assertNotNull(showUser);
		} catch (LibException e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void showUser_param() {
		try {
			User showUser = weibo.showUser("");
			assertNotNull(showUser);
			assertTrue(false);
		} catch (LibException e) {
			//e.printStackTrace();
		}

		try {
			User showUser = weibo.showUser(null);
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
			listUser = weibo.searchUsers("Neo", paging);
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
			listUser = weibo.searchUsers("", paging);
			assertTrue(ListUtil.isNotEmpty(listUser));

			assertTrue(false);
		} catch (LibException e) {
			//e.printStackTrace();
		}

		try {
			listUser = weibo.searchUsers("Neo", null);
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
			    listUser = weibo.getFriends(paging);
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
			listUser = weibo.getFriends(null);
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
			listUser = weibo.getFriends(paging);
			assertTrue(ListUtil.isNotEmpty(listUser));

			User user = listUser.get(0);
			assertNotNull(user);

			paging = new Paging<User>();
			while (paging.hasNext()) {
				paging.moveToNext();
			    listUser = weibo.getUserFriends(user.getUserId(), paging);
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
			listUser = weibo.getUserFriends("", paging);
			assertTrue(ListUtil.isNotEmpty(listUser));
			assertTrue(false);
		} catch (LibException e) {
			//e.printStackTrace();
			assertTrue(true);
		}

		try {
			listUser = weibo.getUserFriends("shejiaomao", null);
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
			    listUser = weibo.getFollowers(paging);
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
			listUser = weibo.getFriends(null);
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
			listUser = weibo.getFollowers(paging);
			assertTrue(ListUtil.isNotEmpty(listUser));

			User user = listUser.get(0);
			assertNotNull(user);

			paging = new Paging<User>();
			while (paging.hasNext()) {
				paging.moveToNext();
			    listUser = weibo.getUserFollowers(user.getUserId(), paging);
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
			listUser = weibo.getUserFollowers("", paging);
			assertTrue(ListUtil.isNotEmpty(listUser));
			assertTrue(false);
		} catch (LibException e) {
			//e.printStackTrace();
			assertTrue(true);
		}

		try {
			listUser = weibo.getUserFollowers("shejiaomao", null);
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
			User user = weibo.verifyCredentials();
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}
}
