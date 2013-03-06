package com.cattong.weibo;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.cattong.commons.LibException;
import com.cattong.commons.Paging;
import com.cattong.commons.util.ListUtil;
import com.cattong.commons.util.StringUtil;
import com.cattong.entity.Relationship;
import com.cattong.entity.Status;
import com.cattong.entity.User;
import com.cattong.weibo.Weibo;

//已经完成基本的测试用例
//@Ignore
public class FriendshipMethods {
	private static Weibo mBlog = null;

	@BeforeClass
	public static void beforClass() {
        mBlog = Config.getMicroBlog(Config.currentProvider);
	}

	@AfterClass
	public static void afterClass() {
	}

	@Test
	public void createFriendship() {
		try {
			List<Status> listStatus = mBlog.getPublicTimeline();
			assertTrue(ListUtil.isNotEmpty(listStatus));

			User user = listStatus.get(0).getUser();
			assertNotNull(user);

			User friend = mBlog.createFriendship(user.getUserId());
			assertNotNull(friend);
			assertTrue(StringUtil.isNotEmpty(friend.getUserId()));
		} catch (LibException e) {
			e.printStackTrace();
			assertTrue(false);
		}

	}

	@Test
	public void destroyFriendShip() {
		try {
			List<Status> listStatus = mBlog.getPublicTimeline();
			assertTrue(ListUtil.isNotEmpty(listStatus));

			User user = listStatus.get(0).getUser();
			assertNotNull(user);
			
			TestUtil.sleep();
			
			User friend = mBlog.createFriendship(user.getUserId());
			assertNotNull(friend);
			assertTrue(StringUtil.isNotEmpty(friend.getUserId()));

			TestUtil.sleep();

			User destroyFriend = mBlog.destroyFriendship(friend.getUserId());
			assertNotNull(destroyFriend);
		} catch (LibException e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void showRelationship() {		
		try {
			User me = mBlog.verifyCredentials();
		    assertNotNull(me);

			Paging<User> paging = new Paging<User>();
			List<User> listUser = mBlog.getFollowers(paging);
			assertTrue(ListUtil.isNotEmpty(listUser));
			User user = listUser.get(0);
			assertNotNull(user);

			Relationship relationship = null;
			relationship = mBlog.showRelationship(me.getUserId(), user.getUserId());
		    assertNotNull(relationship);
			assertTrue(relationship.isSourceFollowedByTarget());

			//测试关注用户关系
			paging = new Paging<User>();
			listUser = mBlog.getFriends(paging);
			assertTrue(ListUtil.isNotEmpty(listUser));
			user = listUser.get(0);
			assertNotNull(user);

			relationship = mBlog.showRelationship(me.getUserId(), user.getUserId());
		    assertNotNull(relationship);
			assertTrue(relationship.isSourceFollowingTarget());
		} catch (LibException e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}
}
