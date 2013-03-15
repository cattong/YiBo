package com.cattong.weibo;

import java.util.List;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;

import com.cattong.commons.LibException;
import com.cattong.commons.Paging;
import com.cattong.commons.http.auth.Authorization;
import com.cattong.commons.util.ListUtil;
import com.cattong.entity.Status;
import com.cattong.entity.User;
import com.cattong.oauth.Config;
import com.cattong.weibo.entity.Group;


public class GroupMethods {
	private static Weibo weibo = null;

	@BeforeClass
	public static void beforClass() {
		Authorization auth = new Authorization(Config.SP);
		auth.setAccessToken(Config.ACCESS_TOKEN);
        auth.setAccessSecret(Config.ACCESS_SECRET);
		weibo = WeiboFactory.getInstance(auth);
	}

	@Test
	public void createGroup() {
		try {
			Group group = weibo.createGroup("What", false,  "测试" + System.currentTimeMillis());
			Assert.assertTrue(group != null);
			weibo.destroyGroup(group.getId());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertTrue(false);
		}
	}

	@Test
	public void updateGroup() throws LibException {
		try {
			Group group = weibo.createGroup("What", false,  "测试" + System.currentTimeMillis());
			group = weibo.updateGroup(group.getId(), "更新测试", false, "更新测试");
			Assert.assertTrue("更新测试".equals(group.getName()));
			weibo.destroyGroup(group.getId());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertTrue(false);
		}
	}

	@Test
	public void getGroup() throws LibException {
		List<Group> lists = weibo.getGroups(weibo.getUserId(), new Paging<Group>());
		Assert.assertTrue(ListUtil.isNotEmpty(lists));
	}

	@Test
	public void showGroup() throws LibException {
		try {
			List<Group> lists = weibo.getGroups(weibo.getUserId(), new Paging<Group>());
			Group group = weibo.showGroup(lists.get(0).getId());
			Assert.assertTrue(group != null);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertTrue(false);
		}
	}

	@Test
	public void destroyGroup() throws LibException {
		try {
			Group group = weibo.createGroup("What", false,  "测试" + System.currentTimeMillis());
			Group deleted = weibo.destroyGroup(group.getId());
			Assert.assertTrue(deleted != null);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertTrue(false);
		}
	}

	@Test
	public void getGroupStatuses()throws LibException {
		try {
			Group group = weibo.createGroup("What", false,  "测试" + System.currentTimeMillis());
			Paging<User> paging = new Paging<User>();
			paging.moveToFirst();
			List<User> users = weibo.getFriends(paging);
			for (User user : users) {
				weibo.createGroupMember(group.getId(), user.getUserId());
			}
			Paging<Status> statusPaging = new Paging<Status>();
			statusPaging.moveToFirst();
			List<Status> statusList = weibo.getGroupStatuses(group.getId(), statusPaging);
			Assert.assertTrue(ListUtil.isNotEmpty(statusList));
			weibo.destroyGroup(group.getId());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertTrue(false);
		}
	}

	@Test
	public void getGroupMemberships() throws LibException {
		try {
			Paging<Group> paging = new Paging<Group>();
			paging.moveToFirst();
			weibo.getGroupMemberships("吴鹰", paging);
			if (paging.moveToNext()){
				weibo.getGroupMemberships("吴鹰", paging);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertTrue(false);
		}
	}

	@Test
	public void getGroupSubscriptions() throws LibException {
		try {
			Paging<Group> paging = new Paging<Group>();
			paging.moveToFirst();
			//mBlog.getGroupSubscriptions("社交猫", paging);
			if (paging.moveToNext()){
				weibo.getGroupMemberships("社交猫", paging);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertTrue(false);
		}
	}
	@Test
	public void getAllGroups() throws LibException {

	}
}
