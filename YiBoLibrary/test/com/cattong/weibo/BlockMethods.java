package com.cattong.weibo;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.cattong.commons.LibException;
import com.cattong.commons.LibResultCode;
import com.cattong.commons.Paging;
import com.cattong.commons.http.auth.Authorization;
import com.cattong.commons.util.ListUtil;
import com.cattong.entity.User;
import com.cattong.oauth.Config;

public class BlockMethods {

	private static Weibo weibo = null;

	@BeforeClass
	public static void beforClass() {
		Authorization auth = new Authorization(Config.SP);
		auth.setAccessToken(Config.ACCESS_TOKEN);
        auth.setAccessSecret(Config.ACCESS_SECRET);
		weibo = WeiboFactory.getInstance(auth);
	}

	@Test
	public void createBlock() {
		User blockedUser = null;
		try {
			Paging<User> userPaging = new Paging<User>();
			List<User> friends = weibo.getFriends(userPaging);
			assertTrue(ListUtil.isNotEmpty(friends));
			User user = friends.get(0);
			assertNotNull(user);

			blockedUser = weibo.createBlock(user.getUserId());
			assertTrue(blockedUser != null);
		} catch (LibException e) {
			if (e.getErrorCode() != LibResultCode.API_UNSUPPORTED) {
			    e.printStackTrace();
			    assertTrue(false);
			}
		}

	}

	@Test
	public void destroyBlock() {
		User unBlockedUser = null;
		try {
			Paging<User> userPaging = new Paging<User>();
			List<User> friends = weibo.getFriends(userPaging);
			assertTrue(ListUtil.isNotEmpty(friends));
			User user = friends.get(0);
			assertNotNull(user);

			user = weibo.createBlock(user.getUserId());
			unBlockedUser = weibo.destroyBlock(user.getUserId());
			assertTrue(unBlockedUser != null);
		} catch (LibException e) {
			if (e.getErrorCode() != LibResultCode.API_UNSUPPORTED) {
			    e.printStackTrace();
			    assertTrue(false);
			}
		}
	}

	@Test
	public void existsBlock(){
		boolean exists = false;
		boolean notexists = false;
		try {
			Paging<User> userPaging = new Paging<User>();
			List<User> friends = weibo.getFriends(userPaging);
			assertTrue(ListUtil.isNotEmpty(friends));
			User user = friends.get(0);
			assertNotNull(user);

			user = weibo.createBlock(user.getUserId());
			
			assertTrue(exists);

			weibo.destroyBlock(user.getUserId());
			
			assertTrue(notexists);
		} catch (LibException e) {
			if (e.getErrorCode() != LibResultCode.API_UNSUPPORTED) {
			    e.printStackTrace();
			    assertTrue(false);
			}
		}

	}

	@Test
	public void getBlockingUsers(){
		List<User> blocked = null;
		try {
			Paging<User> paging = new Paging<User>();
			paging.moveToFirst();

			blocked = weibo.getBlockingUsers(paging);
			assertTrue(blocked != null);
		} catch (LibException e) {
			if (e.getErrorCode() != LibResultCode.API_UNSUPPORTED) {
			    e.printStackTrace();
			    assertTrue(false);
			}
		}
	}

}
