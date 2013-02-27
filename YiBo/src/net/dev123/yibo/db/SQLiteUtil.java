package net.dev123.yibo.db;

import java.util.ArrayList;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * SQLiteUtil
 *
 * @version
 * @author 马庆升
 * @time 2010-8-6 下午04:13:33
 */
public class SQLiteUtil {

	private SQLiteDatabase sqLiteDatabase = null;

	public SQLiteUtil(SQLiteDatabase sqLiteDatabase) {
		if (null == sqLiteDatabase) {
			throw new NullPointerException("SQLiteDatabase参数不能为空");
		}
		this.sqLiteDatabase = sqLiteDatabase;
	}

	public <T> ArrayList<T> queryByPage(String sql, int pageIndex, int pageSize, CursorDataExtractor<T> cursorDataExactor) {
		int offset = pageIndex * pageSize;
		sql = "select * from (" + sql + ")" + " limit " + pageSize + " offset" + offset;
		ArrayList<T> resultList = null;
		Cursor cursor = sqLiteDatabase.rawQuery(sql, null);
		if (cursor != null ) {
			if(cursor.moveToFirst()){
				resultList = new ArrayList<T>();
				do {
					resultList.add(cursorDataExactor.extractData(sqLiteDatabase, cursor));
				} while (cursor.isLast());
			}
			cursor.deactivate();
			cursor.close();
		}
		return resultList;
	}

	public <T> T query(String sql, CursorDataExtractor<T> cursorDataExactor) {
		Cursor cursor = sqLiteDatabase.rawQuery(sql, null);
		T t = null;
		if (cursor != null ) {
			if(cursor.moveToFirst()){
				t = cursorDataExactor.extractData(sqLiteDatabase, cursor);
			}
			cursor.deactivate();
			cursor.close();
		}
		return t;
	}

}
