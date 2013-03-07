package com.shejiaomao.weibo.db;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.cattong.commons.util.StringUtil;
import com.cattong.entity.Passport;
import com.cattong.entity.PointsLevel;
import com.shejiaomao.weibo.common.Constants;

public class ConfigSystemDao {

	private static final String TABLE = "Config_System";
	private static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss";
	private DateFormat dateFormat;
	private DBHelper dbHelper;

	public ConfigSystemDao(Context context) {
		dbHelper = DBHelper.getInstance(context);
		dateFormat = new SimpleDateFormat(DATE_FORMAT_PATTERN);
	}

	public void savePassport(Passport passport) {
		if (passport == null) {
			return;
		}

		SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
		sqLiteDatabase.beginTransaction();
		try {
			put(sqLiteDatabase, Constants.PASSPORT_EMAIL,
					passport.getEmail(), "通行证邮箱");
			put(sqLiteDatabase, Constants.PASSPORT_USERNAME,
					passport.getUsername(), "通行证用户名");
			put(sqLiteDatabase, Constants.PASSPORT_TOKEN,
					passport.getAccessToken(), "通行证认证Token");
			
			put(sqLiteDatabase, Constants.PASSPORT_STATE,
					passport.getState(), "通行证状态");
			put(sqLiteDatabase, Constants.PASSPORT_VIP,
					passport.isVip(), "通行证是否VIP");
			
			PointsLevel pointLevel = passport.getPointsLevel();
			if (pointLevel != null) {
			    put(sqLiteDatabase, Constants.PASSPORT_POINTS,
					pointLevel.getPoints(), "通行证积分");
			    put(sqLiteDatabase, Constants.PASSPORT_TITLE,
					pointLevel.getTitle(), "通行证头衔");
			}
			sqLiteDatabase.setTransactionSuccessful();
		} finally {
			sqLiteDatabase.endTransaction();
		}
	}

	public Passport getPassport() {
		Passport passport = new Passport();
		passport.setEmail(getString(Constants.PASSPORT_EMAIL));
		passport.setUsername(getString(Constants.PASSPORT_USERNAME));
		passport.setAccessToken(getString(Constants.PASSPORT_TOKEN));
		passport.setState(getInt(Constants.PASSPORT_STATE));
		passport.setVip(getBoolean(Constants.PASSPORT_VIP));
		
		PointsLevel pointsLevel = new PointsLevel();
		int points = getInt(Constants.PASSPORT_POINTS);
		pointsLevel.setPoints(points);
		pointsLevel.setTitle(getString(Constants.PASSPORT_TITLE));
		passport.setPointsLevel(pointsLevel);
		
		return passport;
	}

	public void destroyPassport() {
		SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
		sqLiteDatabase.beginTransaction();
		try {
			put(sqLiteDatabase, Constants.PASSPORT_EMAIL,
					null, "通行证邮箱");
			put(sqLiteDatabase, Constants.PASSPORT_USERNAME,
					null, "通行证用户名");
			put(sqLiteDatabase, Constants.PASSPORT_TOKEN,
					null, "通行证认证Token");
			put(sqLiteDatabase, Constants.PASSPORT_STATE,
					null, "通行证状态");
			put(sqLiteDatabase, Constants.PASSPORT_VIP,
					null, "通行证是否VIP");
			put(sqLiteDatabase, Constants.PASSPORT_POINTS,
					0, "通行证积分");
			put(sqLiteDatabase, Constants.PASSPORT_TITLE,
					null, "通行证头衔");
			sqLiteDatabase.setTransactionSuccessful();
		} finally {
			sqLiteDatabase.endTransaction();
		}
	}

	/**
	 * 保持设置
	 *
	 * @param name
	 * @param value
	 */
	public void put(String key, Object value, String desc) {
		checkNull(key);
		SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
		put(sqLiteDatabase, key, value, desc);
	}

	/*package*/ void put(SQLiteDatabase sqLiteDatabase,
					String key, Object value, String desc){
		ContentValues values = new ContentValues();
		String savedValue = null;
		if (value != null) {
			savedValue = value.toString();
			if (value instanceof Date) {
				savedValue = dateFormat.format((Date)value);
			}
		}

		values.put("Config_Key", key);
		values.put("Config_Value", savedValue);
		values.put("Config_Description", desc);

		sqLiteDatabase.replace(TABLE, null, values);
	}

	/**
	 * 删除设置项
	 *
	 * @param name
	 * @param value
	 */
	public void delete(String key) {
		checkNull(key);

		SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
		StringBuilder sql = new StringBuilder();
		sql.append("DELETE FROM ").append(TABLE);
		sql.append(" WHERE Config_Key = '").append(key).append("'");
		sqLiteDatabase.execSQL(sql.toString());
	}

	/**
	 * 获取指定的设置值
	 *
	 * @param name
	 *            设置名称
	 * @return 设置值
	 */
	public String getString(String key) {
		SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
		return get(sqLiteDatabase, key);
	}

	public int getInt(String key) {
		String value = getString(key);
		if (StringUtil.isNotEmpty(value)) {
			return Integer.valueOf(value);
		}
		return Integer.MIN_VALUE;
	}

	public double getDouble(String key) {
		String value = getString(key);
		if (StringUtil.isNotEmpty(value)) {
			return Double.valueOf(value);
		}
		return Double.MIN_VALUE;
	}

	public float getFloat(String key) {
		String value = getString(key);
		if (StringUtil.isNotEmpty(value)) {
			return Float.valueOf(value);
		}
		return Float.MIN_VALUE;
	}

	public Date getDate(String key) throws ParseException {
		String value = getString(key);
		if (StringUtil.isNotEmpty(value)) {
			return dateFormat.parse(value);
		}
		return null;
	}

	public boolean getBoolean(String key) {
		String value = getString(key);
		if (StringUtil.isNotEmpty(value)) {
			return Boolean.valueOf(value);
		}
		return Boolean.FALSE;
	}

	/*package*/ String get(SQLiteDatabase sqLiteDatabase,String name) {
		checkNull(name);
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT Config_Value FROM ").append(TABLE);
		sql.append(" WHERE Config_Key = '").append(name).append("'");

		Cursor cursor = sqLiteDatabase.rawQuery(sql.toString(), null);
		String value = null;
		if (null != cursor ) {
			if(cursor.moveToFirst()){
				value = cursor.getString(cursor.getColumnIndex("Config_Value"));
			}
			cursor.deactivate();
			cursor.close();
		}
		return value;
	}

	/**
	 * 获取所有设置
	 *
	 * @return Hashtable 保存设置信息键值对的Hashtable
	 */
	public Hashtable<String, String> getConfigKeyValueMap() {
		Hashtable<String, String> settings = new Hashtable<String, String>();
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT Config_Key, Config_Value From ").append(TABLE);

		SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
		Cursor cursor = sqLiteDatabase.rawQuery(sql.toString(), null);
		if (null != cursor) {
			if(cursor.moveToFirst()){
				do {
					settings.put(cursor.getString(cursor.getColumnIndex("Config_Key")), cursor.getString(cursor
							.getColumnIndex("Config_Value")));
				} while (cursor.isAfterLast());
			}
			cursor.deactivate();
			cursor.close();
		}
		return settings;
	}

	/**
	 * 检查参数是否为空
	 *
	 * @param account
	 */
	private void checkNull(Object... object) {
		for (Object o : object) {
			if (null == o) {
				throw new NullPointerException("Parameter is null");
			}
		}
	}

}
