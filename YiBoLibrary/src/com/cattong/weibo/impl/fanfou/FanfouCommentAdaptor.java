package com.cattong.weibo.impl.fanfou;

import com.cattong.commons.LibException;
import com.cattong.commons.ServiceProvider;
import com.cattong.entity.Comment;
import com.cattong.entity.Status;

/**
 * @author
 * @version
 **/
class FanfouCommentAdaptor {
	public static Comment createCommentFromStatus(Status status) throws LibException {
		if (null == status) {
			throw new NullPointerException("status is null");
		}

		Comment comment = new Comment();
		comment.setCommentId(status.getStatusId());
		comment.setCreatedAt(status.getCreatedAt());
		comment.setFavorited(status.isFavorited());
		comment.setReplyToStatus(status.getRetweetedStatus());
		comment.setServiceProvider(status.getServiceProvider());
		comment.setSource(status.getSource());
		comment.setText(status.getText());
		comment.setTruncated(status.isTruncated());
		comment.setUser(status.getUser());
		comment.setServiceProvider(ServiceProvider.Fanfou);
		return comment;
	}
}
