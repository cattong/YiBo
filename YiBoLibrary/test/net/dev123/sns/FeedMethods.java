package net.dev123.sns;

import java.util.List;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;

import net.dev123.commons.Paging;
import net.dev123.exception.LibException;
import net.dev123.sns.entity.Post;

public class FeedMethods {
	private static Sns sns = null;

	@BeforeClass
	public static void beforClass() throws LibException {
		sns = TokenConfig.getSns(TokenConfig.currentProvider);
	}

	@Test
	public void share() throws LibException {
		Paging<Post> paging = new Paging<Post>();
		paging.moveToFirst();
		List<Post> posts = sns.getNewsFeed(paging);
		if (posts != null && posts.size() > 0) {
			boolean result = sns.share(posts.get(0));
			Assert.assertTrue(result);
		} else {
			Assert.assertTrue(false);
		}

	}

	/**
	 * 获取最新发布的更新内容
	 *
	 * @return
	 * @throws LibException
	 */
	@Test
	public void getNewsFeed() throws LibException {
		Paging<Post> paging = new Paging<Post>();
		paging.moveToFirst();
		List<Post> posts = sns.getNewsFeed(paging);
		Assert.assertTrue(posts != null && posts.size() > 0);
	}

	/**
	 * 获取指定profile的所有更新内容，
	 *
	 * @param profileId
	 *            profileId可以是userId或者pageId
	 * @return
	 * @throws LibException
	 */
	@Test
	public void getProfileFeed() throws LibException {
		Paging<Post> paging = new Paging<Post>();
		paging.moveToFirst();
		List<Post> posts = sns.getProfileFeed(sns.getUserId(), paging);
		Assert.assertTrue(posts != null && posts.size() > 0);
	}
}
