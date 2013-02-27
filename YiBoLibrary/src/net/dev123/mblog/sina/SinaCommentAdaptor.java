package net.dev123.mblog.sina;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import net.dev123.commons.ServiceProvider;
import net.dev123.commons.util.ParseUtil;
import net.dev123.exception.ExceptionCode;
import net.dev123.exception.LibException;
import net.dev123.mblog.entity.Comment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SinaCommentAdaptor {

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
			ArrayList<Comment> comments = new ArrayList<Comment>(size);
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
			comment.setText(SinaEmotions.normalizeEmotion(ServiceProvider.Sina, text));
			if (!json.isNull("user")) {
				comment.setUser(SinaUserAdaptor.createUser(json.getJSONObject("user")));
			}
			if (!json.isNull("status")) {
				comment.setInReplyToStatus(SinaStatusAdaptor.createStatus(json.getJSONObject("status")));
			}
			if (!json.isNull("reply_comment")) {
				comment.setInReplyToComment(createComment(json.getJSONObject("reply_comment")));
			}
			comment.setServiceProvider(ServiceProvider.Sina);
			return comment;
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR);
		} catch (ParseException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR);
		}
	}
}
