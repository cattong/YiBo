package net.dev123.mblog;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import net.dev123.commons.Paging;
import net.dev123.commons.util.ListUtil;
import net.dev123.commons.util.StringUtil;
import net.dev123.exception.LibException;
import net.dev123.mblog.entity.Relationship;
import net.dev123.mblog.entity.Status;
import net.dev123.mblog.entity.User;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

//已经完成基本的测试用例
//@Ignore
public class FriendshipMethods {
	private static MicroBlog mBlog = null;

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

			User friend = mBlog.createFriendship(user.getId());
			assertNotNull(friend);
			assertTrue(StringUtil.isNotEmpty(friend.getId()));
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
			
			User friend = mBlog.createFriendship(user.getId());
			assertNotNull(friend);
			assertTrue(StringUtil.isNotEmpty(friend.getId()));

			TestUtil.sleep();

			User destroyFriend = mBlog.destroyFriendship(friend.getId());
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
			relationship = mBlog.showRelationship(me.getId(), user.getId());
		    assertNotNull(relationship);
			assertTrue(relationship.isFollowed());

			//测试关注用户关系
			paging = new Paging<User>();
			listUser = mBlog.getFriends(paging);
			assertTrue(ListUtil.isNotEmpty(listUser));
			user = listUser.get(0);
			assertNotNull(user);

			relationship = mBlog.showRelationship(me.getId(), user.getId());
		    assertNotNull(relationship);
			assertTrue(relationship.isFollowing());
		} catch (LibException e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}
}
