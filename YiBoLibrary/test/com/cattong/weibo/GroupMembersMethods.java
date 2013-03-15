package com.cattong.weibo;

import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.cattong.commons.LibException;
import com.cattong.commons.Paging;
import com.cattong.commons.http.auth.Authorization;
import com.cattong.entity.User;
import com.cattong.oauth.Config;
import com.cattong.weibo.entity.Group;

public class GroupMembersMethods {
	private static Weibo weibo = null;
	private Group testGroup;

	@BeforeClass
	public static void beforClass() {
		Authorization auth = new Authorization(Config.SP);
		auth.setAccessToken(Config.ACCESS_TOKEN);
        auth.setAccessSecret(Config.ACCESS_SECRET);
		weibo = WeiboFactory.getInstance(auth);
	}

	@Before
	public void createTestGroup() throws LibException {
		testGroup = weibo.createGroup("What", false,  "测试" + System.currentTimeMillis());
	}

	@After
	public void destroyTestGroup() throws LibException {
		weibo.destroyGroup(testGroup.getId());
	}

	@Test
	public void getGroupMembers() {
		try {
			Paging<User> paging = new Paging<User>();
			paging.moveToFirst();
			List<User> users = weibo.getFriends(paging);
			weibo.createGroupMember(testGroup.getId(), users.get(0).getUserId());
			paging.moveToFirst();
			List<User> members = weibo.getGroupMembers(testGroup.getId(), paging);
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
			List<User> users = weibo.getFriends(paging);
			Group userList = weibo.createGroupMember(testGroup.getId(), users.get(0).getUserId());
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
			List<User> users = weibo.getFriends(paging);
			weibo.createGroupMember(testGroup.getId(), users.get(0).getUserId());
			Group deleted = weibo.destroyGroupMember(testGroup.getId(), users.get(0).getUserId());
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
			List<User> users = weibo.getFriends(paging);
			String userId = users.get(0).getUserId();
			User user = weibo.showGroupMember(testGroup.getId(), userId);
			Assert.assertTrue(user == null);
			weibo.createGroupMember(testGroup.getId(), users.get(0).getUserId());
			user = weibo.showGroupMember(testGroup.getId(), userId);
			Assert.assertTrue(user != null);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertTrue(false);
		}
	}

}
