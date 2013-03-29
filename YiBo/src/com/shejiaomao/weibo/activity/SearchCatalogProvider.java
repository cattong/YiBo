package com.shejiaomao.weibo.activity;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.provider.BaseColumns;

import com.cattong.commons.util.StringUtil;
import com.shejiaomao.maobo.R;

public class SearchCatalogProvider extends ContentProvider {
	public static final int SEARCH_CATALOG_STATUS = 1;
	public static final int SEARCH_CATALOG_USER = 2;
	
	private static String[] COLUMN_NAMES = {
		BaseColumns._ID, SearchManager.SUGGEST_COLUMN_TEXT_1,
		SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID,
		SearchManager.SUGGEST_COLUMN_INTENT_EXTRA_DATA};
    
	@Override
	public boolean onCreate() {
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		if (selectionArgs == null
			|| selectionArgs.length == 0
			|| StringUtil.isEmpty(selectionArgs[0])) {
			return null;
		}
		
		MatrixCursor cursor = new MatrixCursor(COLUMN_NAMES);
		Object[] row1 = new Object[]{
			new Integer(SEARCH_CATALOG_STATUS), 
			getContext().getString(R.string.lable_search_suggest_status, selectionArgs[0]),
			SEARCH_CATALOG_STATUS, selectionArgs[0]};
		Object[] row2 = new Object[]{
			new Integer(SEARCH_CATALOG_USER), 
			getContext().getString(R.string.lable_search_suggest_user, selectionArgs[0]),
			SEARCH_CATALOG_USER, selectionArgs[0]};
		
		cursor.addRow(row1);
		cursor.addRow(row2);
		return cursor;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		return null;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		return 0;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}
	
}