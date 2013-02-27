package net.dev123.mblog;

import java.util.List;

import junit.framework.Assert;
import net.dev123.commons.Paging;
import net.dev123.exception.LibException;
import net.dev123.mblog.entity.User;
import net.dev123.mblog.entity.Group;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class GroupMembersMethods {
	private static MicroBlog mBlog = null;
	private Group testGroup;

	@BeforeClass
	public static void beforClass() {
        mBlog = Config.getMicroBlog(Config.currentProvider);
	}

	@Before
	public void createTestGroup() throws LibException {
		testGroup = mBlog.createGroup("What", false,  "测试" + System.currentTimeMillis());
	}

	@After
	public void destroyTestGroup() throws LibException {
		mBlog.destroyGroup(testGroup.getId());
	}

	@Test
	public void getGroupMembers() {
		try {
			Paging<User> paging = new Paging<User>();
			paging.moveToFirst();
			List<User> users = mBlog.getFriends(paging);
			mBlog.createGroupMember(testGroup.getId(), users.get(0).getId());
			paging.moveToFirst();
			List<User> members = mBlog.getGroupMembers(testGroup.getId(), paging);
			Assert.assertTrue(members.size() == 1);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertTrue(false);
		}

	}

	@Test
	public void addGroupMember() {
		try {
			Paging<User> paging = new Paging<User>();
			paging.moveToFirst();
			List<User> users = mBlog.getFriends(paging);
			Group userList = mBlog.createGroupMember(testGroup.getId(), users.get(0).getId());
			Assert.assertTrue(userList != null);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertTrue(false);
		}
	}

	@Test
	public void addGroupMembers() {

	}

	@Test
	public void deleteGroupMember() {
		try {
			Paging<User> paging = new Paging<User>();
			paging.moveToFirst();
			List<User> users = mBlog.getFriends(paging);
			mBlog.createGroupMember(testGroup.getId(), users.get(0).getId());
			Group deleted = mBlog.destroyGroupMember(testGroup.getId(), users.get(0).getId());
			Assert.assertTrue(deleted != null);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertTrue(false);
		}
	}

	@Test
	public void showGroupMembership() {
		try {
			Paging<User> paging = new Paging<User>();
			paging.moveToFirst();
			List<User> users = mBlog.getFriends(paging);
			String userId = users.get(0).getId();
			User user = mBlog.showGroupMember(testGroup.getId(), userId);
			Assert.assertTrue(user == null);
			mBlog.createGroupMember(testGroup.getId(), users.get(0).getId());
			user = mBlog.showGroupMember(testGroup.getId(), userId);
			Assert.assertTrue(user != null);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertTrue(false);
		}
	}

}
