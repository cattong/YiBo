package com.shejiaomao.weibo.db;

import java.util.List;

import com.cattong.commons.Paging;
import com.cattong.commons.ServiceProvider;
import com.cattong.commons.util.ListUtil;
import com.cattong.commons.util.StringUtil;
import com.cattong.entity.BaseUser;
import com.cattong.entity.User;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class SocialGraphDao {
	private static final String TABLE = "Social_Graph";

	private UserDao userDao;
	private DBHelper dbHelper;

	public SocialGraphDao(Context context) {
		dbHelper = DBHelper.getInstance(context);
		userDao = new UserDao(context);
	}

	public void save(User userA, User userB, Relation relation, ServiceProvider sp) {
		SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
		sqLiteDatabase.beginTransaction();
		try {
			save(sqLiteDatabase, userA, userB, relation, sp);

			sqLiteDatabase.setTransactionSuccessful();
		} finally {
			sqLiteDatabase.endTransaction();
		}
	}

	void save(SQLiteDatabase sqLiteDatabase, User userA, User userB, Relation relation, ServiceProvider sp) {
		ContentValues values = new ContentValues();
		values.put("User_A_ID", userA.getUserId());
		values.put("User_B_ID", userB.getUserId());
		values.put("Service_Provider", sp.getSpNo());
		values.put("Relationship", relation.getType());
		sqLiteDatabase.replace(TABLE, null, values);
	}

	public void delete(User userA, User userB, Relation relation, ServiceProvider sp) {
		SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
		sqLiteDatabase.beginTransaction();
		try {
			String whereClause = " USER_A_ID = '?' AND USER_B_ID = '?' AND Relationship = ? AND Service_Provider = ?";
			String[] whereArgs = new String[4];
			whereArgs[0] = userA.getUserId();
			whereArgs[1] = userB.getUserId();
			whereArgs[2] = String.valueOf(sp.getSpNo());
			whereArgs[3] = String.valueOf(relation.getType());
			sqLiteDatabase.delete(TABLE, whereClause, whereArgs);
			sqLiteDatabase.setTransactionSuccessful();
		} finally {
			sqLiteDatabase.endTransaction();
		}

	}

	public List<BaseUser> getFriends(User user, Paging<? extends BaseUser> paging) {
		if (user == null || paging == null) {
			return null;
		}
		return getUsers(user, paging, Relation.Followingship);
	}

	public List<BaseUser> getFollowers(User user, Paging<? extends BaseUser> paging) {
		if (user == null || paging == null) {
			return null;
		}
		return getUsers(user, paging, Relation.Followedship);
	}

	private List<BaseUser> getUsers(User user, Paging<? extends BaseUser> paging, Relation relation){
		StringBuilder sql = new StringBuilder();
		int providerNo = user.getServiceProvider().getSpNo();
		sql.append(
			"select " +
			"    a.* " +
			"from " +
			"    User a, Social_Graph b " +
			"where " +
		    "    b.User_A_ID = '" + user.getUserId() + "' and " +
			"    b.User_B_ID = a.User_ID and " +
		    "    b.Service_Provider = " + providerNo + " and " +
		    "    b.Relationship = " + relation.getType() + " " +
		    "order by a.Screen_Name asc "
		);

		List<BaseUser> userList = userDao.find(sql.toString(),
			paging.getPageIndex(), paging.getPageSize());
		if (ListUtil.isEmpty(userList) || userList.size() < paging.getPageSize() / 2) {
			paging.setLastPage(true);
		}
		return userList;
	}

	public void saveFriends(User user, List<User> friends) {
		saveSocialGraph(user, friends, Relation.Followingship);
	}

	public void saveFollowers(User user, List<User> followers) {
		saveSocialGraph(user, followers, Relation.Followedship);
	}

	private void saveSocialGraph(User user, List<User> users, Relation relation){
		SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
		sqLiteDatabase.beginTransaction();
		try {
			for(User u : users){
				save(sqLiteDatabase, user, u, relation, user.getServiceProvider());
				userDao.save(sqLiteDatabase, u);
			}

			sqLiteDatabase.setTransactionSuccessful();
		} finally{
			sqLiteDatabase.endTransaction();
		}
	}

	public List<BaseUser> findUsers(BaseUser user, String filterName, Relation relation) {
		List<BaseUser> listUser = null;
		if (user == null || StringUtil.isEmpty(filterName)) {
			return listUser;
		}
		if (relation == null) {
			relation = Relation.Followingship;
		}
		StringBuilder sql = new StringBuilder();
		int providerNo = user.getServiceProvider().getSpNo();
		sql.append(
			"select " +
			"    a.* " +
			"from " +
			"    User a, Social_Graph b " +
			"where " +
		    "    b.User_A_ID = '" + user.getUserId() + "' and " +
			"    b.User_B_ID = a.User_ID and " +
		    "    b.Service_Provider = " + providerNo + " and " +
		    "    b.Relationship = " + relation.getType() + " and " +
		    "    ( a.Screen_Name like '%" + filterName + "%' or " +
		    "      a.Name like '%" + filterName + "%')"
		);

		return userDao.find(sql.toString());
	}
}
