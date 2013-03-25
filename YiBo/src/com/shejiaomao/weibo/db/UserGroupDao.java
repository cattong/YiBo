package com.shejiaomao.weibo.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.cattong.commons.Logger;
import com.cattong.commons.Paging;
import com.cattong.commons.ServiceProvider;
import com.cattong.commons.util.ListUtil;
import com.cattong.commons.util.StringUtil;
import com.cattong.entity.BaseUser;
import com.cattong.weibo.entity.Group;

public class UserGroupDao extends BaseDao<UserGroup> {
	private static final String TABLE = "User_Group";
    private UserDao userDao;
	public UserGroupDao(Context context) {
		super(context);
		userDao = new UserDao(context);
	}

	public void save(UserGroup userGroup) {
		if (isNull(userGroup)) {
			return;
		}
		SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
		save(sqLiteDatabase, userGroup);
	}

	public void batchSave(List<UserGroup> userGroups) {
		if (isNull(userGroups)) {
			return;
		}
		SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
		sqLiteDatabase.beginTransaction();
		try {
			for (UserGroup userGroup : userGroups) {
				save(sqLiteDatabase, userGroup);
			}
			sqLiteDatabase.setTransactionSuccessful();
		} finally {
			sqLiteDatabase.endTransaction();
		}
	}

	public void batchSave(LocalAccount account, LocalGroup group, List<? extends BaseUser> userList) {
		if (account == null || group == null || ListUtil.isEmpty(userList)) {
			return;
		}

		userDao.batchSave(userList);
		List<UserGroup> userGroupList = new ArrayList<UserGroup>();
		UserGroup userGroup = null;
		for (BaseUser user : userList) {
			userGroup = new UserGroup();
			userGroup.setGroupId(group.getGroupId());
			userGroup.setServiceProvider(account.getServiceProvider());
			userGroup.setState(UserGroup.STATE_ADDED);
			userGroup.setUserId(user.getUserId());
			userGroupList.add(userGroup);
		}

		batchSave(userGroupList);
	}

	void save(SQLiteDatabase sqLiteDatabase, UserGroup userGroup) {
		if (isNull(userGroup)) {
			return;
		}
		if(Logger.isDebug()){
			Log.d("Save UserGroup:", userGroup.toString());
		}

		ContentValues values = new ContentValues();
		values.put("Service_Provider", userGroup.getServiceProvider().getSpNo());
		values.put("User_ID", userGroup.getUserId());
		values.put("Group_ID", userGroup.getGroupId());
		values.put("State", userGroup.getState());

		sqLiteDatabase.replace(TABLE, null, values);
	}

	public int delete(UserGroup userGroup) {
		if (isNull(userGroup)) {
			return -1;
		}

		return delete(userGroup.getGroupId(), userGroup.getServiceProvider(), userGroup.getUserId());
	}

	public int delete(long groupId, ServiceProvider sp, String userId) {
		StringBuilder whereClause = new StringBuilder();
		if (groupId > 0L) {
			whereClause.append("Group_ID = ").append(groupId);
		}
		if (sp != null) {
			if (whereClause.length() > 0) {
				whereClause.append(" and ");
			}
			whereClause.append(" Service_Provider = ").append(sp.getSpNo());
		}
		if (StringUtil.isNotEmpty(userId)) {
			if (whereClause.length() > 0) {
				whereClause.append(" and ");
			}
			whereClause.append(" User_ID = '").append(userId).append("'");
		}

		if (whereClause.length() > 0) {
			SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
			return sqLiteDatabase.delete(TABLE, whereClause.toString(), null);
		}

		return -1;
	}

	public List<UserGroup> findUserGroups(long groupId, ServiceProvider sp) {
		List<UserGroup> userGroups = null;
		if (groupId <= 0) {
			return userGroups;
		}

		StringBuilder sql = new StringBuilder();
		sql.append("select * from User_Group where Group_ID = ").append(groupId);
		if (sp != null) {
			sql.append(" and Service_Privider = ").append(sp.getSpNo());
		}

		return find(sql.toString());
	}

	public List<BaseUser> getMembers(LocalGroup group, ServiceProvider serviceProvider,
		Paging<? extends BaseUser> paging) {
		List<BaseUser> listUser = null;
		if (group == null
			|| paging == null
		    || serviceProvider == null) {
			return listUser;
		}

		String sql =
		    "select " +
			"    b.* " +
			"from " +
			"    User_Group a, User b " +
			"where " +
			"    a.User_ID = b.User_ID and " +
			"    a.Group_ID = " + group.getGroupId() + " and " +
			"    a.Service_Provider = " +  serviceProvider.getSpNo() + " " +
			"order by b.Screen_Name desc";

		UserDao dao = new UserDao(context);
		listUser = dao.find(sql, paging.getPageIndex(), paging.getPageSize());
		if (ListUtil.isEmpty(listUser) || listUser.size() < paging.getPageSize() / 2) {
			paging.setLastPage(true);
		}

		return listUser;
	}

	public boolean isExist(Group group, BaseUser target) {
		boolean isExist = false;
		if (group == null || target == null) {
			return isExist;
		}

		String sql =
		    "select " +
			"    a.* " +
			"from " +
			"    User_Group a, Group_Info b " +
			"where " +
			"    a.Group_ID = b.Group_ID and " +
			"    b.SP_Group_ID = '" + group.getId() + "' and " +
			"    a.User_ID = '" + target.getUserId() + "' and " +
			"    a.Service_Provider = " +  target.getServiceProvider().getSpNo();

		UserGroup ug = query(sql);
		if (ug != null) {
			isExist = true;
		}

		return isExist;
	}
	@Override
	public UserGroup extractData(SQLiteDatabase sqLiteDatabase, Cursor cursor) {
		UserGroup userGroup = new UserGroup();
		userGroup.setGroupId(cursor.getLong(cursor.getColumnIndex("Group_ID")));
		userGroup.setServiceProviderNo(cursor.getInt(cursor.getColumnIndex("Service_Provider")));
		userGroup.setState(cursor.getInt(cursor.getColumnIndex("State")));
		userGroup.setUserId(cursor.getString(cursor.getColumnIndex("User_ID")));

		return userGroup;
	}

}
