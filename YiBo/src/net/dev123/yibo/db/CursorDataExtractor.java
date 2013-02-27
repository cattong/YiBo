package net.dev123.yibo.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * CursorDataExtractor
 *
 * @author 马庆升
 * @time 2010-8-10 下午04:26:01
 * @param <T>
 */
public interface CursorDataExtractor<T> {
	T extractData(SQLiteDatabase sqLiteDatabase, Cursor cursor);
}
