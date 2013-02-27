package net.dev123.sns;

import java.util.List;

import junit.framework.Assert;

import net.dev123.commons.Paging;
import net.dev123.exception.LibException;
import net.dev123.sns.entity.Post;

import org.junit.BeforeClass;
import org.junit.Test;

public class LikeMethods {
	private static Sns sns = null;

	@BeforeClass
	public static void beforClass() throws LibException {
		sns = TokenConfig.getSns(TokenConfig.currentProvider);
	}

	@Test
	public void createLike() throws LibException {
		Paging<Post> paging = new Paging<Post>();
		paging.moveToFirst();
		List<Post> posts = sns.getNewsFeed(paging);
		if (posts != null && posts.size() > 0) {
			Post post = posts.get(0);
			boolean result = sns.createLike(post.getObjectId(), post.getOwner().getProfileId(), post.getPostType());
			Assert.assertTrue(result);
		} else {
			Assert.assertTrue(false);
		}
	}

	@Test
	public void destroyLike() throws LibException {
		Paging<Post> paging = new Paging<Post>();
		paging.moveToFirst();
		List<Post> posts = sns.getNewsFeed(paging);
		if (posts != null && posts.size() > 0) {
			Post post = posts.get(0);
			boolean result = sns.createLike(post.getObjectId(), post.getFrom().getProfileId(), post.getPostType());
			result = sns.destroyLike(post.getObjectId(), post.getOwner().getProfileId(), post.getPostType());
			Assert.assertTrue(result);
		} else {
			Assert.assertTrue(false);
		}
	}

	@Test
	public void getLikeCount() throws LibException {
		Paging<Post> paging = new Paging<Post>();
		paging.moveToFirst();
		List<Post> posts = sns.getNewsFeed(paging);
		if (posts != null && posts.size() > 0) {
			Post post = posts.get(0);
			sns.createLike(post.getObjectId(), post.getOwner().getProfileId(), post.getPostType());
			long count = sns.getLikeCount(post.getObjectId(), post.getOwner().getProfileId(), post.getPostType());
			Assert.assertTrue(count >= 0);
		} else {
			Assert.assertTrue(false);
		}
	}

}
