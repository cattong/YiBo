package com.cattong.weibo.impl.sina;

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

class SinaCommentAdaptor {

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
			JSONObject json = new JSONObject(jsonString);			
			JSONArray jsonList = json.getJSONArray("comments");
			int size = jsonList.length();
			ArrayList<Comment> comments = new ArrayList<Comment>(size);
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
			comment.setText(SinaEmotions.normalizeEmotion(ServiceProvider.Sina, text));
			if (!json.isNull("user")) {
				comment.setUser(SinaUserAdaptor.createUser(json.getJSONObject("user")));
			}
			if (!json.isNull("status")) {
				comment.setReplyToStatus(SinaStatusAdaptor.createStatus(json.getJSONObject("status")));
			}
			if (!json.isNull("reply_comment")) {
				comment.setReplyToComment(createComment(json.getJSONObject("reply_comment")));
			}
			comment.setServiceProvider(ServiceProvider.Sina);
			return comment;
		} catch (JSONException e) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR);
		} catch (ParseException e) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR);
		}
	}
}
