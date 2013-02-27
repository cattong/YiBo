package net.dev123.mblog.netease;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import net.dev123.commons.PagableList;
import net.dev123.commons.ServiceProvider;
import net.dev123.commons.util.ParseUtil;
import net.dev123.commons.util.StringUtil;
import net.dev123.exception.ExceptionCode;
import net.dev123.exception.LibException;
import net.dev123.mblog.Emotions;
import net.dev123.mblog.entity.Comment;
import net.dev123.mblog.entity.Status;
import net.dev123.mblog.entity.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NetEaseCommentAdaptor {
	public static Comment createCommentFromStatus(Status status) throws LibException {
		if (null == status) {
			throw new NullPointerException("status is null");
		}

		Comment comment = new Comment();
		comment.setId(status.getId());
		comment.setCreatedAt(status.getCreatedAt());
		comment.setFavorited(status.isFavorited());
		comment.setInReplyToStatus(status.getRetweetedStatus());
		comment.setServiceProvider(status.getServiceProvider());
		comment.setSource(status.getSource());
		comment.setText(status.getText());
		comment.setTruncated(status.isTruncated());
		comment.setUser(status.getUser());
		comment.setServiceProvider(ServiceProvider.NetEase);
		return comment;
	}

	public static List<Comment> createCommentsList(String jsonString) throws LibException{
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

	public static PagableList<Comment> createPagableCommentsList(String jsonString) throws LibException {
		try {
			if ("[]".equals(jsonString) || "{}".equals(jsonString)) {
				return new PagableList<Comment>(0, 0, 0);
			}
			JSONObject json = new JSONObject(jsonString);
			JSONArray jsonList = json.getJSONArray("statuses");
			int size = jsonList.length();
			long nextCursor = json.getLong("cursor_id");
			PagableList<Comment> commentList = new PagableList<Comment>(size, -1, nextCursor);
			for (int i = 0; i < size; i++) {
				commentList.add(createComment(jsonList.getJSONObject(i)));
			}
			return commentList;
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR);
		}
	}

	static Comment createComment(JSONObject json) throws LibException {
		try {
			Comment comment = new Comment();
			comment.setCreatedAt(ParseUtil.getDate("created_at", json));
			comment.setFavorited(ParseUtil.getBoolean("favorited", json));
			String id = ParseUtil.getRawString("cursor_id", json);
			if (StringUtil.isEmpty(id)) {
				id = ParseUtil.getRawString("id", json);
			}
			comment.setId(id);
			comment.setTruncated(ParseUtil.getBoolean("truncated", json));
			comment.setSource(ParseUtil.getRawString("source", json));
			comment.setText(Emotions.normalizeEmotion(ServiceProvider.NetEase,
					ParseUtil.getRawString("text", json)));
			if (!json.isNull("user")) {
				comment.setUser(NetEaseUserAdaptor.createUser(json.getJSONObject("user")));
			}
			if (!json.isNull("status")) {
				comment.setInReplyToStatus(NetEaseStatusAdaptor.createStatus(json.getJSONObject("status")));
			}
			Status srcStatus = new Status();
			srcStatus.setText(Emotions.normalizeEmotion(ServiceProvider.NetEase,
					ParseUtil.getRawString("in_reply_to_status_text", json)));
			srcStatus.setId(ParseUtil.getRawString("in_reply_to_status_id", json));
			srcStatus.setServiceProvider(ServiceProvider.NetEase);
			
			User srcUser = new User();
			srcUser.setId(ParseUtil.getRawString("in_reply_to_user_id", json));
			srcUser.setName(ParseUtil.getRawString("in_reply_to_screen_name", json));
			srcUser.setScreenName(ParseUtil.getRawString("in_reply_to_user_name", json));
			srcUser.setServiceProvider(ServiceProvider.NetEase);
			
			srcStatus.setUser(srcUser);
			comment.setInReplyToStatus(srcStatus);
			
			comment.setServiceProvider(ServiceProvider.NetEase);
			return comment;
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR);
		} catch (ParseException e) {
			throw new LibException(ExceptionCode.DATE_PARSE_ERROR);
		}
	}
}
