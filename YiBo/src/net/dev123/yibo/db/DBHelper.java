package net.dev123.yibo.db;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.dev123.commons.ServiceProvider;
import net.dev123.commons.http.auth.Authorization;
import net.dev123.commons.oauth.config.OAuthConfiguration;
import net.dev123.commons.oauth.config.OAuthConfigurationFactory;
import net.dev123.commons.util.StringUtil;
import net.dev123.yibo.R;
import net.dev123.yibo.common.Constants;
import net.dev123.yibome.entity.Account;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * DBHelper
 *
 * @version
 * @author 马庆升
 * @time 2010-8-20 下午09:56:55
 */
public class DBHelper extends SQLiteOpenHelper {

	private static final int DB_VERSION = 35;
	private static final String DB_NAME = "yibo.db";

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

	public void onCreate(SQLiteDatabase sqLiteDatabase) {
		for (int i = 0; i < createSQL.length; i++) {
			sqLiteDatabase.execSQL(createSQL[i]);
		}
	}

	public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
		if (newVersion > oldVersion) {
			List<ContentValues> datas = retrieveAccountsData(sqLiteDatabase);
			execSQLs(sqLiteDatabase, destroySQL);
			execSQLs(sqLiteDatabase, createSQL);
			if (datas != null && datas.size() > 0) {
				for (ContentValues data : datas) {
					sqLiteDatabase.insert("Account", null, data);
				}
			}
		}
	}

	private void execSQLs(SQLiteDatabase sqLiteDatabase, String[] sqls) {
		if (sqls != null && sqls.length > 0) {
			for (int i = 0; i < sqls.length; i++) {
				if (Constants.DEBUG) {
					Log.d("EXECSQL", sqls[i]);
				}
				sqLiteDatabase.execSQL(sqls[i]);
			}
		}
	}

	private List<ContentValues> retrieveAccountsData(SQLiteDatabase db) {
		List<ContentValues> datas = null;
		Cursor cursor = db.rawQuery("select * from Account", null);
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				datas = new ArrayList<ContentValues>();
				do {
					datas.add(extractDataFromCursor(db, cursor));
				} while (cursor.moveToNext());
			}
			cursor.deactivate();
			cursor.close();
			cursor = null;
		}
		return datas;
	}

	private ContentValues extractDataFromCursor(SQLiteDatabase db, Cursor cursor) {
		ContentValues contentValues = new ContentValues();
		contentValues.put("Auth_Token",
			cursor.getString(cursor.getColumnIndex("Auth_Token")));
		contentValues.put("Auth_Secret",
			cursor.getString(cursor.getColumnIndex("Auth_Secret")));
		contentValues.put("Service_Provider",
			cursor.getInt(cursor.getColumnIndex("Service_Provider")));
		contentValues.put("User_ID",
			cursor.getString(cursor.getColumnIndex("User_ID")));
		contentValues.put("Screen_Name",
			cursor.getString(cursor.getColumnIndex("Screen_Name")));

		int spNo = contentValues.getAsInteger("Service_Provider");
		ServiceProvider sp = ServiceProvider.getServiceProvider(spNo);

		int columnIndex = 0;

		columnIndex = cursor.getColumnIndex("Auth_Version");
		int authVersion = Authorization.AUTH_VERSION_BASIC;
		if (columnIndex > 0) {
			authVersion = cursor.getInt(columnIndex);
		} else {
			columnIndex = cursor.getColumnIndex("Is_OAuth");
			if (columnIndex > 0 && cursor.getInt(columnIndex) == 1) {
				authVersion = Authorization.AUTH_VERSION_OAUTH_1;
			}
		}
		contentValues.put("Auth_Version", authVersion);

		columnIndex = cursor.getColumnIndex("Token_Expires_At");
		if (columnIndex > 0) {
			contentValues.put("Token_Expires_At", cursor.getLong(columnIndex));
		} else {
			contentValues.put("Token_Expires_At", -1);
		}

		columnIndex = cursor.getColumnIndex("Token_Scopes");
		if (columnIndex > 0) {
			contentValues.put("Token_Scopes", cursor.getString(columnIndex));
		}

		columnIndex = cursor.getColumnIndex("State");
		if (columnIndex > 0) {
			contentValues.put("State", cursor.getInt(columnIndex));
		} else {
			contentValues.put("State", Account.STATE_ADDED);
		}

		columnIndex = cursor.getColumnIndex("Created_At");
		if (columnIndex > 0) {
			contentValues.put("Created_At", cursor.getLong(columnIndex));
		} else {
			contentValues.put("Created_At", new Date().getTime());
		}

		columnIndex = cursor.getColumnIndex("App_Key");
		OAuthConfiguration defaultConfig
	    	= OAuthConfigurationFactory.getOAuthConfiguration(sp);
		if (columnIndex > 0) {
			contentValues.put("App_Key", cursor.getString(columnIndex));
			columnIndex = cursor.getColumnIndex("App_Secret");
			if (columnIndex > 0) {
				contentValues.put("App_Secret", cursor.getString(columnIndex));
			} else if (StringUtil.isEquals(contentValues.getAsString("App_Key"),
						defaultConfig.getOAuthConsumerKey())) {
				contentValues.put("App_Secret", defaultConfig.getOAuthConsumerSecret());
			} else {
				contentValues.put("App_Secret", "NULL");
			}
		} else if (authVersion == Authorization.AUTH_VERSION_OAUTH_1
					|| authVersion == Authorization.AUTH_VERSION_OAUTH_2){
			contentValues.put("App_Key", defaultConfig.getOAuthConsumerKey());
			contentValues.put("App_Secret", defaultConfig.getOAuthConsumerSecret());
		} else {
			contentValues.put("App_Key", "NULL");
			contentValues.put("App_Secret", "NULL");
		}

		columnIndex = cursor.getColumnIndex("Is_Default");
		if (columnIndex > 0) {
			contentValues.put("Is_Default", cursor.getInt(columnIndex));
		} else {
			// SQLite does not have a separate Boolean storage class.
			// Instead, Boolean values are stored as integers 0 (false) and 1 (true).
			contentValues.put("Is_Default", 0);
		}

		if (sp == ServiceProvider.Twitter
			&& authVersion == Authorization.AUTH_VERSION_BASIC) {
			//处理Twitter使用代理的情况，
			columnIndex = cursor.getColumnIndex("Rest_Proxy_Url");
			if (columnIndex > 0) {
				contentValues.put("Rest_Proxy_Url", cursor.getString(columnIndex));
			}

			columnIndex = cursor.getColumnIndex("Search_Proxy_Url");
			if (columnIndex > 0) {
				contentValues.put("Search_Proxy_Url", cursor.getString(columnIndex));
			}

			if (!contentValues.containsKey("Rest_Proxy_Url")) {
				//如果Account表列中无代理配置，则从Setting表中找
				//以下是处理1.0版本的Twitter代理存储在Setting表的情况
				long accountId = cursor.getLong(cursor.getColumnIndex("Account_ID"));
				Cursor settingCursor =
					db.rawQuery("select Setting_Value from Setting where Setting_Name = ?",
						new String[]{"rest_" + accountId});
				if (settingCursor != null) {
					if (settingCursor.moveToFirst()) {
			        	contentValues.put("Rest_Proxy_Url", settingCursor.getString(0));
					}
					settingCursor.deactivate();
					settingCursor.close();
					settingCursor = null;
				}
			}
		}

		return contentValues;
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
