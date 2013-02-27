package net.dev123.sns.renren;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import net.dev123.commons.ServiceProvider;
import net.dev123.commons.util.ParseUtil;
import net.dev123.commons.util.StringUtil;
import net.dev123.exception.ExceptionCode;
import net.dev123.exception.LibException;
import net.dev123.sns.entity.Comment;
import net.dev123.sns.entity.Post.PostType;
import net.dev123.sns.entity.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RenRenCommentAdapter {

	public static Comment createComment(String jsonString) throws LibException {
		try {
			JSONObject json = new JSONObject(jsonString);
			return createComment(json);
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR, e);
		}
	}

	public static List<Comment> createCommentsList(String jsonString, PostType type)
			throws LibException {
		try {
			if (StringUtil.isEquals("{}", jsonString)
				|| StringUtil.isEquals("[]", jsonString)) {
				return new ArrayList<Comment>(0);
			}
			if (type == null) {
				throw new LibException(ExceptionCode.PARAMETER_NULL);
			}
			JSONArray jsonArray = null;
			if(type == PostType.NOTE
				|| type == PostType.PHOTO
				|| type == PostType.ALBUM
				|| type == PostType.STATUS) {
				jsonArray = new JSONArray(jsonString);
			} else {
				JSONObject json = new JSONObject(jsonString);
				jsonArray = json.getJSONArray("comments");
			}

			int length = jsonArray.length();
			List<Comment> comments = new ArrayList<Comment>(length);
			for (int i = 0; i < length; i++) {
				comments.add(createComment(jsonArray.getJSONObject(i)));
			}
			return comments;
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR, e);
		}
	}

	public static Comment createComment(JSONObject json) throws LibException {
		if (json == null) {
			return null;
		}
		try {
			Comment comment = new Comment();
			comment.setId(ParseUtil.getRawString("comment_id", json));
			comment.setText(ParseUtil.getRawString("text", json));
			comment.setCreatedTime(ParseUtil.getDate("time", json, "yyyy-MM-dd hh:mm:ss"));
			User user = new User();
			user.setId(ParseUtil.getRawString("uid", json));
			user.setScreenName(ParseUtil.getRawString("name", json));
			user.setProfileImageUrl(ParseUtil.getRawString("headurl", json));
			user.setServiceProvider(ServiceProvider.RenRen);
			comment.setFrom(user);
			comment.setServiceProvider(ServiceProvider.RenRen);
			return comment;
		} catch (ParseException e) {
			throw new LibException(ExceptionCode.DATE_PARSE_ERROR, e);
		}
	}

}
