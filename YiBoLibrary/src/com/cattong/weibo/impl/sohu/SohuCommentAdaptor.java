package com.cattong.weibo.impl.sohu;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.cattong.commons.LibException;
import com.cattong.commons.LibResultCode;
import com.cattong.commons.ServiceProvider;
import com.cattong.commons.util.ParseUtil;
import com.cattong.entity.Comment;
import com.cattong.entity.Status;
import com.cattong.entity.User;

class SohuCommentAdaptor {

	public static Comment createComment(String jsonString) throws LibException {
		try {
			JSONObject json = new JSONObject(jsonString);
			return createComment(json);
		} catch (JSONException e) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR);
		}
	}

	public static List<Comment> createCommentList(String jsonString) throws LibException {
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

	static Comment createComment(JSONObject json) throws LibException {
		try {
			Comment comment = new Comment();
			comment.setCreatedAt(ParseUtil.getDate("created_at", json));
			comment.setFavorited(ParseUtil.getBoolean("favorited", json));
			comment.setCommentId(ParseUtil.getRawString("id", json));
			comment.setTruncated(ParseUtil.getBoolean("truncated", json));
			comment.setSource(ParseUtil.getRawString("source", json));
			String text = ParseUtil.getRawString("text", json);
			comment.setText(SohuEmotions.normalizeEmotion(ServiceProvider.Sohu, text));
			if (!json.isNull("user")) {
				comment.setUser(SohuUserAdaptor.createUser(json.getJSONObject("user")));
			}
			if (!json.isNull("in_reply_to_status_id")) {
				Status inReplyToStatus = new Status();
				inReplyToStatus.setStatusId(ParseUtil.getRawString("in_reply_to_status_id", json));
				inReplyToStatus.setText(ParseUtil.getRawString("in_reply_to_status_text", json));
				User user = new User();
				user.setUserId(ParseUtil.getRawString("in_reply_to_user_id", json));
				user.setName(ParseUtil.getRawString("in_reply_to_status_text", json));
				user.setScreenName(user.getName());
				user.setServiceProvider(ServiceProvider.Sohu);
				inReplyToStatus.setUser(user);
				inReplyToStatus.setServiceProvider(ServiceProvider.Sohu);
				comment.setReplyToStatus(inReplyToStatus);
			}
			comment.setServiceProvider(ServiceProvider.Sohu);
			return comment;
		} catch (JSONException e) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR);
		} catch (ParseException e) {
			throw new LibException(LibResultCode.DATE_PARSE_ERROR);
		}
	}
}
