package com.cattong.sns;

import java.util.List;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;

import com.cattong.commons.LibException;
import com.cattong.commons.Paging;
import com.cattong.sns.entity.User;

public class FriendshipMethods {

	private static Sns sns = null;

	@BeforeClass
	public static void beforClass() throws LibException {
		sns = TokenConfig.getSns(TokenConfig.currentProvider);
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

}
