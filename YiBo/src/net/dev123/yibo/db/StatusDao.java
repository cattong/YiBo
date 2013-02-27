package net.dev123.yibo.db;

import java.util.Date;
import java.util.List;

import net.dev123.commons.ServiceProvider;
import net.dev123.commons.util.StringUtil;
import net.dev123.entity.GeoLocation;
import net.dev123.mblog.entity.Status;
import net.dev123.mblog.entity.User;
import net.dev123.yibo.common.StatusCatalog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class StatusDao extends BaseDao<Status> {

	private static final String TABLE = "Status";

	private UserDao userDao;

	public StatusDao(Context context) {
		super(context);
		userDao = new UserDao(context);
	}

	public void save(Status status, StatusCatalog catalog, LocalAccount account) {
		if (isNull(status) || isNull(catalog) || isNull(account)) {
			return;
		}

		SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
		sqLiteDatabase.beginTransaction();
		try {
			save(sqLiteDatabase, status, catalog, account);
			sqLiteDatabase.setTransactionSuccessful();
		} finally {
			sqLiteDatabase.endTransaction();
		}
	}

	public void batchSave(List<Status> statuses, StatusCatalog catalog, LocalAccount account) {
		if (isNull(statuses) || isNull(catalog) || isNull(account)) {
			return;
		}

		SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
		sqLiteDatabase.beginTransaction();
		try {
			for (Status status : statuses) {
				save(sqLiteDatabase, status, catalog, account);
			}
			sqLiteDatabase.setTransactionSuccessful();
		} finally {
			sqLiteDatabase.endTransaction();
		}
	}

	void save(SQLiteDatabase sqLiteDatabase, Status status, StatusCatalog catalog, LocalAccount account) {
		if (isNull(status)) {
			return;
		}

		ContentValues values = new ContentValues();
		values.put("Status_ID", status.getId());
		values.put("Created_At", status.getCreatedAt() == null ? 0 : status.getCreatedAt().getTime());
		values.put("Text", status.getText());
		values.put("Source", status.getSource());
		values.put("Is_Truncated", status.isTruncated() ? 1 : 0);
		values.put("Is_Favorated", status.isFavorited() ? 1 : 0);
		values.put("Thumbnail_Picture", status.getThumbnailPicture());
		values.put("Middle_Picture", status.getMiddlePicture());
		values.put("Original_Picture", status.getOriginalPicture());
		values.put("Service_Provider", status.getServiceProvider().getServiceProviderNo());
		values.put("Catalog", catalog.getCatalogId());
		values.put("Comment_Count", status.getCommentCount());
		values.put("Retweet_Count", status.getRetweetCount());

		GeoLocation geoLocation = status.getGeoLocation();
		if (geoLocation != null) {
			values.put("Geo", geoLocation.getLatitude() + "," + geoLocation.getLongitude());
		}

		long accountId = (account == null ? -1 : account.getAccountId());
		values.put("Account_ID", accountId);
		if (status instanceof LocalStatus) {
			values.put("Is_Divider", ((LocalStatus)status).isDivider() ? 1 : 0);
		} else {
			values.put("Is_Divider", 0);
		}

		if (status.getRetweetedStatus() != null) {
			save(sqLiteDatabase, status.getRetweetedStatus(), catalog, null);
			values.put("Retweeted_Status_ID", status.getRetweetedStatus().getId());
		}
		if( status.getUser() != null) {
			userDao.save(sqLiteDatabase, status.getUser());
			values.put("User_ID", status.getUser().getId());
		}

		sqLiteDatabase.replace(TABLE, null, values);
	}

	public int delete(Status status, LocalAccount account) {
		if (isNull(status)|| isNull(account)) {
			return -1;
		}

		SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
		int rowsAffected = sqLiteDatabase.delete(TABLE, "Status_ID = '" + status.getId()
					+ "' and Account_ID = " + account.getAccountId(), null);
		return rowsAffected;
	}

	public int delete(LocalAccount account) {
		if (isNull(account)) {
			return -1;
		}

		SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
		int rowsAffected = sqLiteDatabase.delete(TABLE, "Account_ID = " + account.getAccountId(), null);
		return rowsAffected;
	}

	public Status findById(String statusId, ServiceProvider sp, boolean isReplyTo) {
		if (isNull(statusId) || isNull(sp)) {
			return null;
		}
		SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
		return findById(sqLiteDatabase, statusId, sp, isReplyTo);
	}

	Status findById(SQLiteDatabase sqLiteDatabase , String statusId, ServiceProvider sp, boolean isReplyTo) {
		StringBuilder sql = new StringBuilder();
		sql.append("select * from Status where Status_ID = '").append(statusId).append("'");
		sql.append(" and Service_Provider = ").append(sp.getServiceProviderNo());
		if (isReplyTo) {
			sql.append(" and Account_ID = -1");
		}
		return this.query(sqLiteDatabase, sql.toString());
	}


	@Override
	public Status extractData(SQLiteDatabase sqLiteDatabase, Cursor cursor) {
		LocalStatus status = new LocalStatus();
		status.setId(cursor.getString(cursor.getColumnIndex("Status_ID")));
		status.setAccountId(cursor.getLong(cursor.getColumnIndex("Account_ID")));
		long time = cursor.getLong(cursor.getColumnIndex("Created_At"));
		if (time > 0) {
			status.setCreatedAt(new Date(time));
		}
		status.setText(cursor.getString(cursor.getColumnIndex("Text")));
		status.setSource(cursor.getString(cursor.getColumnIndex("Source")));
		status.setFavorited(1 == cursor.getInt(cursor.getColumnIndex("Is_Truncated")));
		status.setTruncated(1 == cursor.getInt(cursor.getColumnIndex("Is_Truncated")));
		status.setThumbnailPicture(cursor.getString(cursor.getColumnIndex("Thumbnail_Picture")));
		status.setMiddlePicture(cursor.getString(cursor.getColumnIndex("Middle_Picture")));
		status.setOriginalPicture(cursor.getString(cursor.getColumnIndex("Original_Picture")));
		status.setCommentCount(cursor.getInt(cursor.getColumnIndex("Comment_Count")));
		status.setRetweetCount(cursor.getInt(cursor.getColumnIndex("Retweet_Count")));
		status.setDivider(1 == cursor.getInt(cursor.getColumnIndex("Is_Divider")));

		String geo = cursor.getString(cursor.getColumnIndex("Geo"));
		if (StringUtil.isNotEmpty(geo)) {
			String[] geoValues = geo.split(",");
			if (geoValues != null && geoValues.length >= 2) {
				try {
				    double latitude = Double.parseDouble(geoValues[0]);
				    double longitude = Double.parseDouble(geoValues[1]);
				    GeoLocation geoLocation = new GeoLocation(latitude, longitude);
				    status.setGeoLocation(geoLocation);
				} catch (Exception e) {}
			}
		}

		int sp = cursor.getInt(cursor.getColumnIndex("Service_Provider"));
		status.setServiceProvider(ServiceProvider.getServiceProvider(sp));

		String userId = cursor.getString(cursor.getColumnIndex("User_ID"));
		User user = (User) userDao.findById(sqLiteDatabase, userId, status.getServiceProvider());
		status.setUser(user);

		String retweetedStatusId = cursor.getString(cursor.getColumnIndex("Retweeted_Status_ID"));
		if (retweetedStatusId != null) {
			status.setRetweetedStatus(findById(sqLiteDatabase, retweetedStatusId, status.getServiceProvider(), true));
		}

		return status;
	}

}
