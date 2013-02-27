package net.dev123.mblog;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;
import java.util.List;

import net.dev123.commons.Paging;
import net.dev123.commons.util.ListUtil;
import net.dev123.commons.util.StringUtil;
import net.dev123.exception.LibException;
import net.dev123.mblog.entity.DirectMessage;
import net.dev123.mblog.entity.User;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

//已经完成基本的测试用例
//@Ignore
public class DirectMessageMethods {
	private static MicroBlog mBlog = null;

	@BeforeClass
	public static void beforClass() {
        mBlog = Config.getMicroBlog(Config.currentProvider);
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
			listMessage = mBlog.getInboxDirectMessages(paging);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		assertTrue(ListUtil.isNotEmpty(listMessage));
		
		Method method = null;
		try {
			method = MicroBlog.class.getMethod("getInboxDirectMessages", Paging.class);
		} catch (SecurityException e1) {
			e1.printStackTrace();
			assertTrue(false);
		} catch (NoSuchMethodException e1) {
			e1.printStackTrace();
			assertTrue(false);
		}
		
        //测试上翻
		PagingTest.pageUp(listMessage, mBlog, method);
		
		//测试下翻
		PagingTest.pageDown(listMessage, mBlog, method);
		
		//测试中间展开
        PagingTest.pageExpand(listMessage, mBlog, method);
	}

	@Test
	public void getInboxDirectMessages_param() {
		try {
			List<DirectMessage> listMessage = mBlog.getInboxDirectMessages(null);
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
			listMessage = mBlog.getOutboxDirectMessages(paging);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		assertTrue(ListUtil.isNotEmpty(listMessage));
		
		assertTrue(ListUtil.isNotEmpty(listMessage));
		
		Method method = null;
		try {
			method = MicroBlog.class.getMethod("getOutboxDirectMessages", Paging.class);
		} catch (SecurityException e1) {
			e1.printStackTrace();
			assertTrue(false);
		} catch (NoSuchMethodException e1) {
			e1.printStackTrace();
			assertTrue(false);
		}
		
        //测试上翻
		PagingTest.pageUp(listMessage, mBlog, method);
		
		//测试下翻
		PagingTest.pageDown(listMessage, mBlog, method);
		
		//测试中间展开
        PagingTest.pageExpand(listMessage, mBlog, method);
	}

	@Test
	public void getOutboxDirectMessages_param() {		
		try {
			List<DirectMessage> listMessage = mBlog.getOutboxDirectMessages(null);
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
			List<User> listUser = mBlog.getFollowers(paging);
			assertTrue(ListUtil.isNotEmpty(listUser));
			
			User user = listUser.get(0);
			assertNotNull(user);
			
			String text = "测试接口：sendDirectMessage,莫慌，" + System.currentTimeMillis();			
			DirectMessage message = mBlog.sendDirectMessage(user.getDisplayName(), text);
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
			List<DirectMessage> listMessage = mBlog.getInboxDirectMessages(paging);
			assertTrue(ListUtil.isNotEmpty(listMessage));
			DirectMessage message = listMessage.get(0);
			assertNotNull(message);
			
			TestUtil.sleep();
			
			DirectMessage deletedMessage = mBlog.destroyInboxDirectMessage(message.getId());
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
			List<User> listUser = mBlog.getFollowers(paging);
			assertTrue(ListUtil.isNotEmpty(listUser));
			
			User user = listUser.get(0);
			assertNotNull(user);
			
			String text = "测试删除私信接口：destroyOutboxDirectMessage,莫慌，" + System.currentTimeMillis();			
			DirectMessage message = mBlog.sendDirectMessage(user.getDisplayName(), text);
			assertNotNull(message);
			assertTrue(StringUtil.isNotEmpty(message.getId()));
			
			TestUtil.sleep();
			
			DirectMessage deletedMessage = mBlog.destroyOutboxDirectMessage(message.getId());
			assertNotNull(deletedMessage);
			assertTrue(StringUtil.isNotEmpty(deletedMessage.getId()));		
		} catch (LibException e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}
}
