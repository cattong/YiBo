package net.dev123.mblog;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import net.dev123.commons.Paging;
import net.dev123.commons.util.ListUtil;
import net.dev123.exception.ExceptionCode;
import net.dev123.exception.LibException;
import net.dev123.mblog.entity.User;

import org.junit.BeforeClass;
import org.junit.Test;

public class BlockMethods {

	private static MicroBlog mBlog = null;

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

			blockedUser = mBlog.createBlock(user.getId());
			assertTrue(blockedUser != null);
		} catch (LibException e) {
			if (e.getExceptionCode() != ExceptionCode.UNSUPPORTED_API) {
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

			user = mBlog.createBlock(user.getId());
			unBlockedUser = mBlog.destroyBlock(user.getId());
			assertTrue(unBlockedUser != null);
		} catch (LibException e) {
			if (e.getExceptionCode() != ExceptionCode.UNSUPPORTED_API) {
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

			user = mBlog.createBlock(user.getId());
			exists = mBlog.existsBlock(user.getId());
			assertTrue(exists);

			mBlog.destroyBlock(user.getId());
			notexists = !mBlog.existsBlock(user.getId());
			assertTrue(notexists);
		} catch (LibException e) {
			if (e.getExceptionCode() != ExceptionCode.UNSUPPORTED_API) {
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
			if (e.getExceptionCode() != ExceptionCode.UNSUPPORTED_API) {
			    e.printStackTrace();
			    assertTrue(false);
			}
		}
	}

	@Test
	public void getBlockingUsersIDs(){
		List<String> blockedIds = null;
		try {
			Paging<String> paging = new Paging<String>();
			paging.moveToFirst();

			blockedIds = mBlog.getBlockingUsersIDs(paging);
			assertTrue(blockedIds != null);
		} catch (LibException e) {
			if (e.getExceptionCode() != ExceptionCode.UNSUPPORTED_API) {
			    e.printStackTrace();
			    assertTrue(false);
			}
		}
	}

}
