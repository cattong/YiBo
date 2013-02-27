package net.dev123.sns;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;
import net.dev123.commons.Paging;
import net.dev123.commons.ServiceProvider;
import net.dev123.exception.LibException;
import net.dev123.sns.entity.Page;

import org.junit.BeforeClass;
import org.junit.Test;

public class PageMethods {
	private static Map<ServiceProvider, String> pageMap = new HashMap<ServiceProvider, String>();
	private static String pageId;
	private static Sns sns = null;

	@BeforeClass
	public static void beforClass() throws LibException {
		pageMap.put(ServiceProvider.RenRen, "600536902");
		pageMap.put(ServiceProvider.Facebook, "302556553095158");

		sns = TokenConfig.getSns(TokenConfig.currentProvider);
		pageId = pageMap.get(sns.getAuthorization().getServiceProvider());
	}

	@Test
	public void followPage() throws LibException {
		boolean result = sns.followPage(pageId);
		Assert.assertTrue(result);
	}

	@Test
	public void unfollowPage() throws LibException {
		boolean result = sns.unfollowPage(pageId);
		Assert.assertTrue(result);
	}

	@Test
	public void isPageFollower() throws LibException {
		boolean result = sns.isPageFollower(sns.getUserId(), pageId);
		if (result) {
			sns.unfollowPage(pageId);
			Assert.assertFalse(result);
		} else {
			sns.followPage(pageId);
			Assert.assertTrue(result);
		}
	}

	@Test
	public void getFollowingPages()	throws LibException {
		Paging<Page> paging = new Paging<Page>();
		paging.moveToFirst();
		List<Page> pages = sns.getFollowingPages(sns.getUserId(), paging);
		Assert.assertTrue(pages != null);
	}

	@Test
	public void isPageAdmin() throws LibException {
		boolean result = sns.isPageAdmin(pageId);
		Assert.assertTrue(result);
	}

	@Test
	public void showPage() throws LibException {
		Page page = sns.showPage(pageId);
		Assert.assertTrue(page != null);
	}
}
