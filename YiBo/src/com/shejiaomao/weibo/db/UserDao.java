package com.shejiaomao.weibo.db;

import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.cattong.commons.Logger;
import com.cattong.commons.ServiceProvider;
import com.cattong.commons.util.ListUtil;
import com.cattong.commons.util.StringUtil;
import com.cattong.entity.BaseUser;
import com.cattong.entity.Gender;

public class UserDao extends BaseDao<BaseUser> {
	private static final String TABLE = "User";

	public UserDao(Context context) {
		super(context);
	}

	public void save(BaseUser user) {
		if (isNull(user)) {
			return;
		}
		SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
		save(sqLiteDatabase,user);
	}

	public void batchSave(List<? extends BaseUser> users) {
		if (ListUtil.isEmpty(users)) {
			return;
		}
		SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
		sqLiteDatabase.beginTransaction();
		try {
			for (BaseUser user : users) {
				save(sqLiteDatabase,user);
			}
			sqLiteDatabase.setTransactionSuccessful();
		} finally{
			sqLiteDatabase.endTransaction();
		}
	}

	void save(SQLiteDatabase sqLiteDatabase, BaseUser user) {
		if (isNull(user)) {
			return;
		}
		if(Logger.isDebug()){
			Log.d("Save User:", user.toString());
		}

		ContentValues values = new ContentValues();
		values.put("Service_Provider", user.getServiceProvider().getSpNo());
		values.put("User_Id", user.getUserId());
		values.put("Screen_Name", user.getScreenName());
		values.put("Name", user.getName());
		values.put("Gender", user.getGender() == null ? Gender.Unkown.getGenderNo() : user.getGender().getGenderNo());
		values.put("Location", user.getLocation());
		values.put("Description", user.getDescription());
		values.put("Verified", user.isVerified());
		values.put("Profile_Image_Url", user.getProfileImageUrl());
		values.put("Created_At", user.getCreatedAt() == null ? 0 : user.getCreatedAt().getTime());

		sqLiteDatabase.replace(TABLE, null, values);
	}

	public int delete(BaseUser user, ServiceProvider sp) {
		if (isNull(user) || isNull(sp)) {
			return -1;
		}

		SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
		int rowsAffected = sqLiteDatabase.delete(TABLE, "User_ID = '" + user.getUserId() + "' and Service_Provider = "
				+ sp.getSpNo(), null);
		return rowsAffected;
	}

	public int delete(ServiceProvider sp) {
		if (isNull(sp)) {
			return -1;
		}

		SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
		int rowsAffected = sqLiteDatabase.delete(TABLE, "Service_Provider = " + sp.getSpNo(), null);
		return rowsAffected;
	}

	public BaseUser findById(String userId, ServiceProvider sp) {
		if (isNull(sp) || StringUtil.isEmpty(userId)) {
			return null;
		}
		SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
		return findById(sqLiteDatabase, userId, sp);
	}

	BaseUser findById(SQLiteDatabase sqLiteDatabase, String userId, ServiceProvider sp) {
		String sql = "select * from User where User_ID = '" + userId + "' and Service_Provider = "
				+ sp.getSpNo();
		return this.query(sqLiteDatabase, sql);
	}

	public List<BaseUser> findUsers(ServiceProvider sp, String filterName) {
		List<BaseUser> listUser = null;
		if (sp == null || StringUtil.isEmpty(filterName)) {
			return listUser;
		}
		StringBuilder sql = new StringBuilder();
		int providerNo = sp.getSpNo();
		sql.append(
			"select " +
			"    * " +
			"from " +
			"    User " +
			"where " +
		    "    Service_Provider = " + providerNo + " and " +
		    "    ( Screen_Name like '%" + filterName + "%' or " +
		    "      Name like '%" + filterName + "%')"
		);

		return find(sql.toString());
	}

	@Override
	public BaseUser extractData(SQLiteDatabase sqLiteDatabase, Cursor cursor) {
		int spNo = cursor.getInt(cursor.getColumnIndex("Service_Provider"));
		ServiceProvider serviceProvider = ServiceProvider.getServiceProvider(spNo);
		BaseUser user = null;
		if (serviceProvider.isSns()) {
			user = new com.cattong.sns.entity.User();
		} else {
			user = new com.cattong.entity.User();
		}
		user.setServiceProvider(serviceProvider);
		user.setUserId(cursor.getString(cursor.getColumnIndex("User_ID")));
		user.setScreenName(cursor.getString(cursor.getColumnIndex("Screen_Name")));
		user.setName(cursor.getString(cursor.getColumnIndex("Name")));
		user.setProfileImageUrl(cursor.getString(cursor.getColumnIndex("Profile_Image_Url")));
		int nGender = cursor.getInt(cursor.getColumnIndex("Gender"));
		Gender gender = Gender.Unkown;
		if (nGender == Gender.Male.getGenderNo()) {
			gender = Gender.Male;
		} else if (nGender == Gender.Female.getGenderNo()) {
			gender = Gender.Female;
		}
		user.setGender(gender);
		user.setLocation(cursor.getString(cursor.getColumnIndex("Location")));
		user.setDescription(cursor.getString(cursor.getColumnIndex("Description")));
		user.setVerified(1 == cursor.getInt(cursor.getColumnIndex("Verified")));

		long time = cursor.getLong(cursor.getColumnIndex("Created_At"));
		if (time > 0) {
			user.setCreatedAt(new Date(time));
		}

		return user;
	}

}
