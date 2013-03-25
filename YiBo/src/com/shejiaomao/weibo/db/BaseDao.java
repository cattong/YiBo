package com.shejiaomao.weibo.db;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.cattong.commons.Logger;


public abstract class BaseDao<T> implements CursorDataExtractor<T> {
    protected Context context;
	protected DBHelper dbHelper;

	public BaseDao(Context context) {
		if (null == context) {
			throw new NullPointerException("context should not be null");
		}
		this.context = context;
		this.dbHelper = DBHelper.getInstance(context);
	}

	public List<T> find(String sql, int pageIndex, int pageSize) {
		int offset = (pageIndex - 1) * pageSize;
		sql = "select * from (" + sql + ")" + " limit " + pageSize + " offset " + offset;
		return find(sql);
	}

	public List<T> find(String sql) {
		SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
		return find(sqLiteDatabase, sql);
	}

	List<T> find(SQLiteDatabase sqLiteDatabase, String sql) {
		if (Logger.isDebug()) {
			Log.d("SQLiteDatabase Query", sql);
		}
		List<T> resultList = null;
		Cursor cursor = sqLiteDatabase.rawQuery(sql, null);
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				resultList = new ArrayList<T>();
				do {
					resultList.add(extractData(sqLiteDatabase, cursor));
				} while (cursor.moveToNext());
			}
			cursor.deactivate();
			cursor.close();
			cursor = null;
		}
		return resultList;
	}

	public T query(String sql) {
		SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
		return query(sqLiteDatabase, sql);
	}

	T query(SQLiteDatabase sqLiteDatabase , String sql) {
		T t = null;
		Cursor cursor = sqLiteDatabase.rawQuery(sql, null);
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				t = extractData(sqLiteDatabase, cursor);
			}
			cursor.deactivate();
			cursor.close();
			cursor = null;
		}
		return t;
	}

	/**
	 * 检查参数是否为空
	 *
	 * @param account
	 */
	protected static boolean isNull(Object object) {
		if (object == null) {
			return true;
		}
		if (object instanceof List<?>) {
			return ((List<?>)object).size() == 0;
		}

		return false;
	}

}
