package net.dev123.sns;

import java.util.List;

import junit.framework.Assert;
import net.dev123.commons.Paging;
import net.dev123.commons.util.StringUtil;
import net.dev123.entity.BaseUser;
import net.dev123.exception.LibException;
import net.dev123.sns.entity.User;

import org.junit.BeforeClass;
import org.junit.Test;

public class UserMethods {
	private static Sns sns = null;

	@BeforeClass
	public static void beforClass() throws LibException {
		sns = TokenConfig.getSns(TokenConfig.currentProvider);
	}

	@Test
	public void showUser() throws LibException {
		String userId = sns.getUserId();
		BaseUser user = sns.showUser(userId);
		Assert.assertTrue(user != null);
	};

	@Test
	public void showUsers() throws LibException {
		Paging<String> paging = new Paging<String>();
		paging.moveToFirst();
		List<String> userIds = sns.getFriendsIds(paging);
		List<User> users = sns.showUsers(userIds);
		Assert.assertTrue(users != null);
	};

	@Test
	public void getUserId() throws LibException {
		String userId = sns.getUserId();
		Assert.assertTrue(StringUtil.isNotEmpty(userId));
	};

	@Test
	public void getScreenName() throws LibException {
		String screenName = sns.getScreenName();
		Assert.assertTrue(StringUtil.isNotEmpty(screenName));
	};

}
