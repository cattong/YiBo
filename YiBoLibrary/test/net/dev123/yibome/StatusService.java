package net.dev123.yibome;

import static org.junit.Assert.assertTrue;

import java.util.List;

import net.dev123.commons.Paging;
import net.dev123.commons.ServiceProvider;
import net.dev123.commons.http.auth.OAuthAuthorization;
import net.dev123.commons.util.ListUtil;
import net.dev123.exception.LibException;
import net.dev123.mblog.entity.Status;
import net.dev123.yibome.entity.SubscribeCatalog;

import org.junit.BeforeClass;
import org.junit.Test;

public class StatusService {
	//28804f4de111539bb6da840ff8e0c0f2,iZOLwAmL4H9vmLPdKkVIN19gFq2MHEDG
	private static YiBoMe yiboMe;

	@BeforeClass
	public static void beforClass() {
		OAuthAuthorization auth = new OAuthAuthorization("77c58346232a821559eec11171e16a18", "1GFUUyXDEmF4Nmecwt5CYCiUtwgfrN1h", ServiceProvider.YiBoMe);
		yiboMe = new YiBoMeImpl(auth);
	}

	@Test
	public void getDailyNews() throws LibException {
		Paging<Status> paging = new Paging<Status>();
		paging.moveToNext();
		List<Status> statusList = yiboMe.getStatusSubscribe(SubscribeCatalog.DAILY_NEWS, 
			ServiceProvider.Sina, paging);
		assertTrue(ListUtil.isNotEmpty(statusList));
	}
	
	@Test
	public void getImageStatuses() throws LibException {
		Paging<Status> paging = new Paging<Status>();
		paging.moveToNext();
		List<Status> statusList = yiboMe.getStatusSubscribe(SubscribeCatalog.IMAGE, 
			ServiceProvider.Sina, paging);
		assertTrue(ListUtil.isNotEmpty(statusList));
	}
	
	@Test
	public void getJokeStatuses() throws LibException {
		Paging<Status> paging = new Paging<Status>();
		paging.moveToNext();
		
		List<Status> statusList = yiboMe.getStatusSubscribe(SubscribeCatalog.JOKE, 
			ServiceProvider.Sina, paging);
		assertTrue(ListUtil.isNotEmpty(statusList));
	}
}
