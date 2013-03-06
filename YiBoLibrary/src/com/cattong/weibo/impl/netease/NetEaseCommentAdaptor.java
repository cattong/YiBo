package com.cattong.weibo.impl.netease;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.cattong.commons.LibException;
import com.cattong.commons.LibResultCode;
import com.cattong.commons.PagableList;
import com.cattong.commons.ServiceProvider;
import com.cattong.commons.util.ParseUtil;
import com.cattong.commons.util.StringUtil;
import com.cattong.entity.Comment;
import com.cattong.entity.Status;
import com.cattong.entity.User;
import com.cattong.weibo.Emotions;

class NetEaseCommentAdaptor {
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
		comment.setServiceProvider(ServiceProvider.NetEase);
		return comment;
	}

	public static List<Comment> createCommentsList(String jsonString) throws LibException{
		try {
			if ("[]".equals(jsonString) || "{}".equals(jsonString)) {
				return new ArrayList<Comment>(0);
			}
			JSONArray jsonList = new JSONArray(jsonString);
			int size = jsonList.length();
			List<Comment> comments = new ArrayList<Comment>(size);
			if (size > 0) {
				for (int i = 0; i < size; i++) {
					comments.add(createComment(jsonList.getJSONObject(i)));
				}
			}
			return comments;
		} catch (JSONException e) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR);
		}
	}

	public static PagableList<Comment> createPagableCommentsList(String jsonString) throws LibException {
		try {
			if ("[]".equals(jsonString) || "{}".equals(jsonString)) {
				return new PagableList<Comment>(0, 0, 0);
			}
			JSONObject json = new JSONObject(jsonString);
			JSONArray jsonList = json.getJSONArray("statuses");
			int size = jsonList.length();
			long nextCursor = json.getLong("cursor_id");
			PagableList<Comment> commentList = new PagableList<Comment>(size, -1, nextCursor);
			for (int i = 0; i < size; i++) {
				commentList.add(createComment(jsonList.getJSONObject(i)));
			}
			return commentList;
		} catch (JSONException e) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR);
		}
	}

	static Comment createComment(JSONObject json) throws LibException {
		try {
			Comment comment = new Comment();
			comment.setCreatedAt(ParseUtil.getDate("created_at", json));
			comment.setFavorited(ParseUtil.getBoolean("favorited", json));
			String id = ParseUtil.getRawString("cursor_id", json);
			if (StringUtil.isEmpty(id)) {
				id = ParseUtil.getRawString("id", json);
			}
			comment.setCommentId(id);
			comment.setTruncated(ParseUtil.getBoolean("truncated", json));
			comment.setSource(ParseUtil.getRawString("source", json));
			comment.setText(Emotions.normalizeEmotion(ServiceProvider.NetEase,
					ParseUtil.getRawString("text", json)));
			if (!json.isNull("user")) {
				comment.setUser(NetEaseUserAdaptor.createUser(json.getJSONObject("user")));
			}
			if (!json.isNull("status")) {
				comment.setReplyToStatus(NetEaseStatusAdaptor.createStatus(json.getJSONObject("status")));
			}
			Status srcStatus = new Status();
			srcStatus.setText(Emotions.normalizeEmotion(ServiceProvider.NetEase,
					ParseUtil.getRawString("in_reply_to_status_text", json)));
			srcStatus.setStatusId(ParseUtil.getRawString("in_reply_to_status_id", json));
			srcStatus.setServiceProvider(ServiceProvider.NetEase);
			
			User srcUser = new User();
			srcUser.setUserId(ParseUtil.getRawString("in_reply_to_user_id", json));
			srcUser.setName(ParseUtil.getRawString("in_reply_to_screen_name", json));
			srcUser.setScreenName(ParseUtil.getRawString("in_reply_to_user_name", json));
			srcUser.setServiceProvider(ServiceProvider.NetEase);
			
			srcStatus.setUser(srcUser);
			comment.setReplyToStatus(srcStatus);
			
			comment.setServiceProvider(ServiceProvider.NetEase);
			return comment;
		} catch (JSONException e) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR);
		} catch (ParseException e) {
			throw new LibException(LibResultCode.DATE_PARSE_ERROR);
		}
	}
}
