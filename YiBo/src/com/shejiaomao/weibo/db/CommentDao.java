package com.shejiaomao.weibo.db;

import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.cattong.commons.ServiceProvider;
import com.cattong.commons.util.StringUtil;
import com.cattong.entity.Comment;
import com.cattong.entity.Status;
import com.cattong.entity.User;
import com.shejiaomao.weibo.common.StatusCatalog;

public class CommentDao extends BaseDao<Comment> {

	private static final String TABLE = "Comment";

	private UserDao userDao;
	private StatusDao statusDao;

	public CommentDao(Context context) {
		super(context);
		userDao = new UserDao(context);
		statusDao = new StatusDao(context);
	}

	public void save(Comment comment, LocalAccount account) {
		if (isNull(comment)) {
			return;
		}

		SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
		sqLiteDatabase.beginTransaction();
		try {
			save(sqLiteDatabase, comment, account);
			sqLiteDatabase.setTransactionSuccessful();
		} finally {
			sqLiteDatabase.endTransaction();
		}
	}

	public void batchSave(List<Comment> comments, LocalAccount account) {
		if (isNull(comments) || isNull(account)) {
			return;
		}

		SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
		sqLiteDatabase.beginTransaction();
		try {
			for (Comment comment : comments) {
				save(sqLiteDatabase, comment, account);
			}
			sqLiteDatabase.setTransactionSuccessful();
		} finally {
			sqLiteDatabase.endTransaction();
		}
	}


	void save(SQLiteDatabase sqLiteDatabase, Comment comment, LocalAccount account) {
		if (isNull(comment)) {
			return;
		}

		ContentValues values = new ContentValues();
		values.put("Comment_ID", comment.getCommentId());
		values.put("Created_At", comment.getCreatedAt() == null ? 0 : comment.getCreatedAt().getTime());
		values.put("Text", comment.getText());
		values.put("Source", comment.getSource());
		values.put("Is_Truncated", comment.isTruncated() ? 1 : 0);
		values.put("Is_Favorated", comment.isFavorited() ? 1 : 0);
		values.put("Service_Provider", comment.getServiceProvider().getSpNo());

		values.put("Account_ID", account == null ? -1 : account.getAccountId());
		if (comment instanceof LocalComment) {
			values.put("Is_Divider", ((LocalComment)comment).isDivider() ? 1 : 0);
		} else {
			values.put("Is_Divider", 0);
		}

		if (comment.getReplyToComment() != null) {
			save(sqLiteDatabase, comment.getReplyToComment(), null);
			values.put("In_Reply_To_Comment_ID", comment.getReplyToComment().getCommentId());
		}

		if (comment.getReplyToStatus() != null) {
			statusDao.save(sqLiteDatabase, comment.getReplyToStatus(), StatusCatalog.Others, null);
			values.put("In_Reply_To_Status_ID", comment.getReplyToStatus().getStatusId());
		}

		if (comment.getUser() != null) {
			userDao.save(sqLiteDatabase, comment.getUser());
			values.put("User_ID", comment.getUser().getUserId());
		}

		sqLiteDatabase.replace(TABLE, null, values);

	}

	public int delete(Comment comment, LocalAccount account) {
		if (isNull(comment) || isNull(account)) {
			return -1;
		}

		SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();

		int rowsAffected = sqLiteDatabase.delete(TABLE, "Comment_ID = '" + comment.getCommentId() + "' and Account_ID = "
					+ account.getAccountId(), null);
		return rowsAffected;
	}

	public int delete(LocalAccount account) {
		if (isNull(account)) {
			return -1;
		}

		SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
		int rowsAffected = sqLiteDatabase.delete(TABLE, "Account_ID = " + account.getAccountId(), null);
		return rowsAffected;
	}

	public Comment findById(String commentId, ServiceProvider sp, boolean isReplyTo) {
		if (isNull(commentId) || isNull(sp)) {
			return null;
		}

		SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
		return findById(sqLiteDatabase, commentId, sp, isReplyTo);
	}

	Comment findById(SQLiteDatabase sqLiteDatabase , String commentId, ServiceProvider sp, boolean isReplyTo) {
		String sql = "select * from Comment where Comment_ID = '" + commentId + "' and Service_Provider = "
				+ sp.getSpNo();
		if (isReplyTo) {
			sql += " and Account_ID = -1";
		}
		return this.query(sqLiteDatabase, sql);
	}


	@Override
	public Comment extractData(SQLiteDatabase sqLiteDatabase, Cursor cursor) {
		LocalComment comment = new LocalComment();
		comment.setCommentId(cursor.getString(cursor.getColumnIndex("Comment_ID")));
		long time = cursor.getLong(cursor.getColumnIndex("Created_At"));
		if (time > 0) {
			comment.setCreatedAt(new Date(time));
		}
		comment.setText(cursor.getString(cursor.getColumnIndex("Text")));
		comment.setSource(cursor.getString(cursor.getColumnIndex("Source")));
		comment.setFavorited(1 == cursor.getInt(cursor.getColumnIndex("Is_Favorated")));
		comment.setTruncated(1 == cursor.getInt(cursor.getColumnIndex("Is_Truncated")));
		comment.setDivider(1 == cursor.getInt(cursor.getColumnIndex("Is_Divider")));

		int sp = cursor.getInt(cursor.getColumnIndex("Service_Provider"));
		comment.setServiceProvider(ServiceProvider.getServiceProvider(sp));

		String userId = cursor.getString(cursor.getColumnIndex("User_ID"));
		if (StringUtil.isNotEmpty(userId)) {
			User user = (User) userDao.findById(sqLiteDatabase, userId, comment.getServiceProvider());
			comment.setUser(user);
		}

		String statusId = cursor.getString(cursor.getColumnIndex("In_Reply_To_Status_ID"));
		if (statusId != null) {
			Status inReplyToStatus = statusDao.findById(sqLiteDatabase, statusId, comment.getServiceProvider(), true);
			comment.setReplyToStatus(inReplyToStatus);
		}

		String commentId = cursor.getString(cursor.getColumnIndex("In_Reply_To_Comment_ID"));
		if (commentId != null) {
			Comment inReplyToComment = findById(sqLiteDatabase, commentId, comment.getServiceProvider(), true);
			comment.setReplyToComment(inReplyToComment);
		}

		return comment;
	}

}
