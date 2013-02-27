package net.dev123.sns.renren;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.dev123.commons.Paging;
import net.dev123.commons.ServiceProvider;
import net.dev123.commons.http.HttpMethod;
import net.dev123.commons.http.HttpRequestHelper;
import net.dev123.commons.http.HttpRequestMessage;
import net.dev123.commons.http.auth.Authorization;
import net.dev123.commons.oauth2.OAuth2;
import net.dev123.commons.util.EncryptUtil;
import net.dev123.commons.util.ListUtil;
import net.dev123.commons.util.ParseUtil;
import net.dev123.commons.util.StringUtil;
import net.dev123.exception.ExceptionCode;
import net.dev123.exception.LibException;
import net.dev123.sns.Sns;
import net.dev123.sns.entity.Album;
import net.dev123.sns.entity.Comment;
import net.dev123.sns.entity.FriendList;
import net.dev123.sns.entity.Note;
import net.dev123.sns.entity.Page;
import net.dev123.sns.entity.Photo;
import net.dev123.sns.entity.Post;
import net.dev123.sns.entity.Privacy;
import net.dev123.sns.entity.Profile;
import net.dev123.sns.entity.Status;
import net.dev123.sns.entity.User;
import net.dev123.sns.entity.Post.PostType;

import org.apache.http.client.ResponseHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RenRen extends Sns {

	private static final String REST_BASE = "http://api.renren.com/restserver.do";
	private static final String API_VERSION = "1.0";
	private static final String RESPONSE_FORMAT = "json";

	public static final String PROPERTY_FEED_TYPE = "feed_type";
	private static final String LIKE_URL_FORMAT =
		"http://www.renren.com/g?ownerid=%1$s&resourceid=%2$s&type=%3$s";

	private ResponseHandler<String> responseHandler;
	private String userId;
	private String screenName;

	public RenRen(Authorization auth) {
		super(auth);
		this.responseHandler = new RenRenResponseHandler();
	}

	@Override
	public String getScreenName() throws LibException {
		if (StringUtil.isEmpty(screenName)) {
			this.userId = getUserId();
			User user = showUser(userId);
			if (user != null) {
				this.screenName = user.getScreenName();
			}
		}
		return this.screenName;
	}

	@Override
	public String getUserId() throws LibException {
		if (StringUtil.isEmpty(userId)) {
			TreeMap<String, String> params = new TreeMap<String, String>();
			params.put("method", "users.getLoggedInUser");
			String json = sendPostRestRequest(params, null);
			this.userId = RenRenUserAdapter.createUserId(json);
		}
		return this.userId;
	}

	/******* PostMethods ********/

	/*
	 * 分享平台已有的发布内容
	 */
	@Override
	public boolean share(Post post) throws LibException {
		if (post == null || StringUtil.isEmpty(post.getId())) {
			return false;
		}

		TreeMap<String, String> params = new TreeMap<String, String>();
		params.put("method", "share.share");
		String type = null;
		switch (post.getPostType()) {
		case ALBUM:
			type = "8";
			break;
		case NOTE:
			type = "1";
			break;
		case PHOTO:
			type = "2";
			break;
		case LINK:
			type = "6";
			break;
		case VIDEO:
			type = "10";
			break;
		case MUSIC:
			type = "11";
			break;
		default:
			type = "20";
			break;
		}
		params.put("type", type); // 类型为分享
		if (post.getFrom() == null) {
			throw new LibException(ExceptionCode.PARAMETER_ERROR);
		}
		params.put("ugc_id", post.getObjectId());
		params.put("user_id", post.getOwner().getProfileId());
		sendPostRestRequest(params);
		return true;
	}

	@Override
	public List<Post> getNewsFeed(Paging<Post> paging) throws LibException {
		return getProfileFeed(null, paging);
	}

	private static String[] feedType = {
		"10", // 更新状态
		"11", // Page更新状态
		"20", // 发表日志
		"21", // 分享日志
		"22", // Page发表日志
		"23", // Page分享日志
		"30", // 上传照片
		"31", // Page上传照片
		"32", // 分享照片
		"33", // 分享相册
		"34", // 修改头像的新鲜事
		"35", // page修改头像的新鲜事
		"36", // Page分享照片
		"40", // 成为好友的新鲜事
		"41", // 成为page粉丝的新鲜事
		"50", // 分享视频
		"51", // 分享链接
		"52", // 分享音乐
		"53", // Page分享视频
		"54", // Page分享链接
		"55"  // Page分享音乐
	};

	@Override
	public List<Post> getProfileFeed(String profileId, Paging<Post> paging) throws LibException {
		if (paging == null) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		if (!paging.isPagePaging()) {
			initPagePaging(paging);
		}

		TreeMap<String, String> params = new TreeMap<String, String>();
		params.put("method", "feed.get");
		params.put("type", StringUtil.join(feedType, ","));
		if (StringUtil.isNotEmpty(profileId)) {
			params.put("uid", profileId);
		}
		params.put("page", String.valueOf(paging.getPageIndex()));
		params.put("count", String.valueOf(paging.getPageSize()));
		String jsonString = sendPostRestRequest(params);
		List<Post> posts = RenRenPostAdapter.createPostList(jsonString);
		updatePaging(posts, paging);
		return posts;
	}

	/******* FriendshipMethods ********/

	@Override
	public boolean areFriends(String sourceUserId, String targetUserId)
			throws LibException {
		if (StringUtil.isEmpty(sourceUserId)
			|| StringUtil.isEmpty(targetUserId)) {
			throw new LibException(ExceptionCode.PARAMETER_ERROR);
		}
		TreeMap<String, String> params = new TreeMap<String, String>();
		params.put("method", "friends.areFriends");
		params.put("uids1", sourceUserId);
		params.put("uids2", targetUserId);

		String jsonString = sendPostRestRequest(params);
		try {
			JSONArray jsonArray = new JSONArray(jsonString);
			JSONObject json = jsonArray.getJSONObject(0);
			return ParseUtil.getInt("are_friends", json) == 1;
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR, e.getMessage());
		}
	}

	@Override
	public List<Boolean> areFriends(List<String> listSourceUserId,
			List<String> listTargetUserId) throws LibException {
		if (ListUtil.isEmpty(listSourceUserId)
			|| ListUtil.isEmpty(listTargetUserId)) {
			throw new LibException(ExceptionCode.PARAMETER_ERROR);
		}

		TreeMap<String, String> params = new TreeMap<String, String>();
		params.put("method", "friends.areFriends");
		params.put("uids1", StringUtil.join(listSourceUserId.toArray(), ","));
		params.put("uids2", StringUtil.join(listTargetUserId.toArray(), ","));

		String jsonString = sendPostRestRequest(params, null);
		try {
			JSONArray jsonArray = new JSONArray(jsonString);
			int size = jsonArray.length();
			List<Boolean> result = new ArrayList<Boolean>(size);
			JSONObject json = null;
			for (int i= 0; i < size; i++) {
				json = jsonArray.getJSONObject(i);
				result.add(ParseUtil.getInt("are_friends", json) == 1);
			}
			return result;
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR, e.getMessage());
		}
	}

	@Override
	public List<String> getFriendsIds(Paging<String> paging)
			throws LibException {
		if (paging == null) {
			throw new LibException(ExceptionCode.PARAMETER_ERROR);
		}
		if (!paging.isPagePaging()) {
			initPagePaging(paging);
		}

		TreeMap<String, String> params = new TreeMap<String, String>();
		params.put("method", "friends.get");
		params.put("page", String.valueOf(paging.getPageIndex()));
		params.put("count", String.valueOf(paging.getPageSize()));

		String json = sendPostRestRequest(params);
		List<String> friendsIds = RenRenUserAdapter.createUserIdList(json);
		updatePaging(friendsIds, paging);
		return friendsIds;
	}

	@Override
	public List<User> getFriends(Paging<User> paging) throws LibException {
		if (paging == null) {
			throw new LibException(ExceptionCode.PARAMETER_ERROR);
		}
		if (!paging.isPagePaging()) {
			initPagePaging(paging);
		}

		TreeMap<String, String> params = new TreeMap<String, String>();
		params.put("method", "friends.getFriends");
		params.put("page", String.valueOf(paging.getPageIndex()));
		params.put("count", String.valueOf(paging.getPageSize()));

		String json = sendPostRestRequest(params);
		List<User> listUser = RenRenUserAdapter.createSimpleUserList(json);
		updatePaging(listUser, paging);
		return listUser;
	}

	@Override
	public boolean createFriendList(String listName) throws LibException {
		return false;
	}

	@Override
	public boolean destroyFriendList(String listName) throws LibException {
		return false;
	}

	@Override
	public List<FriendList> getFriendLists(Paging<FriendList> paging)
			throws LibException {
		return null;
	}

	@Override
	public List<User> getMutualFriends(String userIdA, String userIdB, Paging<User> paging)
			throws LibException {
		if (StringUtil.isEmpty(userIdA)
			|| StringUtil.isEmpty(userIdB)) {
			throw new LibException(ExceptionCode.PARAMETER_ERROR);
		}

		TreeMap<String, String> params = new TreeMap<String, String>();
		params.put("method", "friends.getSameFriends");
		params.put("uid1", userIdA);
		params.put("uid2", userIdB);
		params.put("fields", "uid,name,headurl");

		String json = sendPostRestRequest(params);
		List<User> listUser = RenRenUserAdapter.createSimpleUserList(json);
		return listUser;
	}

	/******* CommentMethods ********/

	@Override
	public boolean createComment(String commentText, String objectId, String ownerId, PostType type)
			throws LibException {
		if (StringUtil.isEmpty(commentText)
			|| StringUtil.isEmpty(objectId)
			|| StringUtil.isEmpty(ownerId)
			|| type == null) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		TreeMap<String, String> params = new TreeMap<String, String>();
		switch (type) {
		case NOTE:
			params.put("method", "blog.addComment");
			params.put("content", commentText);
			params.put("uid", getUserId());
			params.put("id", objectId);
			break;
		case STATUS:
			params.put("method", "status.addComment");
			params.put("content", commentText);
			params.put("owner_id", ownerId);
			params.put("status_id", objectId);
			break;
		case PHOTO:
		case ALBUM:
			params.put("method", "photos.addComment");
			params.put("content", commentText);
			params.put("uid", ownerId);
			if (type == PostType.ALBUM) {
				params.put("aid", objectId);
			} else {
				params.put("pid", objectId);
			}
			break;
		default:
			// 其他均认为是分享
			params.put("method", "share.addComment");
			params.put("share_id", objectId);
			params.put("owner_id", ownerId);
			break;
		}
		String response = sendPostRestRequest(params);
		return RenRenBaseAdapter.createIntegerResult(response) == 1;
	}

	@Override
	public Comment showComment(String commentId) throws LibException {
		return null;
	}

	@Override
	public boolean destroyComment(String commentId) throws LibException {
		return false;
	}

	@Override
	public List<Comment> getComments(String objectId, String ownerId, PostType type, Paging<Comment> paging)
			throws LibException {
		if (StringUtil.isEmpty(objectId)
			|| StringUtil.isEmpty(ownerId)
			|| type == null
			|| paging == null) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		if (!paging.isPagePaging()) {
			initPagePaging(paging);
		}

		TreeMap<String, String> params = new TreeMap<String, String>();
		switch (type) {
		case NOTE:
			params.put("method", "blog.getComments");
			params.put("uid", getUserId());
			params.put("id", objectId);
			break;
		case STATUS:
			params.put("method", "status.getComment");
			params.put("owner_id", ownerId);
			params.put("status_id", objectId);
			break;
		case PHOTO:
		case ALBUM:
			params.put("method", "photos.getComments");
			params.put("uid", ownerId);
			if (type == PostType.ALBUM) {
				params.put("aid", objectId);
			} else {
				params.put("pid", objectId);
			}
			break;
		default:
			// 其他均认为是分享
			params.put("method", "share.getComments");
			params.put("share_id", objectId);
			params.put("owner_id", ownerId);
			break;
		}
		params.put("page", String.valueOf(paging.getPageIndex()));
		params.put("count", String.valueOf(paging.getPageSize()));

		String response = sendPostRestRequest(params);
		List<Comment> commentList = RenRenCommentAdapter.createCommentsList(response, type);
		updatePaging(commentList, paging);
		return commentList;
	}

	/******* LikeMethods ********/

	@Override
	public boolean createLike(String objectId, String ownerId, PostType type) throws LibException {
		if (StringUtil.isEmpty(objectId)
			|| StringUtil.isEmpty(ownerId)
			|| type == null) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		String url = constructLikeUrl(objectId, ownerId, type);
		TreeMap<String, String> params = new TreeMap<String, String>();
		params.put("method", "like.like");
		params.put("url", url);
		String response = sendPostRestRequest(params);
		return RenRenBaseAdapter.createIntegerResult(response) == 1;
	}

	private String constructLikeUrl(String objectId, String ownerId,
			PostType type) {
		String typeStr = null;
		switch (type) {
		case PHOTO:
		case ALBUM:
		case VIDEO:
			typeStr = type.toString().toLowerCase();
			break;
		case NOTE:
			typeStr = "blog";
			break;
		default:
			typeStr = "share";
			break;
		}
		String url = String.format(LIKE_URL_FORMAT, ownerId, objectId, typeStr);
		return url;
	}

	@Override
	public boolean destroyLike(String objectId, String ownerId, PostType type) throws LibException {
		if (StringUtil.isEmpty(objectId)
			|| StringUtil.isEmpty(ownerId)
			|| type == null) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		String url = constructLikeUrl(objectId, ownerId, type);
		TreeMap<String, String> params = new TreeMap<String, String>();
		params.put("method", "like.unlike");
		params.put("url", url);
		String response = sendPostRestRequest(params);
		return RenRenBaseAdapter.createIntegerResult(response) == 1;
	}

	@Override
	public long getLikeCount(String objectId, String ownerId, PostType type) throws LibException {
		if (StringUtil.isEmpty(objectId)
			|| StringUtil.isEmpty(ownerId)
			|| type == null) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		String url = constructLikeUrl(objectId, ownerId, type);
		TreeMap<String, String> params = new TreeMap<String, String>();
		params.put("method", "like.getCount");
		params.put("urls", "[\"" + url + "\"]");
		String response = sendPostRestRequest(params);
		try {
			JSONObject json = new JSONObject(response);
			JSONArray likes = json.getJSONArray("like_urls");
			json = likes.getJSONObject(0);
			return ParseUtil.getLong("like_count", json);
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR);
		}
	}

	/******* NoteMethods ********/

	@Override
	public boolean createNote(String subject, String content,  Privacy privacy, String... tags)
			throws LibException {
		if (StringUtil.isEmpty(subject)
			|| StringUtil.isEmpty(content)) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		TreeMap<String, String> params = new TreeMap<String, String>();
		params.put("method", "blog.addBlog");
		params.put("title", subject);
		params.put("content", content);
		if (privacy != null) {
			int visible = 99; // 所有人可见
			switch (privacy.getValue()) {
			case EVERYONE:
				visible = 99;
				break;
			case ALL_FRIENDS:
				visible = 1;
				break;
			case SELF:
				visible = -1;
			}
			params.put("visable", String.valueOf(visible));
		}
		String response = sendPostRestRequest(params);
		try {
			JSONObject json = new JSONObject(response);
			return json.has("id");
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR);
		}
	}

	@Override
	public Note showNote(String noteId, String ownerId) throws LibException {
		if (StringUtil.isEmpty(noteId) || StringUtil.isEmpty(ownerId)) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		TreeMap<String, String> params = new TreeMap<String, String>();
		params.put("method", "blog.get");
		params.put("id", noteId);
		params.put("uid", ownerId);
		String response = sendPostRestRequest(params);
		Note note = RenRenNoteAdapter.createNote(response);
		note.setFrom(showProfile(ownerId));
		return note;
	}

	@Override
	public List<Note> getNotes(String ownerId, Paging<Note> paging) throws LibException {
		if (paging == null) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		if (!paging.isPagePaging()) {
			initPagePaging(paging);
		}
		if (StringUtil.isEmpty(ownerId)) {
			ownerId = getUserId();
		}
		TreeMap<String, String> params = new TreeMap<String, String>();
		params.put("method", "blog.gets");
		params.put("uid", ownerId);
		params.put("page", String.valueOf(paging.getPageIndex()));
		params.put("count", String.valueOf(paging.getPageSize()));
		String resonse = sendPostRestRequest(params);
		List<Note> notes = RenRenNoteAdapter.createNoteList(resonse);
		if (notes != null && notes.size() > 0) {
			Profile author = showProfile(ownerId);
			for (Note note : notes) {
				note.setFrom(author);
			}
		}
		updatePaging(notes, paging);
		return notes;
	}

	/******* PageMethods ********/

	@Override
	public Page showPage(String pageId) throws LibException {
		if (StringUtil.isEmpty(pageId)) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		TreeMap<String, String> params = new TreeMap<String, String>();
		params.put("method", "pages.getInfo");
		params.put("page_id", pageId);
		params.put("fields", "desc,base_info,detail_info,contact_info");
		String response = sendPostRestRequest(params);
		return RenRenPageAdapter.createPage(response);
	}

	@Override
	public boolean followPage(String pageId) throws LibException {
		if (StringUtil.isEmpty(pageId)) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		TreeMap<String, String> params = new TreeMap<String, String>();
		params.put("method", "pages.becomeFan");
		params.put("page_id", pageId);
		String response = sendPostRestRequest(params);
		return RenRenBaseAdapter.createIntegerResult(response) == 1;
	}

	@Override
	public boolean unfollowPage(String pageId) throws LibException {
		if (StringUtil.isEmpty(pageId)) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		TreeMap<String, String> params = new TreeMap<String, String>();
		params.put("method", "pages.quit");
		params.put("page_id", pageId);
		String response = sendPostRestRequest(params);
		return RenRenBaseAdapter.createIntegerResult(response) == 1;
	}

	@Override
	public List<Page> getFollowingPages(String userId, Paging<Page> paging) throws LibException {
		if (StringUtil.isEmpty(userId) || paging == null) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		if (!paging.isPagePaging()) {
			initPagePaging(paging);
		}
		TreeMap<String, String> params = new TreeMap<String, String>();
		params.put("method", "pages.getList");
		params.put("uid", userId);
		params.put("page", String.valueOf(paging.getPageIndex()));
		params.put("count", String.valueOf(paging.getPageSize()));
		String response = sendPostRestRequest(params);
		List<Page> pages = RenRenPageAdapter.createPageList(response);
		updatePaging(pages, paging);
		return pages;
	}

	@Override
	public boolean isPageFollower(String userId, String pageId)
			throws LibException {
		if (StringUtil.isEmpty(userId) || StringUtil.isEmpty(pageId)) {
			throw new LibException(ExceptionCode.PARAMETER_ERROR);
		}

		TreeMap<String, String> params = new TreeMap<String, String>();
		params.put("method", "pages.isFan");
		params.put("page_id", pageId);
		params.put("uid", userId);

		String json = sendPostRestRequest(params);
		return RenRenBaseAdapter.createIntegerResult(json) == 1;
	}

	@Override
	public boolean isPageAdmin(String pageId) throws LibException {
		if (StringUtil.isEmpty(pageId)) {
			throw new LibException(ExceptionCode.PARAMETER_ERROR);
		}

		TreeMap<String, String> params = new TreeMap<String, String>();
		params.put("method", "pages.isAdmin");
		params.put("page_id", pageId);
		params.put("uid", userId);

		String json = sendPostRestRequest(params, null);
		return RenRenBaseAdapter.createIntegerResult(json) == 1;
	}

	private boolean isPage(String profileId) throws LibException {
		if (StringUtil.isEmpty(profileId)) {
			throw new LibException(ExceptionCode.PARAMETER_ERROR);
		}
		TreeMap<String, String> params = new TreeMap<String, String>();
		params.put("method", "pages.isPage");
		params.put("page_id", profileId);
		try {
			String json = sendPostRestRequest(params);
			return RenRenBaseAdapter.createIntegerResult(json) == 1;
		} catch (LibException e) {
			if (e.getExceptionCode() == 20302) {
				return false;
			} else {
				throw e;
			}
		}

	}

	/******* UserMethods ********/

	private static final String USER_FULL_FIELD = "uid,name,sex,star,zidou,"
			+ "vip,birthday,email_hash,tinyurl,headurl,mainurl,"
			+ "hometown_location,work_info,university_info,hs_info";

	@Override
	public User showUser(String userId) throws LibException {
		if (StringUtil.isEmpty(userId)) {
			throw new LibException(ExceptionCode.PARAMETER_ERROR);
		}

		TreeMap<String, String> params = new TreeMap<String, String>();
		params.put("method", "users.getInfo");
		params.put("uids", userId);
		params.put("fields", USER_FULL_FIELD);

		String json = sendPostRestRequest(params, null);
		List<User> listUser = RenRenUserAdapter.createUserList(json);
		User user = null;
		if (ListUtil.isNotEmpty(listUser) && listUser.size() == 1) {
			user = listUser.get(0);
		}
		return user;
	}

	@Override
	public List<User> showUsers(List<String> listUserId) throws LibException {
		if (ListUtil.isEmpty(listUserId)) {
			throw new LibException(ExceptionCode.PARAMETER_ERROR);
		}

		StringBuffer toIds = new StringBuffer();
		for (String userId : listUserId) {
			toIds.append(userId + ",");
		}
		toIds.deleteCharAt(toIds.length() - 1);

		TreeMap<String, String> params = new TreeMap<String, String>();
		params.put("method", "users.getInfo");
		params.put("uids", toIds.toString());
		params.put("fields", USER_FULL_FIELD);

		String json = sendPostRestRequest(params, null);
		List<User> listUser = RenRenUserAdapter.createUserList(json);
		return listUser;
	}

	@Override
	public boolean createStatus(String statusMessage)
			throws LibException {
		if (StringUtil.isEmpty(statusMessage)) {
			throw new LibException(ExceptionCode.PARAMETER_ERROR);
		}
		TreeMap<String, String> params = new TreeMap<String, String>();
		params.put("method", "status.set");
		String status = RenRenEmotions.specializeEmotion(ServiceProvider.RenRen, statusMessage);
		params.put("status", status);
		String jsonString = sendPostRestRequest(params);

		return RenRenBaseAdapter.createIntegerResult(jsonString) == 1;
	}

	private Profile showProfile(String profileId) throws LibException {
		if (StringUtil.isEmpty(profileId)) {
			throw new LibException(ExceptionCode.PARAMETER_ERROR);
		}
		Profile profile = null;
		if (isPage(profileId)) {
			profile = showPage(profileId);
		} else {
			profile = showUser(profileId);
		}
		return profile;
	}

	@Override
	public List<Status> getStatuses(String profileId,
			Paging<Status> paging) throws LibException {
		if (paging == null) {
			throw new LibException(ExceptionCode.PARAMETER_ERROR);
		}
		if (!paging.isPagePaging()) {
			initPagePaging(paging);
		}
		TreeMap<String, String> params = new TreeMap<String, String>();
		params.put("method", "status.gets");
		if (StringUtil.isNotEmpty(profileId)) {
			params.put("uid", profileId);
		}
		params.put("page", String.valueOf(paging.getPageIndex()));
		params.put("count", String.valueOf(paging.getPageSize()));

		String jsonString = sendPostRestRequest(params);
		List<Status> statusList = RenRenStatusAdapter.createStatusList(jsonString);
		if (statusList.size() > 0) {
			Profile profile = showProfile(profileId);
			for (Status status : statusList) {
				status.setFrom(profile);
			}
		}
		updatePaging(statusList, paging);
		return statusList;
	}

	@Override
	public Status showStatus(String statusId, String ownerId) throws LibException {
		if (StringUtil.isEmpty(statusId)) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		if (StringUtil.isEmpty(ownerId)) {
			ownerId = getUserId();
		}
		TreeMap<String, String> params = new TreeMap<String, String>();
		params.put("method", "status.get");
		params.put("owner_id", ownerId);

		String jsonString = sendPostRestRequest(params);
		Status status = RenRenStatusAdapter.createStatus(jsonString);
		status.setFrom(showProfile(ownerId));
		return status;
	}


	private String sendPostRestRequest(TreeMap<String, String> parameters) throws LibException {
		if (parameters == null) {
			return null;
		}
		return sendPostRestRequest(parameters, null);
	}

	private String sendPostRestRequest(TreeMap<String, String> parameters,
			Map<String, File> fileParameters) throws LibException {
		if (parameters == null) {
			return null;
		}

		parameters.put(OAuth2.ACCESS_TOKEN, auth.getAuthToken());
		parameters.put("v", API_VERSION);
		parameters.put("format", RESPONSE_FORMAT);
		parameters.put("call_id", String.valueOf(System.currentTimeMillis()));

		StringBuffer sb = new StringBuffer();
		Iterator<Map.Entry<String, String>> iterator = parameters.entrySet()
				.iterator();
		Map.Entry<String, String> entry = null;
		while (iterator.hasNext()) {
			entry = iterator.next();
			sb.append(entry.getKey());
			sb.append("=");
			sb.append(entry.getValue());
		}
		sb.append(oauthConf.getOAuthConsumerSecret());
		String sig = "";
		try {
			sig = EncryptUtil.getMD5(new String(sb.toString().getBytes("UTF-8")));
		} catch (UnsupportedEncodingException e) {
		}
		parameters.put("sig", sig);

		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(
				HttpMethod.POST, REST_BASE, auth);
		httpRequestMessage.addParameters(parameters);
		httpRequestMessage.addParameters(fileParameters);

		return HttpRequestHelper.execute(httpRequestMessage, responseHandler);
	}

	/** PhotoMethods */

	@Override
	public boolean uploadPhoto(File photo, String caption) throws LibException {
		return uploadPhoto(photo, null, caption);
	}

	@Override
	public boolean uploadPhoto(File photo, String albumId, String caption) throws LibException {
		if (photo == null) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		TreeMap<String, String> params = new TreeMap<String, String>();
		params.put("method", "photos.upload");
		if (StringUtil.isNotEmpty(caption)) {
			String specCaption = RenRenEmotions.specializeEmotion(ServiceProvider.RenRen, caption);
			params.put("caption", specCaption);
		}
		if (StringUtil.isNotEmpty(albumId)) {
			params.put("aid", albumId);
		}
		Map<String, File> fileParameters = new TreeMap<String, File>();
		fileParameters.put("upload", photo);
		String response = sendPostRestRequest(params, fileParameters);
		try {
			JSONObject json = new JSONObject(response);
			return json.has("pid");
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR, e);
		}
	}

	@Override
	public Photo showPhoto(String photoId, String ownerId) throws LibException {
		if (StringUtil.isEmpty(photoId)) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		if (StringUtil.isEmpty(ownerId)) {
			ownerId = getUserId();
		}
		TreeMap<String, String> params = new TreeMap<String, String>();
		params.put("method", "photos.get");
		params.put("pids", photoId);
		params.put("uid", ownerId);
		String response = sendPostRestRequest(params);
		List<Photo> photos = RenRenPhotoAdapter.createPhotoList(response);
		Photo photo = null;
		if (photos.size() > 0) {
			photo = photos.get(0);
			photo.setFrom(showProfile(ownerId));
		}
		return photo;
	}

	@Override
	public boolean destroyPhoto(String photoId) throws LibException {
		return false;
	}

	@Override
	public boolean createAlbum(String name, String description, String ownerId, Privacy privacy)
			throws LibException {
		if (StringUtil.isEmpty(name)) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		TreeMap<String, String> params = new TreeMap<String, String>();
		params.put("method", "photos.createAlbum");
		params.put("name", name);
		if (StringUtil.isNotEmpty(description)) {
			params.put("description", description);
		}
		if (privacy != null) {
			int visible = 99; // 所有人可见
			switch (privacy.getValue()) {
			case EVERYONE:
				visible = 99;
				break;
			case ALL_FRIENDS:
				visible = 1;
				break;
			case SELF:
				visible = -1;
			}
			params.put("visable", String.valueOf(visible));
		}
		String response = sendPostRestRequest(params);
		try {
			JSONObject json = new JSONObject(response);
			return json.has("aid");
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR);
		}
	}

	@Override
	public boolean destroyAlbum(String albumId) throws LibException {
		return false;
	}

	@Override
	public List<Album> getAlbums(String ownerId, Paging<Album> paging) throws LibException {
		if (paging == null) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		if (!paging.isPagePaging()) {
			initPagePaging(paging);
		}
		if (StringUtil.isEmpty(ownerId)) {
			ownerId = getUserId();
		}
		TreeMap<String, String> params = new TreeMap<String, String>();
		params.put("method", "photos.getAlbums");
		params.put("uid", ownerId);
		params.put("page", String.valueOf(paging.getPageIndex()));
		params.put("count", String.valueOf(paging.getPageSize()));
		String response = sendPostRestRequest(params);
		List<Album> albumList = RenRenAlbumAdapter.createAlbumList(response);
		updatePaging(albumList, paging);
		return albumList;
	}

	@Override
	public Album showAlbum(String albumId, String ownerId) throws LibException {
		return null;
	}

	@Override
	public List<Photo> getAlbumPhotos(String albumId, String ownerId, Paging<Photo> paging)
			throws LibException {
		if (StringUtil.isEmpty(albumId) || paging == null) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		if (StringUtil.isEmpty(ownerId)) {
			ownerId = getUserId();
		}
		if (!paging.isPagePaging()) {
			initPagePaging(paging);
		}
		TreeMap<String, String> params = new TreeMap<String, String>();
		params.put("method", "photos.get");
		params.put("aid", albumId);
		params.put("uid", ownerId);
		String response = sendPostRestRequest(params);
		List<Photo> photos = RenRenPhotoAdapter.createPhotoList(response);
		if (photos.size() > 0) {
			Profile from = showProfile(ownerId);
			for (Photo photo : photos) {
				photo.setFrom(from);
			}
		}
		updatePaging(photos, paging);
		return photos;
	}

	@Override
	public boolean createFriendListMember(String listId, String userId)
			throws LibException {
		return false;
	}

	@Override
	public boolean destroyFriendListMember(String listId, String userId)
			throws LibException {
		return false;
	}

	@Override
	public List<User> getFriendListMember(String listId, Paging<User> paging)
			throws LibException {
		return null;
	}

	@Override
	public boolean destroyStatus(String statusId) throws LibException {
		return false;
	}

	@Override
	public boolean destroyNote(String noteId) throws LibException {
		return false;
	}

}
