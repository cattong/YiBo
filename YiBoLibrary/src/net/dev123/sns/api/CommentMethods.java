package net.dev123.sns.api;

import java.util.List;

import net.dev123.commons.Paging;
import net.dev123.exception.LibException;
import net.dev123.sns.entity.Comment;
import net.dev123.sns.entity.Post.PostType;

public interface CommentMethods {

	boolean createComment(String commentText, String objectId, String ownerId,
			PostType type) throws LibException;

	Comment showComment(String commentId) throws LibException;

	boolean destroyComment(String commentId) throws LibException;

	List<Comment> getComments(String objectId, String ownerId, PostType type,
			Paging<Comment> paging) throws LibException;

}
