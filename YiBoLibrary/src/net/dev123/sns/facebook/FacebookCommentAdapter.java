package net.dev123.sns.facebook;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import net.dev123.commons.ServiceProvider;
import net.dev123.commons.util.ParseUtil;
import net.dev123.commons.util.StringUtil;
import net.dev123.exception.ExceptionCode;
import net.dev123.exception.LibException;
import net.dev123.sns.entity.Comment;
import net.dev123.sns.entity.Page;
import net.dev123.sns.entity.Profile;
import net.dev123.sns.entity.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FacebookCommentAdapter {

	public static Comment createComment(String jsonString) throws LibException {
		try {
			JSONObject json = new JSONObject(jsonString);
			return createComment(json);
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR, e);
		}
	}

	public static List<Comment> createCommentsList(String jsonString)
			throws LibException {
		try {
			if (StringUtil.isEquals("{}", jsonString)
				|| StringUtil.isEquals("[]", jsonString)) {
				return new ArrayList<Comment>(0);
			}
			JSONArray jsonArray =  new JSONArray(jsonString);
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
			comment.setId(ParseUtil.getRawString("id", json));
			comment.setText(ParseUtil.getRawString("message", json));
			comment.setCreatedTime(ParseUtil.getDate("created_time", json, Facebook.DATE_FORMAT));
			JSONObject fromJson = json.getJSONObject("from");
			Profile from = null;
			if (fromJson.has("category")) {
				Page page = new Page();
				page.setId(ParseUtil.getRawString("id", fromJson));
				page.setName(ParseUtil.getRawString("name", fromJson));
				page.setPicture(String.format(Facebook.PICTURE_URL_FORMAT, page.getId()));
				page.setCategory(ParseUtil.getRawString("category", fromJson));
				from  = page;
			} else {
				User user = new User();
				user.setId(ParseUtil.getRawString("id", fromJson));
				user.setName(ParseUtil.getRawString("name", fromJson));
				user.setHeadUrl(String.format(Facebook.PICTURE_URL_FORMAT, user.getId()));
				from = user;
			}
			comment.setFrom(from);
			comment.setServiceProvider(ServiceProvider.Facebook);
			return comment;
		} catch (ParseException e) {
			throw new LibException(ExceptionCode.DATE_PARSE_ERROR, e);
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR);
		}
	}

}
