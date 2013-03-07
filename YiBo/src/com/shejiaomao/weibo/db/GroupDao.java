package com.shejiaomao.weibo.db;

import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.cattong.commons.Paging;
import com.cattong.commons.util.ListUtil;
import com.cattong.commons.util.StringUtil;
import com.cattong.weibo.entity.Group;

public class GroupDao extends BaseDao<Group> {
	private static final String TABLE = "Group_Info";

	public GroupDao(Context context) {
		super(context);
	}

	public LocalGroup save(LocalAccount account, Group group) {
		if (isNull(account) || isNull(group)) {
			return null;
		}

		LocalGroup result = getGroup(account, group);
		if (result != null) {
			return result;
		}

		SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
		sqLiteDatabase.beginTransaction();
		try {
			long remoteGroupId = 0l;
			Date createdAt = new Date();
			int state = LocalGroup.STATE_ADDED;
			if (group instanceof LocalGroup) {
				LocalGroup localGroup = (LocalGroup)group;
				remoteGroupId = localGroup.getRemoteGroupId();
				createdAt = localGroup.getCreatedAt();
				state = localGroup.getState();

				result = localGroup;
			} else {
				result = new LocalGroup();
			}

			ContentValues values = new ContentValues();
			values.put("SP_Group_ID", group.getId());
			values.put("Remote_Group_ID", remoteGroupId);
			values.put("Group_Name", group.getName());
			values.put("Created_At", createdAt.getTime());
			values.put("State", state);
            values.put("Account_ID", account.getAccountId());

			long rowId = sqLiteDatabase.replace(TABLE, null, values);
			if (group instanceof LocalGroup) {
			    result.setGroupId(rowId);
			    result.setId(group.getId());
			    result.setRemoteGroupId(remoteGroupId);
			    result.setName(group.getName());
			    result.setCreatedAt(createdAt);
			    result.setState(state);
			    result.setAccountId(account.getAccountId());
			}
			sqLiteDatabase.setTransactionSuccessful();
		} finally {
			sqLiteDatabase.endTransaction();
		}

		return result;
	}

	public void save(LocalAccount account, List<Group> groupList) {
		if (isNull(account) || ListUtil.isEmpty(groupList)) {
			return;
		}

		for (Group group : groupList) {
			save(account, group);
		}
	}

	public int update(LocalGroup group) {
		if (isNull(group)) {
			return -1;
		}

		SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
		sqLiteDatabase.beginTransaction();

		int rowsAffected = 0;
		try {
			ContentValues values = new ContentValues();
			values.put("Group_ID", group.getGroupId());
			values.put("SP_Group_ID", group.getSpGroupId());
			values.put("Remote_Group_ID", group.getRemoteGroupId());
			values.put("Group_Name", group.getName());
			long createTime = (group.getCreatedAt() == null ? 0 : group.getCreatedAt().getTime());
			values.put("Created_At", createTime);
			values.put("State", group.getState());
            values.put("Account_ID", group.getAccountId());
			rowsAffected = sqLiteDatabase.update(TABLE, values, "Group_ID = " + group.getGroupId(), null);

			sqLiteDatabase.setTransactionSuccessful();
		} finally {
			sqLiteDatabase.endTransaction();
		}

		return rowsAffected;
	}

	public boolean logicalDelete(LocalGroup group) {
		if (isNull(group)) {
			return false;
		}

		group.setState(LocalGroup.STATE_DELETED);
		update(group);
		return true;
	}

	public int delete(LocalGroup group) {
		if (isNull(group)) {
			return -1;
		}

		SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();

		int rowsAffected = sqLiteDatabase.delete(TABLE, "Group_ID = " + group.getGroupId(), null);
		return rowsAffected;
	}

	public int delete(LocalAccount account, Group group) {
		if (isNull(account) || isNull(group)) {
			return -1;
		}

		SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
        String whereClause = "SP_Group_ID = " + group.getId() 
            + " and Account_ID = " + account.getAccountId();
		int rowsAffected = sqLiteDatabase.delete(TABLE, whereClause, null);
		return rowsAffected;
	}
	
	public List<Group> findAll(LocalAccount account) {
		if (account == null) {
			return null;
		}
		String sql = "select * from Group_Info where Account_ID = " + account.getAccountId()
			+ " and State != " + LocalGroup.STATE_DELETED;
		return find(sql);
	}

	public Group findById(long groupId) {
		String sql = "select * from Group_Info where Group_ID = " + groupId;
		return this.query(sql);
	}

	public LocalGroup getGroup(LocalAccount account, Group group) {
		if (account == null || group == null) {
			return null;
		}
		String groupName = group.getName();
		if (StringUtil.isNotEmpty(groupName)) {
			groupName = groupName.replace("'", "''");
		}
		String sql = "select * from Group_Info where SP_Group_ID = '"
			+ group.getId() + "' and Account_ID = " + account.getAccountId()
			+ " and Group_Name = '" + groupName + "'";
		return (LocalGroup)this.query(sql);
	}

	public List<Group> getGroups(LocalAccount account, Paging<Group> paging) {
		List<Group> groupList = null;
		if (account == null || paging == null) {
			return groupList;
		}

		String sql =
		    "select " +
			"    * " +
			"from " +
			"    Group_Info " +
			"where " +
			"    State != " + LocalGroup.STATE_DELETED + " and " +
			"    Account_ID = " + account.getAccountId() + " " +
			"order by Created_At asc";

		groupList = find(sql, paging.getPageIndex(), paging.getPageSize());
		if (ListUtil.isEmpty(groupList) 
			|| groupList.size() < paging.getPageSize() / 2) {
			paging.setLastPage(true);
		}

		return groupList;
	}

	public boolean merge(LocalAccount account, List<Group> newGroupList) {
		boolean hasChange = false;
		if (account == null || ListUtil.isEmpty(newGroupList)) {
			return hasChange;
		}

		List<Group> oldGroupList = findAll(account);
		if (ListUtil.isEmpty(oldGroupList)) {
			hasChange = true;
			return hasChange;
		}
		for (Group group : newGroupList) {
			if (!oldGroupList.contains(group)) {
			    save(account, group);
			    hasChange = true;
			}
		}
		
		for (Group group : oldGroupList) {
			if (!newGroupList.contains(group)) {
				//delete
				delete((LocalGroup)group);
				hasChange = true;
			}
		}
		
		return hasChange;
	}
	
	@Override
	public LocalGroup extractData(SQLiteDatabase sqLiteDatabase, Cursor cursor) {
		LocalGroup group = new LocalGroup();
		group.setId(cursor.getString(cursor.getColumnIndex("SP_Group_ID")));
		group.setGroupId(cursor.getLong(cursor.getColumnIndex("Group_ID")));
		group.setSpGroupId(cursor.getString(cursor.getColumnIndex("SP_Group_ID")));
		group.setRemoteGroupId(cursor.getLong(cursor.getColumnIndex("Remote_Group_ID")));
		group.setName(cursor.getString(cursor.getColumnIndex("Group_Name")));
		group.setState(cursor.getInt(cursor.getColumnIndex("State")));

		long time = cursor.getLong(cursor.getColumnIndex("Created_At"));
		if (time > 0) {
			group.setCreatedAt(new Date(time));
		}
		group.setAccountId(cursor.getLong(cursor.getColumnIndex("Account_ID")));

		return group;
	}

}
