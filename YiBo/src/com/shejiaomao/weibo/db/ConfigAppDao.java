package com.shejiaomao.weibo.db;

import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.cattong.commons.ServiceProvider;
import com.cattong.commons.util.ListUtil;
import com.cattong.commons.util.StringUtil;
import com.cattong.entity.ConfigApp;

public class ConfigAppDao extends BaseDao<ConfigApp> {
	private static final String TABLE = "Config_App";

	public ConfigAppDao(Context context) {
		super(context);
	}

	public void save(ConfigApp app) {
		if (isNull(app)) {
			return;
		}
		SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
		save(sqLiteDatabase, app);
	}

	public void batchSave(List<ConfigApp> apps) {
		if (ListUtil.isEmpty(apps)) {
			return;
		}
		SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
		sqLiteDatabase.beginTransaction();
		try {
			for (ConfigApp app : apps) {
				save(sqLiteDatabase, app);
			}
			sqLiteDatabase.setTransactionSuccessful();
		} finally {
			sqLiteDatabase.endTransaction();
		}
	}

	void save(SQLiteDatabase sqLiteDatabase, ConfigApp app) {
		if (isNull(app)) {
			return;
		}

		ContentValues values = new ContentValues();
		values.put("Service_Provider", app.getServiceProviderNo());
		values.put("App_Name", app.getAppName());
		values.put("App_Key", app.getAppKey());
		values.put("App_Secret", app.getAppSecret());
		values.put("Auth_Version", app.getAuthVersion());
		values.put("Auth_Flow", app.getAuthFlow());
		values.put("Callback_Url", app.getCallbackUrl());
		long rowId = sqLiteDatabase.insert(TABLE, null, values);
		app.setAppId(rowId);
	}
	
	public int update(ConfigApp app) {
		if (isNull(app) || app.getAppId() <= 0L) {
			return -1;
		}
		
		SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
		sqLiteDatabase.beginTransaction();

		int rowsAffected = 0;
		try {
			ContentValues values = new ContentValues();
			values.put("Service_Provider", app.getServiceProviderNo());
			values.put("App_Name", app.getAppName());
			values.put("App_Key", app.getAppKey());
			values.put("App_Secret", app.getAppSecret());
			values.put("Auth_Version", app.getAuthVersion());
			values.put("Auth_Flow", app.getAuthFlow());
			values.put("Callback_Url", app.getCallbackUrl());
			rowsAffected = sqLiteDatabase.update(TABLE, values, 
				"App_ID = " + app.getAppId(), null);
			sqLiteDatabase.setTransactionSuccessful();
		} finally {
			sqLiteDatabase.endTransaction();
		}

		return rowsAffected;
		
	}


	public int delete(ConfigApp app) {
		if (isNull(app) || app.getAppId() <= 0L) {
			return -1;
		}

		SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
		int rowsAffected = sqLiteDatabase.delete(TABLE,
				"App_ID = " + app.getAppId() , null);
		return rowsAffected;
	}

	public ConfigApp findByAppKey(String appKey, ServiceProvider sp) {
		if (StringUtil.isEmpty(appKey)) {
			return null;
		}
		StringBuilder sql = new StringBuilder();
		sql.append("select * from ").append(TABLE);
		sql.append(" where App_Key = '").append(appKey).append("'");
		if (sp != null) {
			sql.append(" and Service_Provider = ").append(sp.getSpNo());
		}
		return query(sql.toString());
	}
	
	public ConfigApp findById(String appId, ServiceProvider sp) {
		if (isNull(sp) || StringUtil.isEmpty(appId)) {
			return null;
		}
		SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
		return findById(sqLiteDatabase, appId, sp);
	}

	ConfigApp findById(SQLiteDatabase sqLiteDatabase, String appId,
			ServiceProvider sp) {
		String sql = "select * from " + TABLE + " where App_ID = " + appId
				+ " and Service_Provider = " + sp.getSpNo();
		return this.query(sqLiteDatabase, sql);
	}

	public List<ConfigApp> findAll() {
		return find("select * from " + TABLE);
	}
	
	public List<ConfigApp> findApps(ServiceProvider sp) {
		if (sp == null) {
			return null;
		}
		StringBuilder sql = new StringBuilder();
		int providerNo = sp.getSpNo();
		sql.append("select * from " + TABLE + " where "
				+ " Service_Provider = " + providerNo);

		return find(sql.toString());
	}

	@Override
	public ConfigApp extractData(SQLiteDatabase sqLiteDatabase, Cursor cursor) {
		int spNo = cursor.getInt(cursor.getColumnIndex("Service_Provider"));
		ConfigApp app = new ConfigApp();
		app.setServiceProviderNo(spNo);
		app.setAppId(cursor.getLong(cursor.getColumnIndex("App_ID")));
		app.setAppName(cursor.getString(cursor.getColumnIndex("App_Name")));
		app.setAppKey(cursor.getString(cursor.getColumnIndex("App_Key")));
		app.setAppSecret(cursor.getString(cursor.getColumnIndex("App_Secret")));
		app.setAuthFlow(cursor.getInt(cursor.getColumnIndex("Auth_Flow")));
		app.setAuthVersion(cursor.getInt(cursor.getColumnIndex("Auth_Version")));
		app.setState(ConfigApp.STATE_ENABLED);
		app.setShared(false);
		app.setCallbackUrl(cursor.getString(cursor.getColumnIndex("Callback_Url")));
		return app;
	}

}
