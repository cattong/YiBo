package com.cattong.sns;

import java.util.List;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;

import com.cattong.commons.LibException;
import com.cattong.commons.Paging;
import com.cattong.commons.util.StringUtil;
import com.cattong.entity.BaseUser;
import com.cattong.sns.entity.User;

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
