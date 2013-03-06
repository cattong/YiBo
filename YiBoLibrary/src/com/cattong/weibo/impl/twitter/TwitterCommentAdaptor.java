package com.cattong.weibo.impl.twitter;

import java.text.ParseException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.cattong.commons.LibException;
import com.cattong.commons.LibResultCode;
import com.cattong.commons.ServiceProvider;
import com.cattong.commons.util.ParseUtil;
import com.cattong.entity.Comment;
import com.cattong.entity.Status;

class TwitterCommentAdaptor {

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
		comment.setServiceProvider(ServiceProvider.Twitter);
		return comment;
	}


	public static Comment createComment(String jsonString) throws LibException {
		try {
			JSONObject json = new JSONObject(jsonString);
			return createComment(json);
		} catch (JSONException e) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR, e);
		}
	}

	public static ArrayList<Comment> createCommentList(String jsonString) throws LibException {
		try {
			if ("[]".equals(jsonString) || "{}".equals(jsonString)) {
				return new ArrayList<Comment>(0);
			}
			JSONArray jsonList = new JSONArray(jsonString);
			int size = jsonList.length();
			ArrayList<Comment> comments = new ArrayList<Comment>(size);
			if (size > 0) {
				for (int i = 0; i < size; i++) {
					comments.add(createComment(jsonList.getJSONObject(i)));
				}
			}
			return comments;
		} catch (JSONException e) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR, e);
		}
	}

	static Comment createComment(JSONObject json) throws LibException {
		try {
			Comment comment = new Comment();
			comment.setCreatedAt(ParseUtil.getDate("created_at", json));
			comment.setFavorited(ParseUtil.getBoolean("favorited", json));
			comment.setCommentId(ParseUtil.getRawString("id", json));
			comment.setTruncated(ParseUtil.getBoolean("truncated", json));
			comment.setSource(ParseUtil.getRawString("source", json));
			comment.setText(ParseUtil.getRawString("text", json));
			if (!json.isNull("user")) {
				comment.setUser(TwitterUserAdaptor.createUser(json.getJSONObject("user")));
			}
			if (!json.isNull("status")) {
				comment.setReplyToStatus(TwitterStatusAdaptor.createStatus(json.getJSONObject("status")));
			}
			if (!json.isNull("reply_comment")) {
				comment.setReplyToComment(createComment(json.getJSONObject("reply_comment")));
			}
			comment.setServiceProvider(ServiceProvider.Twitter);
			return comment;
		} catch (JSONException e) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR, e);
		} catch (ParseException e) {
			throw new LibException(LibResultCode.DATE_PARSE_ERROR, e);
		}
	}
}
