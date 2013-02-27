package net.dev123.sns.facebook;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import net.dev123.commons.ServiceProvider;
import net.dev123.commons.util.ParseUtil;
import net.dev123.commons.util.StringUtil;
import net.dev123.exception.ExceptionCode;
import net.dev123.exception.LibException;
import net.dev123.sns.entity.Page;
import net.dev123.sns.entity.Post;
import net.dev123.sns.entity.Profile;
import net.dev123.sns.entity.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FacebookPostAdapter {

	public static Post createPost(String jsonString) throws LibException {
		try {
			JSONObject json = new JSONObject(jsonString);
			return createPost(json);
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR, e);
		}
	}

	public static List<Post> createPostList(String jsonString)
			throws LibException {
		try {
			if (StringUtil.isEquals("{}", jsonString)
					|| StringUtil.isEquals("[]", jsonString)) {
				return new ArrayList<Post>(0);
			}
			JSONArray jsonArray = new JSONArray(jsonString);
			int length = jsonArray.length();
			List<Post> posts = new ArrayList<Post>(length);
			for (int i = 0; i < length; i++) {
				posts.add(createPost(jsonArray.getJSONObject(i)));
			}
			return posts;
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR, e);
		}
	}

	public static Post createPost(JSONObject json) throws LibException {
		if (json == null) {
			return null;
		}
		try {
			Post post = new Post();
			post.setId(ParseUtil.getRawString("id", json));
			JSONObject fromJson = json.getJSONObject("from");
			Profile from = null;
			if (fromJson.has("category")) {
				Page page = new Page();
				page.setId(ParseUtil.getRawString("id", fromJson));
				page.setName(ParseUtil.getRawString("name", fromJson));
				page.setPicture(String.format(Facebook.PICTURE_URL_FORMAT, page.getId()));
				page.setCategory(ParseUtil.getRawString("category", fromJson));
				from = page;
			} else {
				User user = new User();
				user.setId(ParseUtil.getRawString("id", fromJson));
				user.setName(ParseUtil.getRawString("name", fromJson));
				user.setProfileImageUrl(String.format(Facebook.PICTURE_URL_FORMAT, user.getId()));
				from = user;
			}
			post.setFrom(from);
			post.setStory(ParseUtil.getRawString("story", json));
			post.setMessage(ParseUtil.getRawString("message", json));
			post.setLink(ParseUtil.getRawString("link", json));
			post.setLinkName(ParseUtil.getRawString("name", json));
			post.setLinkCaption(ParseUtil.getRawString("caption", json));
			post.setLinkDescription(ParseUtil.getRawString("description", json));
			post.setObjectId(ParseUtil.getRawString("object_id", json));
			post.setSourceLink(ParseUtil.getRawString("source", json));
			if (json.has("comments")) {
				post.setCommentsCount(ParseUtil.getLong("count", json.getJSONObject("comments")));
			}
			if (json.has("likes")) {
				post.setLikesCount(ParseUtil.getLong("count", json.getJSONObject("likes")));
			}
			if (json.has("properties")) {
				JSONArray propertyArray = json.getJSONArray("properties");
				int length = propertyArray.length();
				JSONObject propertyJson = null;
				for (int i = 0; i< length; i++) {
					propertyJson = propertyArray.getJSONObject(i);
					post.setProperty(ParseUtil.getRawString("name", propertyJson),
							ParseUtil.getRawString("text", propertyJson));
				}
			}
			if (json.has("actions")) {
				JSONArray actionArray = json.getJSONArray("actions");
				Post.Action action = null;
				JSONObject actionJson = null;
				List<Post.Action> actions = new ArrayList<Post.Action>();
				int length = actionArray.length();
				for (int i = 0; i< length; i++) {
					action = new Post.Action();
					actionJson = actionArray.getJSONObject(i);
					action.setLink(ParseUtil.getRawString("link", actionJson));
					action.setName(ParseUtil.getRawString("name", actionJson));
					actions.add(action);
				}
				post.setActions(actions);
			}
			post.setCreatedTime(ParseUtil.getDate("created_time", json, Facebook.DATE_FORMAT));
			post.setUpdatedTime(ParseUtil.getDate("updated_time", json, Facebook.DATE_FORMAT));
			post.setServiceProvider(ServiceProvider.Facebook);
			return post;
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR, e);
		} catch (ParseException e) {
			throw new LibException(ExceptionCode.DATE_PARSE_ERROR, e);
		}
	}

}
