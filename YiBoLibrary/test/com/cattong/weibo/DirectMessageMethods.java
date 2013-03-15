package com.cattong.weibo;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.cattong.commons.LibException;
import com.cattong.commons.Paging;
import com.cattong.commons.http.auth.Authorization;
import com.cattong.commons.util.ListUtil;
import com.cattong.commons.util.StringUtil;
import com.cattong.entity.User;
import com.cattong.oauth.Config;
import com.cattong.weibo.entity.DirectMessage;

//已经完成基本的测试用例
//@Ignore
public class DirectMessageMethods {
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

	//翻页：自由方式(FreeStyle)
	@Test
	public void getInboxDirectMessages() {
		List<DirectMessage> listMessage = null;
		Paging<DirectMessage> paging = new Paging<DirectMessage>();
		
		try {
			listMessage = weibo.getInboxDirectMessages(paging);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		assertTrue(ListUtil.isNotEmpty(listMessage));
		
		Method method = null;
		try {
			method = Weibo.class.getMethod("getInboxDirectMessages", Paging.class);
		} catch (SecurityException e1) {
			e1.printStackTrace();
			assertTrue(false);
		} catch (NoSuchMethodException e1) {
			e1.printStackTrace();
			assertTrue(false);
		}
		
        //测试上翻
		PagingTest.pageUp(listMessage, weibo, method);
		
		//测试下翻
		PagingTest.pageDown(listMessage, weibo, method);
		
		//测试中间展开
        PagingTest.pageExpand(listMessage, weibo, method);
	}

	@Test
	public void getInboxDirectMessages_param() {
		try {
			List<DirectMessage> listMessage = weibo.getInboxDirectMessages(null);
			assertNull(listMessage);
			assertTrue(false);
		} catch (LibException e) {
			assertTrue(true);
		}
	}
	
	//翻页：自由方式(FreeStyle)
	@Test
	public void getOutboxDirectMessages() {
		List<DirectMessage> listMessage = null;
		Paging<DirectMessage> paging = new Paging<DirectMessage>();
		
		try {
			listMessage = weibo.getOutboxDirectMessages(paging);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		assertTrue(ListUtil.isNotEmpty(listMessage));
		
		assertTrue(ListUtil.isNotEmpty(listMessage));
		
		Method method = null;
		try {
			method = Weibo.class.getMethod("getOutboxDirectMessages", Paging.class);
		} catch (SecurityException e1) {
			e1.printStackTrace();
			assertTrue(false);
		} catch (NoSuchMethodException e1) {
			e1.printStackTrace();
			assertTrue(false);
		}
		
        //测试上翻
		PagingTest.pageUp(listMessage, weibo, method);
		
		//测试下翻
		PagingTest.pageDown(listMessage, weibo, method);
		
		//测试中间展开
        PagingTest.pageExpand(listMessage, weibo, method);
	}

	@Test
	public void getOutboxDirectMessages_param() {		
		try {
			List<DirectMessage> listMessage = weibo.getOutboxDirectMessages(null);
			assertNull(listMessage);
			assertTrue(false);
		} catch (LibException e) {
			assertTrue(true);
		}
	}
	
	@Test
	public void sendDirectMessage() {
		try {
			Paging<User> paging = new Paging<User>();
			List<User> listUser = weibo.getFollowers(paging);
			assertTrue(ListUtil.isNotEmpty(listUser));
			
			User user = listUser.get(0);
			assertNotNull(user);
			
			String text = "测试接口：sendDirectMessage,莫慌，" + System.currentTimeMillis();			
			DirectMessage message = weibo.sendDirectMessage(user.getDisplayName(), text);
			assertNotNull(message);
			assertTrue(StringUtil.isNotEmpty(message.getId()));
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void destroyInboxDirectMessage() {
		try {
			Paging<DirectMessage> paging = new Paging<DirectMessage>();
			List<DirectMessage> listMessage = weibo.getInboxDirectMessages(paging);
			assertTrue(ListUtil.isNotEmpty(listMessage));
			DirectMessage message = listMessage.get(0);
			assertNotNull(message);
			
			TestUtil.sleep();
			
			DirectMessage deletedMessage = weibo.destroyInboxDirectMessage(message.getId());
			assertNotNull(deletedMessage);
			assertTrue(StringUtil.isNotEmpty(deletedMessage.getId()));	
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}
	
	@Test
	public void destroyOutboxDirectMessage() {
		try {
			Paging<User> paging = new Paging<User>();
			List<User> listUser = weibo.getFollowers(paging);
			assertTrue(ListUtil.isNotEmpty(listUser));
			
			User user = listUser.get(0);
			assertNotNull(user);
			
			String text = "测试删除私信接口：destroyOutboxDirectMessage,莫慌，" + System.currentTimeMillis();			
			DirectMessage message = weibo.sendDirectMessage(user.getDisplayName(), text);
			assertNotNull(message);
			assertTrue(StringUtil.isNotEmpty(message.getId()));
			
			TestUtil.sleep();
			
			DirectMessage deletedMessage = weibo.destroyOutboxDirectMessage(message.getId());
			assertNotNull(deletedMessage);
			assertTrue(StringUtil.isNotEmpty(deletedMessage.getId()));		
		} catch (LibException e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}
}
