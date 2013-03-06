package com.cattong.weibo;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.cattong.commons.LibException;
import com.cattong.commons.LibResultCode;
import com.cattong.commons.Paging;
import com.cattong.commons.util.ListUtil;
import com.cattong.entity.User;
import com.cattong.weibo.Weibo;

public class BlockMethods {

	private static Weibo mBlog = null;

	@BeforeClass
	public static void beforClass() {
        mBlog = Config.getMicroBlog(Config.currentProvider);
	}

	@Test
	public void createBlock() {
		User blockedUser = null;
		try {
			Paging<User> userPaging = new Paging<User>();
			List<User> friends = mBlog.getFriends(userPaging);
			assertTrue(ListUtil.isNotEmpty(friends));
			User user = friends.get(0);
			assertNotNull(user);

			blockedUser = mBlog.createBlock(user.getUserId());
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
			List<User> friends = mBlog.getFriends(userPaging);
			assertTrue(ListUtil.isNotEmpty(friends));
			User user = friends.get(0);
			assertNotNull(user);

			user = mBlog.createBlock(user.getUserId());
			unBlockedUser = mBlog.destroyBlock(user.getUserId());
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
			List<User> friends = mBlog.getFriends(userPaging);
			assertTrue(ListUtil.isNotEmpty(friends));
			User user = friends.get(0);
			assertNotNull(user);

			user = mBlog.createBlock(user.getUserId());
			
			assertTrue(exists);

			mBlog.destroyBlock(user.getUserId());
			
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

			blocked = mBlog.getBlockingUsers(paging);
			assertTrue(blocked != null);
		} catch (LibException e) {
			if (e.getErrorCode() != LibResultCode.API_UNSUPPORTED) {
			    e.printStackTrace();
			    assertTrue(false);
			}
		}
	}

}
