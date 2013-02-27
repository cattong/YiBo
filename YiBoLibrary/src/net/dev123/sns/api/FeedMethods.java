package net.dev123.sns.api;

import java.util.List;

import net.dev123.commons.Paging;
import net.dev123.exception.LibException;
import net.dev123.sns.entity.Post;

public interface FeedMethods {

	boolean share(Post post) throws LibException;

	/**
	 * 获取最新发布的更新内容
	 *
	 * @return
	 * @throws LibException
	 */
	List<Post> getNewsFeed(Paging<Post> paging) throws LibException;

	/**
	 * 获取指定profile的所有更新内容，
	 *
	 * @param profileId
	 *            profileId可以是userId或者pageId
	 * @return
	 * @throws LibException
	 */
	List<Post> getProfileFeed(String profileId, Paging<Post> paging) throws LibException;
}
