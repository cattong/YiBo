package net.dev123.yibo.service.task;

import java.util.Calendar;

import net.dev123.yibo.R;
import net.dev123.yibo.YiBoApplication;
import net.dev123.yibo.common.Constants;
import net.dev123.yibo.common.StatusCatalog;
import net.dev123.yibo.db.DBHelper;
import android.content.Context;
import android.content.res.Resources;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

public class StatusesCleanTask extends AsyncTask<Void, Void, Boolean> {

	private static final String TAG = StatusesCleanTask.class.getSimpleName();
	private Context context;
	private YiBoApplication yibo;

	public StatusesCleanTask(Context context){
		this.context = context;
		yibo = (YiBoApplication)context.getApplicationContext();
	}

	@Override
	protected Boolean doInBackground(Void... arg0) {
		int value = yibo.getCacheStrategy();

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, - value + 1);
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		long time = calendar.getTimeInMillis();
		Resources res = context.getResources();
		String[] cleanSql = res.getStringArray(R.array.db_clean_status_sql);
		String sql = String.format(cleanSql[0], 
			time, StatusCatalog.Home.getCatalogId(), 
			StatusCatalog.Others.getCatalogId(), time);

		if (Constants.DEBUG) Log.d(TAG, sql);
		SQLiteDatabase sqLiteDatabase = DBHelper.getInstance(context).getWritableDatabase();
		boolean success = false;
		try {
			long startTime = System.currentTimeMillis();
			sqLiteDatabase.execSQL(sql);
			if (Constants.DEBUG) {
				Log.v(TAG, "Statused Clean use time:" + (System.currentTimeMillis() - startTime));
			}
			success = true;
		} catch (SQLException e) {
			if (Constants.DEBUG) {
				Log.d(TAG, e.getMessage(), e);
			}
		}

		return success;
	}

}
