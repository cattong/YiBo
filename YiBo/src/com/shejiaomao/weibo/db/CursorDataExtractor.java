package com.shejiaomao.weibo.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * CursorDataExtractor
 *
 *
 * @param <T>
 */
public interface CursorDataExtractor<T> {
	T extractData(SQLiteDatabase sqLiteDatabase, Cursor cursor);
}
