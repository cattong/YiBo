package net.dev123.mblog;

import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;
import java.util.List;

import net.dev123.commons.Paging;
import net.dev123.commons.util.ListUtil;
import net.dev123.exception.LibException;
import net.dev123.mblog.entity.Status;
import net.dev123.mblog.entity.User;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

//已经完成基本的测试用例
//@Ignore
public class TimelineMethods {
	private static MicroBlog mBlog = null;

	@BeforeClass
	public static void beforClass() {
        mBlog = Config.getMicroBlog(Config.currentProvider);
	}
    
	@AfterClass
	public static void afterClass() {
	}
	
	
	//翻页：单向(SigleStyle)
	@Test
	public void getPublicTimeline() {
		List<Status> listStatus = null;

		try {
			listStatus = mBlog.getPublicTimeline();
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
			listStatus = mBlog.getHomeTimeline(paging);
		} catch (LibException e) {
			e.printStackTrace();
			assertTrue(false);
		}

		assertTrue(ListUtil.isNotEmpty(listStatus));
	}

	@Test
	public void getHomeTimeline_params() {
	    try {
			mBlog.getHomeTimeline(null);
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
			listStatus = mBlog.getHomeTimeline(paging);
		} catch (LibException e) {
			e.printStackTrace();
		}
		
		if (ListUtil.isEmpty(listStatus) || listStatus.size() < 6) {
			assertTrue(false);
		}
		
		Method method = null;
		try {
			method = MicroBlog.class.getMethod("getHomeTimeline", Paging.class);
		} catch (SecurityException e1) {
			e1.printStackTrace();
			assertTrue(false);
		} catch (NoSuchMethodException e1) {
			e1.printStackTrace();
			assertTrue(false);
		}
		
        //测试上翻
		PagingTest.pageUp(listStatus, mBlog, method);
		
		//测试下翻
		PagingTest.pageDown(listStatus, mBlog, method);
		
		//测试中间展开
        PagingTest.pageExpand(listStatus, mBlog, method);
	}
	
	//翻页：自由方式(FreeStyle)
	@Test
	public void getFriendsTimeline() {
		List<Status> listStatus = null;
        Paging<Status> paging = new Paging<Status>();

		try {
			listStatus = mBlog.getFriendsTimeline(paging);
		} catch (LibException e) {
			e.printStackTrace();
		}

		assertTrue(ListUtil.isNotEmpty(listStatus));
	}

	@Test
	public void getFriendsTimeline_params() {
	    try {
			mBlog.getFriendsTimeline(null);
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
			listStatus = mBlog.getFriendsTimeline(paging);
		} catch (LibException e) {
			e.printStackTrace();
		}
		
		if (ListUtil.isEmpty(listStatus) || listStatus.size() < 6) {
			assertTrue(false);
		}
		
		Method method = null;
		try {
			method = MicroBlog.class.getMethod("getFriendsTimeline", Paging.class);
		} catch (SecurityException e1) {
			e1.printStackTrace();
			assertTrue(false);
		} catch (NoSuchMethodException e1) {
			e1.printStackTrace();
			assertTrue(false);
		}
		
        //测试上翻
		PagingTest.pageUp(listStatus, mBlog, method);
		
		//测试下翻
		PagingTest.pageDown(listStatus, mBlog, method);
		
		//测试中间展开
        PagingTest.pageExpand(listStatus, mBlog, method);
	}
	
	//翻页：单向方式(SingleStyle)
	@Test
	public void getUserTimeline() {
		List<Status> listStatus = null;
		Paging<Status> paging = new Paging<Status>();

		try {
			User user = mBlog.verifyCredentials();
			listStatus = mBlog.getUserTimeline(user.getId(), paging);
		} catch (LibException e) {
			e.printStackTrace();
		}

		assertTrue(ListUtil.isNotEmpty(listStatus));
	}

	@Test
	public void getUserTimeline_params() {
	    try {
			mBlog.getUserTimeline(null, null);
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
			user = mBlog.verifyCredentials();
			listStatus = mBlog.getUserTimeline(user.getId(), paging);
		} catch (LibException e) {
			e.printStackTrace();
		}
		
		if (ListUtil.isEmpty(listStatus) || listStatus.size() < 6) {
			assertTrue(false);
		}
		
		Method method = null;
		try {
			method = MicroBlog.class.getMethod("getUserTimeline", String.class, Paging.class);
		} catch (SecurityException e1) {
			e1.printStackTrace();
			assertTrue(false);
		} catch (NoSuchMethodException e1) {
			e1.printStackTrace();
			assertTrue(false);
		}
		
		//测试下翻
		PagingTest.pageDown(listStatus, mBlog, method, user.getId());
	}
	
	//翻页：自由方式(FreeStyle)
	@Test
	public void getMentions() {
		List<Status> listStatus = null;
		Paging<Status> paging = new Paging<Status>();

		try {
			listStatus = mBlog.getMentions(paging);
		} catch (LibException e) {
			e.printStackTrace();
			assertTrue(false);
		}

		assertTrue(ListUtil.isNotEmpty(listStatus));
	}

	@Test
	public void getMentions_params() {
	    try {
			mBlog.getMentions(null);
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
			listStatus = mBlog.getMentions(paging);
		} catch (LibException e) {
			e.printStackTrace();
		}
		
		if (ListUtil.isEmpty(listStatus) || listStatus.size() < 6) {
			assertTrue(false);
		}
		
		Method method = null;
		try {
			method = MicroBlog.class.getMethod("getMentions", Paging.class);
		} catch (SecurityException e1) {
			e1.printStackTrace();
			assertTrue(false);
		} catch (NoSuchMethodException e1) {
			e1.printStackTrace();
			assertTrue(false);
		}
		
        //测试上翻
		PagingTest.pageUp(listStatus, mBlog, method);
		
		//测试下翻
		PagingTest.pageDown(listStatus, mBlog, method);
		
		//测试中间展开
        PagingTest.pageExpand(listStatus, mBlog, method);
	}
	
}
