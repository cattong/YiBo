package com.shejiaomao.weibo.service.adapter;

import java.util.Date;
import java.util.List;

import com.shejiaomao.maobo.R;
import android.content.Context;
import android.text.Spannable;
import android.view.LayoutInflater;
import android.view.View;

import com.cattong.commons.util.ListUtil;
import com.cattong.commons.util.StringUtil;
import com.cattong.commons.util.TimeSpanUtil;
import com.cattong.entity.Comment;
import com.cattong.entity.Status;
import com.cattong.entity.User;
import com.shejiaomao.weibo.common.EmotionLoader;
import com.shejiaomao.weibo.common.GlobalResource;
import com.shejiaomao.weibo.common.GlobalVars;
import com.shejiaomao.weibo.db.LocalAccount;
import com.shejiaomao.weibo.db.LocalComment;
import com.shejiaomao.weibo.service.cache.wrap.CommentWrap;
import com.shejiaomao.weibo.service.task.ImageLoad4HeadTask;

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

		Comment replyToComment = comment.getReplyToComment();
		Status replyToStatus = comment.getReplyToStatus();
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
		StringBuffer newId = new StringBuffer(comment.getCommentId());
		char c = newId.charAt(newId.length() - 1);
		byte b = (byte)((int)c - 1);
		newId.setCharAt(newId.length() - 1, (char)b);

		LocalComment dividerComment = new LocalComment();
		dividerComment.setCommentId(newId.toString());
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
