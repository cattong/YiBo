package com.shejiaomao.weibo.db;

import com.shejiaomao.maobo.R;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.cattong.commons.Logger;
import com.shejiaomao.widget.SQLiteOpenHelper;

/**
 * DBHelper
 *
 */
public class DBHelper extends SQLiteOpenHelper {

	private static final int DB_VERSION = 60;
	private static final String DB_NAME = "shejiaomao.db";

	private String[] createSQL = null;
	private String[] destroySQL = null;

	private static DBHelper dbHelper;

	public static synchronized DBHelper getInstance(Context context){
		if (dbHelper == null) {
			dbHelper = new DBHelper(context);
		}
		return dbHelper;
	}

	private DBHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		initSQL(context);
	}

	private void initSQL(Context context) {
		createSQL = context.getResources().getStringArray(R.array.db_create_sql);
		destroySQL = context.getResources().getStringArray(R.array.db_destroy_sql);
	}

	@Override
	public void onCreate(SQLiteDatabase sqLiteDatabase) {
		for (int i = 0; i < createSQL.length; i++) {
			sqLiteDatabase.execSQL(createSQL[i]);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
		if (newVersion > oldVersion) {
			execSQLs(sqLiteDatabase, destroySQL);
			execSQLs(sqLiteDatabase, createSQL);
		}
	}

	private void execSQLs(SQLiteDatabase sqLiteDatabase, String[] sqls) {
		if (sqls != null && sqls.length > 0) {
			for (int i = 0; i < sqls.length; i++) {
				if (Logger.isDebug()) {
					Log.d("EXECSQL", sqls[i]);
				}
				sqLiteDatabase.execSQL(sqls[i]);
			}
		}
	}
	
	@Override
	protected void finalize() throws Throwable {
		if (dbHelper != null) {
			dbHelper.close();
			dbHelper = null;
		}
		super.finalize();
	}

}
