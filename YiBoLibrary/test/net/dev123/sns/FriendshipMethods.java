package net.dev123.sns;

import java.util.List;

import junit.framework.Assert;
import net.dev123.commons.Paging;
import net.dev123.exception.LibException;
import net.dev123.sns.entity.User;

import org.junit.BeforeClass;
import org.junit.Test;

public class FriendshipMethods {

	private static Sns sns = null;

	@BeforeClass
	public static void beforClass() throws LibException {
		sns = TokenConfig.getSns(TokenConfig.currentProvider);
	}

	@Test
	public void areFriends() throws LibException {
		Paging<String> paging = new Paging<String>();
		paging.setPageSize(500);
		paging.moveToFirst();
		List<String> ids = sns.getFriendsIds(paging);
		String friendId = ids.get(0);
		boolean areFriends = sns.areFriends(sns.getUserId(), friendId);
		Assert.assertTrue(areFriends);
	}

	/**
	 * 得到当前登录用户的好友ID列表。
	 */
	@Test
	public void getFriendIds() throws LibException {
		Paging<String> paging = new Paging<String>();
		paging.moveToFirst();
		paging.setPageSize(500);
		List<String> ids = sns.getFriendsIds(paging);
		Assert.assertTrue(ids != null && ids.size() > 0);
	}

	/**
	 * 得到当前登录用户的好友列表。
	 */
	@Test
	public void getFriends() throws LibException {
		Paging<User> paging = new Paging<User>();
		paging.moveToFirst();
		List<User> users = sns.getFriends(paging);
		Assert.assertTrue(users != null && users.size() > 0);
	}

	@Test
	public void getMutualFriends() throws LibException {
		Paging<String> paging = new Paging<String>();
		paging.moveToFirst();
		paging.setPageSize(500);
		List<String> ids = sns.getFriendsIds(paging);
		String friendId = ids.get(0);
		List<User> users = sns.getMutualFriends(sns.getUserId(), friendId, null);
		Assert.assertTrue(users != null);
	}

}
