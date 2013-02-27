package net.dev123.sns.api;

import java.util.List;

import net.dev123.commons.Paging;
import net.dev123.exception.LibException;
import net.dev123.sns.entity.Page;

public interface PageMethods {

	boolean followPage(String pageId) throws LibException;

	boolean unfollowPage(String pageId) throws LibException;

	boolean isPageFollower(String userId, String pageId) throws LibException;

	List<Page> getFollowingPages(String userId, Paging<Page> paging)
			throws LibException;

	boolean isPageAdmin(String pageId) throws LibException;

	Page showPage(String pageId) throws LibException;
}
