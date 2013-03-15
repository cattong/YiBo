package com.cattong.weibo;

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
import com.cattong.entity.Status;
import com.cattong.entity.User;
import com.cattong.oauth.Config;

//已经完成基本的测试用例
//@Ignore
public class TimelineMethods {
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
	
	
	//翻页：单向(SigleStyle)
	@Test
	public void getPublicTimeline() {
		List<Status> listStatus = null;

		try {
			listStatus = weibo.getPublicTimeline();
		} catch (LibException e) {
			e.printStackTrace();
		}
		
		assertTrue(ListUtil.isNotEmpty(listStatus));
	}

    //翻页：自由方式(FreeStyle)
	@Test
	public void getHomeTimeline() {
		List<Status> listStatus = null;
        Paging<Status> paging = new Paging<Status>();

		try {
			listStatus = weibo.getHomeTimeline(paging);
		} catch (LibException e) {
			e.printStackTrace();
			assertTrue(false);
		}

		assertTrue(ListUtil.isNotEmpty(listStatus));
	}

	@Test
	public void getHomeTimeline_params() {
	    try {
			weibo.getHomeTimeline(null);
			assertTrue(false);
		} catch (LibException e) {
			assertTrue(true);
		}
	}
	
	@Test
	public void getHomeTimeline_paging() {
		List<Status> listStatus = null;
        Paging<Status> paging = new Paging<Status>();

		try {
			listStatus = weibo.getHomeTimeline(paging);
		} catch (LibException e) {
			e.printStackTrace();
		}
		
		if (ListUtil.isEmpty(listStatus) || listStatus.size() < 6) {
			assertTrue(false);
		}
		
		Method method = null;
		try {
			method = Weibo.class.getMethod("getHomeTimeline", Paging.class);
		} catch (SecurityException e1) {
			e1.printStackTrace();
			assertTrue(false);
		} catch (NoSuchMethodException e1) {
			e1.printStackTrace();
			assertTrue(false);
		}
		
        //测试上翻
		PagingTest.pageUp(listStatus, weibo, method);
		
		//测试下翻
		PagingTest.pageDown(listStatus, weibo, method);
		
		//测试中间展开
        PagingTest.pageExpand(listStatus, weibo, method);
	}
	
	//翻页：自由方式(FreeStyle)
	@Test
	public void getFriendsTimeline() {
		List<Status> listStatus = null;
        Paging<Status> paging = new Paging<Status>();

		try {
			listStatus = weibo.getFriendsTimeline(paging);
		} catch (LibException e) {
			e.printStackTrace();
		}

		assertTrue(ListUtil.isNotEmpty(listStatus));
	}

	@Test
	public void getFriendsTimeline_params() {
	    try {
			weibo.getFriendsTimeline(null);
			assertTrue(false);
		} catch (LibException e) {
			assertTrue(true);
		}
	}
	
	@Test
	public void getFriendsTimeline_paging() {
		List<Status> listStatus = null;
        Paging<Status> paging = new Paging<Status>();

		try {
			listStatus = weibo.getFriendsTimeline(paging);
		} catch (LibException e) {
			e.printStackTrace();
		}
		
		if (ListUtil.isEmpty(listStatus) || listStatus.size() < 6) {
			assertTrue(false);
		}
		
		Method method = null;
		try {
			method = Weibo.class.getMethod("getFriendsTimeline", Paging.class);
		} catch (SecurityException e1) {
			e1.printStackTrace();
			assertTrue(false);
		} catch (NoSuchMethodException e1) {
			e1.printStackTrace();
			assertTrue(false);
		}
		
        //测试上翻
		PagingTest.pageUp(listStatus, weibo, method);
		
		//测试下翻
		PagingTest.pageDown(listStatus, weibo, method);
		
		//测试中间展开
        PagingTest.pageExpand(listStatus, weibo, method);
	}
	
	//翻页：单向方式(SingleStyle)
	@Test
	public void getUserTimeline() {
		List<Status> listStatus = null;
		Paging<Status> paging = new Paging<Status>();

		try {
			User user = weibo.verifyCredentials();
			listStatus = weibo.getUserTimeline(user.getUserId(), paging);
		} catch (LibException e) {
			e.printStackTrace();
		}

		assertTrue(ListUtil.isNotEmpty(listStatus));
	}

	@Test
	public void getUserTimeline_params() {
	    try {
			weibo.getUserTimeline(null, null);
			assertTrue(false);
		} catch (LibException e) {
			assertTrue(true);
		}
	}
	
	@Test
	public void getUserTimeline_paging() {
		List<Status> listStatus = null;
        Paging<Status> paging = new Paging<Status>();

        User user = null;
		try {
			user = weibo.verifyCredentials();
			listStatus = weibo.getUserTimeline(user.getUserId(), paging);
		} catch (LibException e) {
			e.printStackTrace();
		}
		
		if (ListUtil.isEmpty(listStatus) || listStatus.size() < 6) {
			assertTrue(false);
		}
		
		Method method = null;
		try {
			method = Weibo.class.getMethod("getUserTimeline", String.class, Paging.class);
		} catch (SecurityException e1) {
			e1.printStackTrace();
			assertTrue(false);
		} catch (NoSuchMethodException e1) {
			e1.printStackTrace();
			assertTrue(false);
		}
		
		//测试下翻
		PagingTest.pageDown(listStatus, weibo, method, user.getUserId());
	}
	
	//翻页：自由方式(FreeStyle)
	@Test
	public void getMentions() {
		List<Status> listStatus = null;
		Paging<Status> paging = new Paging<Status>();

		try {
			listStatus = weibo.getMentionTimeline(paging);
		} catch (LibException e) {
			e.printStackTrace();
			assertTrue(false);
		}

		assertTrue(ListUtil.isNotEmpty(listStatus));
	}

	@Test
	public void getMentions_params() {
	    try {
			weibo.getMentionTimeline(null);
			assertTrue(false);
		} catch (LibException e) {
			assertTrue(true);
		}
	}
	
	@Test
	public void getMentions_paging() {
		List<Status> listStatus = null;
        Paging<Status> paging = new Paging<Status>();

		try {
			listStatus = weibo.getMentionTimeline(paging);
		} catch (LibException e) {
			e.printStackTrace();
		}
		
		if (ListUtil.isEmpty(listStatus) || listStatus.size() < 6) {
			assertTrue(false);
		}
		
		Method method = null;
		try {
			method = Weibo.class.getMethod("getMentions", Paging.class);
		} catch (SecurityException e1) {
			e1.printStackTrace();
			assertTrue(false);
		} catch (NoSuchMethodException e1) {
			e1.printStackTrace();
			assertTrue(false);
		}
		
        //测试上翻
		PagingTest.pageUp(listStatus, weibo, method);
		
		//测试下翻
		PagingTest.pageDown(listStatus, weibo, method);
		
		//测试中间展开
        PagingTest.pageExpand(listStatus, weibo, method);
	}
	
}
