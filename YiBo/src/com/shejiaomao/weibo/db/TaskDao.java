package com.shejiaomao.weibo.db;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.cattong.commons.Paging;
import com.cattong.commons.ServiceProvider;
import com.cattong.commons.util.ListUtil;
import com.cattong.entity.BaseUser;
import com.cattong.entity.User;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class TaskDao extends BaseDao<Task> {
	private static final String TABLE = "Task";
	
	private UserDao userDao;
	
	public TaskDao(Context context) {
		super(context);
		userDao = new UserDao(context);
	}
	
	public Task save(Task task) {
		if (task == null) {
			return null;
		}
		
		SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
		sqLiteDatabase.beginTransaction();
		try {
			ContentValues values = new ContentValues();
			values.put("Task_ID", task.getTaskId());
			values.put("Type", task.getType());
			values.put("Content", task.getContent());
			values.put("Result_ID", task.getResultId());
			if (task.getCreatedAt() == null) {
				task.setCreatedAt(new Date());
			}
			values.put("Created_At", task.getCreatedAt().getTime());
			if (task.getFinishedAt() != null) {
			    values.put("Finished_At", task.getFinishedAt().getTime());
			}
			values.put("State", task.getState());
			values.put("Service_Provider", task.getServiceProvider().getSpNo());
			values.put("Account_ID", task.getAccountId());
			
			long rowId = sqLiteDatabase.replace(TABLE, null, values);
			task.setTaskId(rowId);
			sqLiteDatabase.setTransactionSuccessful();
		} finally {
			sqLiteDatabase.endTransaction();
		}
		
		return task;
	}

	public int udpate(Task task) {
		if (task == null || task.getTaskId() == null) {
			return -1;
		}
		
		SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
		sqLiteDatabase.beginTransaction();

		int rowsAffected = 0;
		try {
			ContentValues values = new ContentValues();			

			values.put("Result_ID", task.getResultId());
			if (task.getFinishedAt() != null) {
			    values.put("Finished_At", task.getFinishedAt().getTime());
			}
			values.put("State", task.getState());
			
			rowsAffected = sqLiteDatabase.update(TABLE, values, "Task_ID = " + task.getTaskId(), null);
		} finally {
			sqLiteDatabase.endTransaction();
		}

		return rowsAffected;
	}
	
	public List<Task> findTaskList(LocalAccount account, int type, int state, Paging<?> paging) {
		List<Task> taskList = null;
		if (account == null || paging == null) {
			return taskList;
		}
		
		String sql =
		    "select " +
			"    * " +
			"from " +
			"    Task " +
			"where " +
			"    Type = " + type + " and " +
			"    State = " + state + " and " +
			"    Account_ID = " + account.getAccountId() + " " +
			"order by Created_At desc";
		
		taskList = find(sql, paging.getPageIndex(), paging.getPageSize());
		if (ListUtil.isEmpty(taskList) 
			|| taskList.size() < paging.getPageSize() / 2) {
			paging.setLastPage(true);
		}
		
		return taskList;
	}

	public List<BaseUser> findRecentContact(LocalAccount account, Paging<User> paging) {
		List<BaseUser> userList = null;;
		if (account == null || paging == null) {
			return userList;
		}
		
		List<Task> taskList = findTaskList(account, Task.TYPE_RECENT_CONTACK, Task.STATE_FINISHED, paging);
		if (ListUtil.isEmpty(taskList)) {
	    	return userList;
	    }
	    
	    userList = new ArrayList<BaseUser>();
	    for (Task task : taskList) {
    	    BaseUser user = userDao.findById(task.getResultId(), task.getServiceProvider());
    	    if (user != null && !userList.contains(user)) {
    	    	userList.add(user);
    	    }
	    }
	    
	    return userList;
	}
	
	public void saveRecentContact(LocalAccount account, List<BaseUser> userList) {
		if (account == null || ListUtil.isEmpty(userList)) {
			return;
		}
		
		Task task = new Task();
		task.setType(Task.TYPE_RECENT_CONTACK);
		task.setContent(null);
		
		Date currentDate = new Date();
		task.setCreatedAt(currentDate);
		task.setFinishedAt(currentDate);
		task.setState(Task.STATE_FINISHED);
		task.setServiceProvider(account.getServiceProvider());
		task.setAccountId(account.getAccountId());
		for (BaseUser user : userList) {
			task.setTaskId(null);
			task.setResultId(user.getUserId());
			saveRecentContact(task);
		}
	}
	
	public Task saveRecentContact(Task task) {
		if (task == null) {
			return null;
		}
		
		SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
		sqLiteDatabase.beginTransaction();
		try {
			ContentValues values = new ContentValues();
			//values.put("Task_ID", task.getTaskId());
			values.put("Type", task.getType());
			values.put("Content", task.getContent());
			values.put("Result_ID", task.getResultId());
			if (task.getCreatedAt() == null) {
				task.setCreatedAt(new Date());
			}
			values.put("Created_At", task.getCreatedAt().getTime());
			if (task.getFinishedAt() != null) {
			    values.put("Finished_At", task.getFinishedAt().getTime());
			}
			values.put("State", task.getState());
			values.put("Service_Provider", task.getServiceProvider().getSpNo());
			values.put("Account_ID", task.getAccountId());
			
			int rowsAffected = sqLiteDatabase.update(
				TABLE, values, 
				"Type = " + task.getType() + " and Account_ID = " + task.getAccountId() 
				+ " and Result_ID = '" + task.getResultId() + "'", 
				null);
			if (rowsAffected <= 0) {
				values.put("Task_ID", task.getTaskId());
				long rowId = sqLiteDatabase.replace(TABLE, null, values);
				task.setTaskId(rowId);
			}
			
			sqLiteDatabase.setTransactionSuccessful();
		} finally {
			sqLiteDatabase.endTransaction();
		}
		
		return task;
	}
	
	@Override
	public Task extractData(SQLiteDatabase sqLiteDatabase, Cursor cursor) {
		Task task = new Task();
		task.setTaskId(cursor.getLong(cursor.getColumnIndex("Task_ID")));
		task.setType(cursor.getInt(cursor.getColumnIndex("Type")));
		task.setContent(cursor.getString(cursor.getColumnIndex("Content")));
		task.setResultId(cursor.getString(cursor.getColumnIndex("Result_ID")));
		Long createdAt = cursor.getLong(cursor.getColumnIndex("Created_At"));
		if (createdAt != null && createdAt > 0) {
		    task.setCreatedAt(new Date(createdAt));
		}
		Long finishedAt = cursor.getLong(cursor.getColumnIndex("Finished_At"));
		if (finishedAt != null && finishedAt > 0) {
		    task.setFinishedAt(new Date(finishedAt));
		}
		task.setState(cursor.getInt(cursor.getColumnIndex("State")));
		int spNo = cursor.getInt(cursor.getColumnIndex("Service_Provider"));
		task.setServiceProvider(ServiceProvider.getServiceProvider(spNo));
		task.setAccountId(cursor.getLong(cursor.getColumnIndex("Account_ID")));

		return task;
	}

}
