package net.dev123.sns.renren;

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
import net.dev123.sns.entity.Post.PostType;
import net.dev123.sns.entity.Profile;
import net.dev123.sns.entity.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RenRenPostAdapter {

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
			post.setId(ParseUtil.getRawString("post_id", json));
			post.setObjectId(ParseUtil.getRawString("source_id", json));
			String actorType = ParseUtil.getRawString("actor_type", json);
			Profile from = null;
			if ("user".equalsIgnoreCase(actorType)) {
				User user = new User();
				user.setId(ParseUtil.getRawString("actor_id", json));
				user.setName(ParseUtil.getRawString("name", json));
				user.setProfileImageUrl(ParseUtil.getRawString("headurl", json));
				from = user;
			} else if ("page".equalsIgnoreCase(actorType)) {
				Page page = new Page();
				page.setId(ParseUtil.getRawString("actor_id", json));
				page.setName(ParseUtil.getRawString("name", json));
				page.setPicture(ParseUtil.getRawString("headurl", json));
				from = page;
			}
			post.setFrom(from);
			int feedType = ParseUtil.getInt("feed_type", json);
			post.setProperty("feed_type", feedType);
			post.setPostType(getPostTypeFromFeedType(feedType));
			post.setStory(ParseUtil.getRawString("prefix", json));
			post.setMessage(ParseUtil.getRawString("message", json));
			post.setLink(ParseUtil.getRawString("href", json));
			post.setLinkName(ParseUtil.getRawString("title", json));
			post.setLinkCaption(post.getLink());
			post.setLinkDescription(ParseUtil.getRawString("description", json));
			if (json.has("comments")) {
				post.setCommentsCount(ParseUtil.getLong("count",
						json.getJSONObject("comments")));
			}
			if (json.has("likes")) {
				post.setLikesCount(ParseUtil.getLong("total_count",
						json.getJSONObject("likes")));
			}
			post.setCreatedTime(ParseUtil.getDate("update_time", json,
					"yyyy-MM-dd hh:mm:ss"));
			if (json.has("attachment")) {
				JSONArray attachmentArray = json.getJSONArray("attachment");
				if (attachmentArray.length() > 0) {
					JSONObject attachment = attachmentArray.getJSONObject(0);
					switch (post.getPostType()) {
					case PHOTO:
						post.setPicture(ParseUtil.getRawString("src",
								attachment));
						post.setSourceLink(ParseUtil.getRawString("raw_src",
								attachment));
						post.setObjectId(ParseUtil.getRawString("media_id", attachment));
						break;
					case ALBUM:
						post.setPicture(ParseUtil.getRawString("src",
								attachment));
						post.setSourceLink(ParseUtil.getRawString("raw_src",
								attachment));
						break;
					case VIDEO:
						post.setPicture(ParseUtil.getRawString("src",
								attachment));
						post.setSourceLink(ParseUtil.getRawString("href",
								attachment));
					default:
						break;
					}
					String ownerId = ParseUtil.getRawString("owner_id",
							attachment);
					if (!StringUtil.isEquals(ownerId, from.getProfileId())) {
						User owner = new User();
						owner.setId(ownerId);
						owner.setName(ParseUtil.getRawString("owner_name",
								attachment));
						post.setOwner(owner);
					}
				}
			}
			post.setServiceProvider(ServiceProvider.RenRen);
			return post;
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR, e);
		} catch (ParseException e) {
			throw new LibException(ExceptionCode.DATE_PARSE_ERROR, e);
		}
	}

	/**
	 * <p>
	 * Feed类型
	 * <table border="1" cellpadding="5" cellspacing="0">
	 * <tbody><tr><th>type</th><th>描述 </th></tr>
	 * <tr><td> 10 </td><td> 更新状态的新鲜事。</td></tr>
	 * <tr><td> 11 </td><td> page更新状态的新鲜事。</td></tr>
	 * <tr><td> 20 </td><td> 发表日志的新鲜事。</td></tr>
	 * <tr><td> 21 </td><td> 分享日志的新鲜事。</td></tr>
	 * <tr><td> 22 </td><td> page发表日志的新鲜事。</td></tr>
	 * <tr><td> 23 </td><td> page分享日志的新鲜事。</td></tr>
	 * <tr><td> 30 </td><td> 上传照片的新鲜事。</td></tr>
	 * <tr><td> 31 </td><td> page上传照片的新鲜事。</td></tr>
	 * <tr><td> 32 </td><td> 分享照片的新鲜事。</td></tr>
	 * <tr><td> 33 </td><td> 分享相册的新鲜事。</td></tr>
	 * <tr><td> 34 </td><td> 修改头像的新鲜事。</td></tr>
	 * <tr><td> 35 </td><td> page修改头像的新鲜事。</td></tr>
	 * <tr><td> 36 </td><td> page分享照片的新鲜事。</td></tr>
	 * <tr><td> 40 </td><td> 成为好友的新鲜事。</td></tr>
	 * <tr><td> 41 </td><td> 成为page粉丝的新鲜事。</td></tr>
	 * <tr><td> 50 </td><td> 分享视频的新鲜事。</td></tr>
	 * <tr><td> 51 </td><td> 分享链接的新鲜事。</td></tr>
	 * <tr><td> 52 </td><td> 分享音乐的新鲜事。</td></tr>
	 * <tr><td> 53 </td><td> page分享视频的新鲜事。</td></tr>
	 * <tr><td> 54 </td><td> page分享链接的新鲜事。</td></tr>
	 * <tr><td> 55 </td><td> page分享音乐的新鲜事。</td></tr>
	 * </tbody></table>
	 * </p>
	 */
	private static PostType getPostTypeFromFeedType (int feedType) {
		PostType type = PostType.POST;
		switch (feedType) {
		case 10:
		case 11:
			type = PostType.STATUS;
			break;
		case 20:
		case 21:
		case 22:
		case 23:
			type = PostType.NOTE;
			break;
		case 30:
		case 31:
		case 32:
		case 36:
			type = PostType.PHOTO;
			break;
		case 33:
			type = PostType.ALBUM;
			break;
		case 50:
		case 53:
			type = PostType.VIDEO;
			break;
		case 51:
		case 54:
			type = PostType.LINK;
			break;
		case 52:
		case 55:
			type = PostType.MUSIC;
			break;
		default:
			type = PostType.POST;
			break;
		}

		return type;
	}
}
