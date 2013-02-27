package net.dev123.sns.api;

import net.dev123.exception.LibException;
import net.dev123.sns.entity.Post.PostType;

public interface LikeMethods {

	boolean createLike(String objectId, String ownerId, PostType type)
			throws LibException;

	boolean destroyLike(String objectId, String ownerId, PostType type) throws LibException;

	long getLikeCount(String objectId, String ownerId, PostType type) throws LibException;

}
