package com.cattong.weibo.impl.tencent;

import com.cattong.commons.LibException;
import com.cattong.commons.LibResultCode;
import com.cattong.commons.ServiceProvider;
import com.cattong.entity.Comment;
import com.cattong.entity.Status;

class TencentCommentAdaptor {
	public static Comment createCommentFromStatus(Status status) throws LibException {
		if (null == status) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
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
		comment.setServiceProvider(ServiceProvider.Tencent);
		return comment;
	}
}
