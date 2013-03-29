package com.shejiaomao.weibo.db;

import java.util.Date;
import java.util.List;

import com.shejiaomao.maobo.R;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.cattong.commons.Logger;
import com.cattong.commons.Paging;
import com.cattong.commons.ServiceProvider;
import com.cattong.commons.util.ListUtil;
import com.cattong.entity.User;
import com.cattong.weibo.entity.DirectMessage;

public class DirectMessageDao extends BaseDao<DirectMessage> {
	private static final String TABLE = "Direct_Message";
    private Context context;
	private UserDao userDao;

	public DirectMessageDao(Context context) {
		super(context);
		this.context = context;
		userDao = new UserDao(context);
	}

	public void save(DirectMessage msg, LocalAccount account) {
		if (isNull(msg) || isNull(account)) {
			return;
		}
		SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
		sqLiteDatabase.beginTransaction();
		try {
			save(sqLiteDatabase,msg,account);
			sqLiteDatabase.setTransactionSuccessful();
		} finally{
			sqLiteDatabase.endTransaction();
		}
	}

	public void batchSave(List<DirectMessage> msgs, LocalAccount account) {
		if (isNull(msgs) || isNull(account)) {
			return;
		}

		SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
		sqLiteDatabase.beginTransaction();
		try {
			for (DirectMessage msg : msgs) {
				save(sqLiteDatabase,msg,account);
			}
			sqLiteDatabase.setTransactionSuccessful();
		} finally {
			sqLiteDatabase.endTransaction();
		}
	}

	void save(SQLiteDatabase sqLiteDatabase,DirectMessage msg, LocalAccount account) {
		if (isNull(msg) || isNull(account)) {
			return;
		}
		if(Logger.isDebug()){
			Log.d("Save DirectMessage:", msg.toString());
		}
		ContentValues values = new ContentValues();
		values.put("Msg_ID", msg.getId());

		values.put("Created_At", msg.getCreatedAt() == null ? 0L : msg.getCreatedAt().getTime());
		values.put("Msg_Text", msg.getText());
		values.put("Sender_ID", msg.getSenderId());
		values.put("Recipient_ID", msg.getRecipientId());
		values.put("Sender_Screen_Name", msg.getSenderScreenName());
		values.put("Recipient_Screen_Name", msg.getRecipientScreenName());
		values.put("Service_Provider", msg.getServiceProvider().getSpNo());

		values.put("Account_ID", account.getAccountId());
		if (msg instanceof LocalDirectMessage) {
			values.put("Is_Divider", ((LocalDirectMessage)msg).isDivider() ? 1 : 0);
		} else {
			values.put("Is_Divider", 0);
		}

		sqLiteDatabase.replace(TABLE, null, values);

		userDao.save(sqLiteDatabase,msg.getRecipient());
		userDao.save(sqLiteDatabase,msg.getSender());
	}

	public int delete(DirectMessage msg, LocalAccount account) {
		if (isNull(msg) || isNull(account)) {
			return -1;
		}

		SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
		int rowsAffected = sqLiteDatabase.delete(TABLE, "Msg_ID = '" + msg.getId() + "' and Account_ID = "
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

	public List<DirectMessage> getConversations(User conversationUser, LocalAccount account, Paging<DirectMessage> page) {
		if (conversationUser == null ||
		    account == null ||
		    page == null
		) {
			return null;
		}
		Resources res = context.getResources();
        String[] querySqls = res.getStringArray(R.array.db_query_direct_message_sql);
		String sql = String.format(
			querySqls[3], conversationUser.getUserId(), conversationUser.getUserId(),
			account.getAccountId(), "");

		List<DirectMessage> listTemp = find(sql, page.getPageIndex(), page.getPageSize());
		if (ListUtil.isEmpty(listTemp)) {
			page.setLastPage(true);
		}
		return listTemp;
	}

	@Override
	public DirectMessage extractData(SQLiteDatabase sqLiteDatabase, Cursor cursor) {
		LocalDirectMessage msg = new LocalDirectMessage();
		msg.setCreatedAt(new Date(cursor.getLong(cursor.getColumnIndex("Created_At"))));
		msg.setId(cursor.getString(cursor.getColumnIndex("Msg_ID")));
		msg.setText(cursor.getString(cursor.getColumnIndex("Msg_Text")));
		msg.setRecipientId(cursor.getString(cursor.getColumnIndex("Recipient_ID")));
		msg.setSenderId(cursor.getString(cursor.getColumnIndex("Sender_ID")));
		msg.setRecipientScreenName(cursor.getString(cursor.getColumnIndex("Recipient_Screen_Name")));
		msg.setSenderScreenName(cursor.getString(cursor.getColumnIndex("Sender_Screen_Name")));
		msg.setDivider(1 == cursor.getInt(cursor.getColumnIndex("Is_Divider")));

		int sp = cursor.getInt(cursor.getColumnIndex("Service_Provider"));
		msg.setServiceProvider(ServiceProvider.getServiceProvider(sp));

		User sender = (User) userDao.findById(sqLiteDatabase, msg.getSenderId(), msg.getServiceProvider());
		msg.setSender(sender);

		User recipient = (User) userDao.findById(sqLiteDatabase, msg.getRecipientId(), msg.getServiceProvider());
		msg.setRecipient(recipient);

		return msg;
	}

}
