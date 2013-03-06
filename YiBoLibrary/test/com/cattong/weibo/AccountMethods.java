package com.cattong.weibo;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.cattong.commons.LibException;
import com.cattong.entity.User;
import com.cattong.weibo.Weibo;
import com.cattong.weibo.entity.RateLimitStatus;

//已经完成基本的测试用例
//@Ignore
public class AccountMethods {
	private static Weibo weibo = null;

	@BeforeClass
	public static void beforClass() {
        weibo = Config.getMicroBlog(Config.currentProvider);
	}

	@AfterClass
	public static void afterClass() {
	}

	@Test
	public void verifyCredentials() {
		User user = null;

		try {
			user = weibo.verifyCredentials();
		} catch (LibException e) {
			e.printStackTrace();
		}

		assertTrue(user != null);
	}

	@Test
	public void getRateLimitStatus() {
		RateLimitStatus limitStatus = null;

		try {
			limitStatus = weibo.getRateLimitStatus();
		} catch (LibException e) {
			e.printStackTrace();
		}

		assertTrue(limitStatus != null);
	}

	@Test
	public void updateProfileImage(){
		User user = null;
		try {
			user = weibo.updateProfileImage(new File("F:\\logo\\128_128.png"));
		} catch (LibException e) {
			e.printStackTrace();
		}
		assertTrue(user != null);
	}

	@Test
	public void updateProfile(){
		User user = null;
		try {
			user = weibo.updateProfile("xx" , "xx9@gmail.com", "http://www.shejiaomao.com", null, "描述测试" + System.currentTimeMillis());
		} catch (LibException e) {
			e.printStackTrace();
		}
		assertTrue(user != null);
	}
}
