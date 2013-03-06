package com.cattong.weibo;

import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.cattong.commons.LibException;
import com.cattong.commons.Paging;
import com.cattong.entity.User;
import com.cattong.weibo.Weibo;
import com.cattong.weibo.entity.Group;

public class GroupMembersMethods {
	private static Weibo mBlog = null;
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
			mBlog.createGroupMember(testGroup.getId(), users.get(0).getUserId());
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
			Group userList = mBlog.createGroupMember(testGroup.getId(), users.get(0).getUserId());
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
			mBlog.createGroupMember(testGroup.getId(), users.get(0).getUserId());
			Group deleted = mBlog.destroyGroupMember(testGroup.getId(), users.get(0).getUserId());
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
			String userId = users.get(0).getUserId();
			User user = mBlog.showGroupMember(testGroup.getId(), userId);
			Assert.assertTrue(user == null);
			mBlog.createGroupMember(testGroup.getId(), users.get(0).getUserId());
			user = mBlog.showGroupMember(testGroup.getId(), userId);
			Assert.assertTrue(user != null);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertTrue(false);
		}
	}

}
