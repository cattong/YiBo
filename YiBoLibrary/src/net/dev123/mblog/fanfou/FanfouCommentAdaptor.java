package net.dev123.mblog.fanfou;

import net.dev123.commons.ServiceProvider;
import net.dev123.exception.LibException;
import net.dev123.mblog.entity.Comment;
import net.dev123.mblog.entity.Status;

/**
 * @author Weiping Ye
 * @version 创建时间：2011-8-1 上午11:15:52
 **/
public class FanfouCommentAdaptor {
	public static Comment createCommentFromStatus(Status status) throws LibException {
		if (null == status) {
			throw new NullPointerException("status is null");
		}

		Comment comment = new Comment();
		comment.setId(status.getId());
		comment.setCreatedAt(status.getCreatedAt());
		comment.setFavorited(status.isFavorited());
		comment.setInReplyToStatus(status.getRetweetedStatus());
		comment.setServiceProvider(status.getServiceProvider());
		comment.setSource(status.getSource());
		comment.setText(status.getText());
		comment.setTruncated(status.isTruncated());
		comment.setUser(status.getUser());
		comment.setServiceProvider(ServiceProvider.Fanfou);
		return comment;
	}
}
