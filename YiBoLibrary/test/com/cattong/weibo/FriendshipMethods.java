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
import com.cattong.commons.util.StringUtil;
import com.cattong.entity.Relationship;
import com.cattong.entity.Status;
import com.cattong.entity.User;
import com.cattong.oauth.Config;

//已经完成基本的测试用例
//@Ignore
public class FriendshipMethods {
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
	public void createFriendship() {
		try {
			List<Status> listStatus = weibo.getPublicTimeline();
			assertTrue(ListUtil.isNotEmpty(listStatus));

			User user = listStatus.get(0).getUser();
			assertNotNull(user);

			User friend = weibo.createFriendship(user.getUserId());
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
			List<Status> listStatus = weibo.getPublicTimeline();
			assertTrue(ListUtil.isNotEmpty(listStatus));

			User user = listStatus.get(0).getUser();
			assertNotNull(user);
			
			TestUtil.sleep();
			
			User friend = weibo.createFriendship(user.getUserId());
			assertNotNull(friend);
			assertTrue(StringUtil.isNotEmpty(friend.getUserId()));

			TestUtil.sleep();

			User destroyFriend = weibo.destroyFriendship(friend.getUserId());
			assertNotNull(destroyFriend);
		} catch (LibException e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void showRelationship() {		
		try {
			User me = weibo.verifyCredentials();
		    assertNotNull(me);

			Paging<User> paging = new Paging<User>();
			List<User> listUser = weibo.getFollowers(paging);
			assertTrue(ListUtil.isNotEmpty(listUser));
			User user = listUser.get(0);
			assertNotNull(user);

			Relationship relationship = null;
			relationship = weibo.showRelationship(me.getUserId(), user.getUserId());
		    assertNotNull(relationship);
			assertTrue(relationship.isSourceFollowedByTarget());

			//测试关注用户关系
			paging = new Paging<User>();
			listUser = weibo.getFriends(paging);
			assertTrue(ListUtil.isNotEmpty(listUser));
			user = listUser.get(0);
			assertNotNull(user);

			relationship = weibo.showRelationship(me.getUserId(), user.getUserId());
		    assertNotNull(relationship);
			assertTrue(relationship.isSourceFollowingTarget());
		} catch (LibException e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}
}
