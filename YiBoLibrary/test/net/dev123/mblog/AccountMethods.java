package net.dev123.mblog;

import static org.junit.Assert.assertTrue;

import java.io.File;

import net.dev123.exception.LibException;
import net.dev123.mblog.entity.RateLimitStatus;
import net.dev123.mblog.entity.User;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

//已经完成基本的测试用例
//@Ignore
public class AccountMethods {
	private static MicroBlog mBlog = null;

	@BeforeClass
	public static void beforClass() {
        mBlog = Config.getMicroBlog(Config.currentProvider);
	}

	@AfterClass
	public static void afterClass() {
	}

	@Test
	public void verifyCredentials() {
		User user = null;

		try {
			user = mBlog.verifyCredentials();
		} catch (LibException e) {
			e.printStackTrace();
		}

		assertTrue(user != null);
	}

	@Test
	public void getRateLimitStatus() {
		RateLimitStatus limitStatus = null;

		try {
			limitStatus = mBlog.getRateLimitStatus();
		} catch (LibException e) {
			e.printStackTrace();
		}

		assertTrue(limitStatus != null);
	}

	@Test
	public void updateProfileImage(){
		User user = null;
		try {
			user = mBlog.updateProfileImage(new File("F:\\YiBo SVN\\projects\\YiBo\\documents\\界面设计二期\\logo\\128_128.png"));
		} catch (LibException e) {
			e.printStackTrace();
		}
		assertTrue(user != null);
	}

	@Test
	public void updateProfile(){
		User user = null;
		try {
			user = mBlog.updateProfile("YiBoMM" , "YiBo.M9@gmail.com", "http://www.dev123.net", null, "描述测试" + System.currentTimeMillis());
		} catch (LibException e) {
			e.printStackTrace();
		}
		assertTrue(user != null);
	}
}
