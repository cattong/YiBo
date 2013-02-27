package net.dev123.mblog.sohu;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import net.dev123.commons.ServiceProvider;
import net.dev123.commons.util.ParseUtil;
import net.dev123.exception.ExceptionCode;
import net.dev123.exception.LibException;
import net.dev123.mblog.entity.Comment;
import net.dev123.mblog.entity.Status;
import net.dev123.mblog.entity.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SohuCommentAdaptor {

	public static Comment createComment(String jsonString) throws LibException {
		try {
			JSONObject json = new JSONObject(jsonString);
			return createComment(json);
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR);
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
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR);
		}
	}

	static Comment createComment(JSONObject json) throws LibException {
		try {
			Comment comment = new Comment();
			comment.setCreatedAt(ParseUtil.getDate("created_at", json));
			comment.setFavorited(ParseUtil.getBoolean("favorited", json));
			comment.setId(ParseUtil.getRawString("id", json));
			comment.setTruncated(ParseUtil.getBoolean("truncated", json));
			comment.setSource(ParseUtil.getRawString("source", json));
			String text = ParseUtil.getRawString("text", json);
			comment.setText(SohuEmotions.normalizeEmotion(ServiceProvider.Sohu, text));
			if (!json.isNull("user")) {
				comment.setUser(SohuUserAdaptor.createUser(json.getJSONObject("user")));
			}
			if (!json.isNull("in_reply_to_status_id")) {
				Status inReplyToStatus = new Status();
				inReplyToStatus.setId(ParseUtil.getRawString("in_reply_to_status_id", json));
				inReplyToStatus.setText(ParseUtil.getRawString("in_reply_to_status_text", json));
				User user = new User();
				user.setId(ParseUtil.getRawString("in_reply_to_user_id", json));
				user.setName(ParseUtil.getRawString("in_reply_to_status_text", json));
				user.setScreenName(user.getName());
				user.setServiceProvider(ServiceProvider.Sohu);
				inReplyToStatus.setUser(user);
				inReplyToStatus.setServiceProvider(ServiceProvider.Sohu);
				comment.setInReplyToStatus(inReplyToStatus);
			}
			comment.setServiceProvider(ServiceProvider.Sohu);
			return comment;
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR);
		} catch (ParseException e) {
			throw new LibException(ExceptionCode.DATE_PARSE_ERROR);
		}
	}
}
