package net.dev123.yibo.service.adapter;

import java.util.Date;
import java.util.List;

import net.dev123.commons.util.ListUtil;
import net.dev123.commons.util.StringUtil;
import net.dev123.commons.util.TimeSpanUtil;
import net.dev123.mblog.entity.Comment;
import net.dev123.mblog.entity.Status;
import net.dev123.mblog.entity.User;
import net.dev123.yibo.R;
import net.dev123.yibo.common.EmotionLoader;
import net.dev123.yibo.common.GlobalResource;
import net.dev123.yibo.common.GlobalVars;
import net.dev123.yibo.db.LocalAccount;
import net.dev123.yibo.db.LocalComment;
import net.dev123.yibo.service.cache.wrap.CommentWrap;
import net.dev123.yibo.service.task.ImageLoad4HeadTask;
import android.content.Context;
import android.text.Spannable;
import android.view.LayoutInflater;
import android.view.View;

public class CommentUtil {

	public static View initConvertView(Context context, View convertView) {
		if (convertView != null && isCommentView(convertView)) {
			return convertView;
		}

		LayoutInflater inflater = LayoutInflater.from(context);
		convertView = inflater.inflate(R.layout.list_item_comment, null);
		CommentHolder holder = new CommentHolder(convertView);
	    convertView.setTag(holder);

	    return convertView;
	}

	public static View fillConvertView(View convertView, CommentWrap commentWrap) {
		if (convertView == null
			|| commentWrap == null
			|| commentWrap.getWrap() == null) {
			return null;
		}

		Context context = convertView.getContext();
		CommentHolder holder = (CommentHolder)convertView.getTag();
	    holder.reset();

		Comment comment = commentWrap.getWrap();

        if (GlobalVars.IS_SHOW_HEAD) {
        	holder.ivProfilePicture.setVisibility(View.VISIBLE);
        	User user = comment.getUser();
			String profileUrl = (user == null ? null : user.getProfileImageUrl());
			if (StringUtil.isNotEmpty(profileUrl)) {
				ImageLoad4HeadTask headTask = new ImageLoad4HeadTask(holder.ivProfilePicture, profileUrl, true);
				holder.headTask = headTask;
				headTask.execute();
			}
        } else {
        	holder.ivProfilePicture.setVisibility(View.GONE);
        }

        User user = comment.getUser();
		holder.tvScreenName.setText(user == null ? "" : user.getScreenName());
		boolean isVerfied = (user == null ? false : user.isVerified());
		if (isVerfied) {
		    holder.ivVerify.setVisibility(View.VISIBLE);
		}
		holder.tvCreatedAt.setText(TimeSpanUtil.toTimeSpanString(comment.getCreatedAt()));
		commentWrap.hit();
		if (!commentWrap.isReaded()) {
			holder.tvCreatedAt.setTextColor(GlobalResource.getStatusTimelineUnreadColor(context));
			if (commentWrap.getHit() > 2) {
				commentWrap.setReaded(true);
			}
		}

		Spannable textSpan = EmotionLoader.getEmotionSpannable(comment.getServiceProvider(), comment.getText());
		holder.tvText.setText(textSpan);

		Comment replyToComment = comment.getInReplyToComment();
		Status replyToStatus = comment.getInReplyToStatus();
		String text = null;
		if (replyToComment != null) {
			text = String.format(
				GlobalResource.getCommentReplyFormat(context), replyToComment.getText()
			);
		} else if (replyToStatus != null) {
			text = String.format(
				GlobalResource.getCommentFormat(context), replyToStatus.getText()
			);
		}
		
		Spannable replyTextSpan = EmotionLoader.getEmotionSpannable(comment.getServiceProvider(), text);
		holder.tvReplyText.setText(replyTextSpan);

		return convertView;
	}

	public static LocalComment createDividerComment(List<Comment> commentList, LocalAccount account) {
		if (ListUtil.isEmpty(commentList) || account == null) {
			return null;
		}

		Comment comment = commentList.get(commentList.size() - 1);
		StringBuffer newId = new StringBuffer(comment.getId());
		char c = newId.charAt(newId.length() - 1);
		byte b = (byte)((int)c - 1);
		newId.setCharAt(newId.length() - 1, (char)b);

		LocalComment dividerComment = new LocalComment();
		dividerComment.setId(newId.toString());
		dividerComment.setAccountId(account.getAccountId());
		dividerComment.setServiceProvider(account.getServiceProvider());

		Date createdAt = new Date(comment.getCreatedAt().getTime() -1);
		dividerComment.setCreatedAt(createdAt);
		dividerComment.setDivider(true);
		dividerComment.setText("divider");

		return dividerComment;
	}

	private static boolean isCommentView(View convertView) {
		boolean isCommentView = false;
		try {
		     View view = convertView.findViewById(R.id.ivProfilePicture);
		     if (view != null) {
		    	 isCommentView = true;
		     }
		} catch(Exception e) {
		}

		return isCommentView;
	}
}
