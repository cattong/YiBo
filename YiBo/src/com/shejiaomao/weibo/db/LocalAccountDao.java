package com.shejiaomao.weibo.db;

import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.cattong.commons.ServiceProvider;
import com.cattong.commons.http.auth.Authorization;
import com.cattong.commons.util.EncryptUtil;
import com.cattong.entity.Account;
import com.cattong.entity.BaseUser;
import com.cattong.entity.User;
import com.cattong.weibo.impl.twitter.ProxyBasicAuth;
import com.shejiaomao.weibo.common.Constants;

public class LocalAccountDao extends BaseDao<LocalAccount> {

	private static final String TABLE = "Account";

	private UserDao userDao;

	public LocalAccountDao(Context context) {
		super(context);
		userDao = new UserDao(context);
	}

	public void add(Account account) {
		if (isNull(account)) {
			return;
		}

		SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
		sqLiteDatabase.beginTransaction();
		try {
			add(sqLiteDatabase, account);
			sqLiteDatabase.setTransactionSuccessful();
		} finally {
			sqLiteDatabase.endTransaction();
		}
	}
	
	/*package*/ void add(SQLiteDatabase sqLiteDatabase, Account account) {
		ContentValues values = new ContentValues();
		values.put("Access_Token",
			EncryptUtil.desEncrypt(account.getAccessToken(), Constants.KEY_BYTES));
		values.put("Access_Secret",
			EncryptUtil.desEncrypt(account.getAccessSecret(), Constants.KEY_BYTES));
		values.put("Auth_Version", account.getAuthVersion());
		values.put("Service_Provider", account.getServiceProvider().getSpNo());
		values.put("User_ID", account.getUser().getUserId());
		values.put("Screen_Name", account.getUser().getScreenName());
		values.put("App_Key", String.valueOf(account.getAppKey()));
		values.put("App_Secret", String.valueOf(account.getAppSecret()));
		values.put("State", account.getState());
		values.put("Created_At",
			account.getCreatedAt() == null ?
				new Date().getTime() : account.getCreatedAt().getTime());
		values.put("Is_Default", account.isDefault());
		values.put("Rest_Proxy_Url", account.getRestProxyUrl());
		values.put("Search_Proxy_Url", account.getSearchProxyUrl());
		values.put("Token_Expired_At",
				account.getTokenExpiredAt() == null? -1L : account.getTokenExpiredAt().getTime());

		if (account.getUser() != null) {
			userDao.save(sqLiteDatabase, account.getUser());
		}
		
		if (account.isDefault()) {
			resetDefaultAccount(sqLiteDatabase);  // 清除原有的默认帐号状态设置
		}
		
		clearDeletedAccount(sqLiteDatabase, account); // 确保一个帐号在数据库中只有一条记录，一个状态

		long rowId = sqLiteDatabase.insert(TABLE, null, values);
		//You can access the ROWID of an SQLite table using one the special
		//column names ROWID, _ROWID_, or OID.
		//If a table contains a column of type INTEGER PRIMARY KEY,
		//then that column becomes an alias for the ROWID.
		//You can then access the ROWID using any of four different names,
		//the original three names described above or the name given to
		//the INTEGER PRIMARY KEY column.
		//All these names are aliases for one another and work equally well in any context.
		account.setAccountId(rowId);
	}
	
	private void resetDefaultAccount(SQLiteDatabase sqLiteDatabase) {
		StringBuilder sql = new StringBuilder();
		sql.append(" update Account set Is_Default = 0 ");
		sql.append(" where Is_Default = 1");
		sqLiteDatabase.execSQL(sql.toString());
	}
	
	private void clearDeletedAccount(SQLiteDatabase sqLiteDatabase, Account account) {
		StringBuilder whereClause = new StringBuilder();
		whereClause.append(" State = ").append(Account.STATE_INVALID);
		whereClause.append(" and Service_Provider = ").append(account.getServiceProviderNo());
		whereClause.append(" and User_Id = '").append(account.getUserId()).append("'");
		sqLiteDatabase.delete(TABLE, whereClause.toString(), null);
	}

	public int update(Account account) {
		if (isNull(account)) {
			return -1;
		}

		SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
		sqLiteDatabase.beginTransaction();

		int rowsAffected = 0;
		try {
			rowsAffected = update(sqLiteDatabase, account);
			sqLiteDatabase.setTransactionSuccessful();
		} finally {
			sqLiteDatabase.endTransaction();
		}

		return rowsAffected;
	}

	/*package*/ int update(SQLiteDatabase sqLiteDatabase, Account account) {
		if (isNull(account)) {
			return -1;
		}

		ContentValues values = new ContentValues();
		values.put("Access_Token",
			EncryptUtil.desEncrypt(account.getAccessToken(), Constants.KEY_BYTES));
		values.put("Access_Secret",
			EncryptUtil.desEncrypt(String.valueOf(account.getAccessSecret()), Constants.KEY_BYTES));
		values.put("Auth_Version", account.getAuthVersion());
		values.put("Service_Provider", account.getServiceProvider().getSpNo());
		values.put("User_ID", account.getUser().getUserId());
		values.put("Screen_Name", account.getUser().getScreenName());
		values.put("App_Key", String.valueOf(account.getAppKey()));
		values.put("App_Secret", String.valueOf(account.getAppSecret()));
		values.put("State", account.getState());
		values.put("Is_Default", account.isDefault());
		values.put("Rest_Proxy_Url", account.getRestProxyUrl());
		values.put("Search_Proxy_Url", account.getSearchProxyUrl());
		values.put("Token_Expired_At",
				account.getTokenExpiredAt() == null? -1L : account.getTokenExpiredAt().getTime());

		if (account.isDefault()) {
			resetDefaultAccount(sqLiteDatabase);  // 清除原有的默认帐号状态设置
		}
		
		if (account.getUser() != null) {
			userDao.save(sqLiteDatabase,account.getUser());
		}
		int rowsAffected = sqLiteDatabase.update(TABLE, values,
			"Account_ID = " + account.getAccountId(), null);

		return rowsAffected;
	}

	public int delete(Account account) {
		if (isNull(account)) {
			return -1;
		}

		SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
		StringBuilder whereClause = new StringBuilder();
		if (account.getAccountId() > 0) {
			whereClause.append(" Account_ID = ").append(account.getAccountId());
		} else {
			whereClause.append(" Service_Provider = ").append(account.getServiceProviderNo());
			whereClause.append(" and User_Id = '").append(account.getUserId()).append("'");
		}
		int rowsAffected = sqLiteDatabase.delete(TABLE, whereClause.toString(), null);
		return rowsAffected;
	}

	public LocalAccount getDefaultAccount() {
		StringBuilder sql = new StringBuilder();
		sql.append("select * from Account where Is_Default = 1");
		sql.append(" and State != ").append(Account.STATE_APPLY);
		return query(sql.toString());
	}

	public boolean makeDefault(LocalAccount account) {
		SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
		sqLiteDatabase.beginTransaction();
		boolean isSuccess = false;
		try {
			resetDefaultAccount(sqLiteDatabase);
			StringBuilder sql = new StringBuilder();
			sql.append(" update Account set Is_Default = 1 ");
			sql.append(" where Account_Id = ").append(account.getAccountId());
			sql.append(" and State != ").append(Account.STATE_APPLY);
			account.setDefault(true);
			sqLiteDatabase.execSQL(sql.toString());
			sqLiteDatabase.setTransactionSuccessful();
			isSuccess = true;
		} finally {
			sqLiteDatabase.endTransaction();
		}
		return isSuccess;
	}
	
	public boolean hasUnsyncedAccounts() {
		StringBuilder sql = new StringBuilder();
		sql.append("select * from Account where ");
		sql.append(" State = ").append(Account.STATE_APPLY);
		List<LocalAccount> unsynced = find(sql.toString());
		return unsynced != null && unsynced.size() > 0;
	}

	public List<LocalAccount> findAll() {
		String sql = "select * from Account order by Created_At asc";
		return find(sql);
	}

	public List<LocalAccount> findAllValid() {
		String sql = "select * from Account where State = "
			+ Account.STATE_ACTIVE + " order by Created_At asc";
		return find(sql);
	}

	public LocalAccount findById(long accountId){
		String sql = "select * from Account where Account_ID = " + accountId;
		return this.query(sql);
	}

	public List<LocalAccount> findByState(int state){
		String sql = "select * from Account where State = " + state;
		return find(sql);
	}
		
	/*package*/ LocalAccount findByUser(SQLiteDatabase sqLiteDatabase, BaseUser user) {
		StringBuilder sql = new StringBuilder();
		sql.append(" select * from Account where User_Id = '");
		sql.append(user.getUserId()).append("'");
		sql.append(" and Service_Provider = ");
		sql.append(user.getServiceProvider().getSpNo());
		return query(sqLiteDatabase, sql.toString());
	}

	/**
	 * 判断服务提供商下相应用户是否已配置
	 *
	 * @param sp
	 *            服务提供商
	 * @param accountName
	 *            用户名
	 * @return 是否已配置
	 */
	public boolean isExists(ServiceProvider sp, String userId) {
		StringBuilder sql = new StringBuilder();
		sql.append(" select * from Account where User_Id = '").append(userId).append("'");
		sql.append(" and Service_Provider = ").append(sp.getSpNo());
		sql.append(" and State = ").append(Account.STATE_ACTIVE);
		LocalAccount oldAccount = query(sql.toString());

		return null != oldAccount;
	}

	@Override
	public LocalAccount extractData(SQLiteDatabase sqLiteDatabase, Cursor cursor) {
		LocalAccount account = new LocalAccount();
		account.setAccountId(cursor.getLong(cursor.getColumnIndex("Account_ID")));
		String accessToken = EncryptUtil.desDecrypt(
			cursor.getString(cursor.getColumnIndex("Access_Token")),Constants.KEY_BYTES);
		String accessSecret = EncryptUtil.desDecrypt(
			cursor.getString(cursor.getColumnIndex("Access_Secret")), Constants.KEY_BYTES);

		int authVersion = cursor.getInt(cursor.getColumnIndex("Auth_Version"));
		account.setAuthVersion(authVersion);
		account.setState(cursor.getInt(cursor.getColumnIndex("State")));
		long time = cursor.getLong(cursor.getColumnIndex("Created_At"));
		account.setCreatedAt(new Date(time));
		account.setDefault(1 == cursor.getInt(cursor.getColumnIndex("Is_Default")));
		account.setRestProxyUrl(cursor.getString(cursor.getColumnIndex("Rest_Proxy_Url")));
		account.setSearchProxyUrl(cursor.getString(cursor.getColumnIndex("Search_Proxy_Url")));
		long expiresIn = cursor.getLong(cursor.getColumnIndex("Token_Expired_At"));
		if (expiresIn > 0) {
			account.setTokenExpiredAt(new Date(expiresIn));
		}

		String consumerKey = cursor.getString(cursor.getColumnIndex("App_Key"));
		String consumerSecret = cursor.getString(cursor.getColumnIndex("App_Secret"));
		account.setAppKey(consumerKey);
		account.setAppSecret(consumerSecret);
		
		int sp = cursor.getInt(cursor.getColumnIndex("Service_Provider"));
		ServiceProvider serviceProvider = ServiceProvider.getServiceProvider(sp);
		
		Authorization auth = new Authorization(serviceProvider);
		auth.setAccessToken(accessToken);
		auth.setAccessSecret(accessSecret);
        auth.setExpiredAt(account.getTokenExpiredAt());
		auth.getoAuthConfig().setConsumerKey(consumerKey);
		auth.getoAuthConfig().setConsumerSecret(consumerSecret);
		
        if (authVersion == Authorization.AUTH_VERSION_BASIC) {
			if (serviceProvider == ServiceProvider.Twitter) {
				ProxyBasicAuth proxyAuth = new ProxyBasicAuth(accessToken, 
					accessSecret, serviceProvider);
				proxyAuth.setRestApiServer(account.getRestProxyUrl());
				proxyAuth.setSearchApiServer(account.getSearchProxyUrl());
				auth = proxyAuth;
			} else {
				auth = new Authorization(serviceProvider, accessToken, accessSecret);
			}
		} 
        
		account.setAuthorization(auth);

		String userId = cursor.getString(cursor.getColumnIndex("User_ID"));
		BaseUser user = userDao.findById(sqLiteDatabase,
			userId, account.getAuthorization().getServiceProvider());
		if (user == null) {
			user = new User();
			user.setUserId(userId);
			user.setScreenName(cursor.getString(cursor.getColumnIndex("Screen_Name")));
			user.setServiceProvider(serviceProvider);
		}
		account.setUser(user);

		return account;
	}
	
//	public boolean syncToDatabase(AccountSyncResult syncResult) {
//		if (syncResult == null) {
//			return false;
//		}
//		
//		boolean isSynced = false;
//		SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
//		sqLiteDatabase.beginTransaction();
//		
//		try {
//			List<Account> accountList = null;
//			LocalAccount localAccount = null;
//			
//			accountList = syncResult.getToBeUpdated();
//			if (accountList!= null && accountList.size() > 0) {
//				//服务端更新的帐号，本地以服务端数据为准进行更新
//				for (Account account : accountList) {
//					localAccount = findByUser(sqLiteDatabase, account.getUser());
//					account.setAccountId(localAccount.getAccountId());
//					copyAccountProperties(account, localAccount);
//					localAccount.setState(Account.STATE_SYNCED);
//					update(sqLiteDatabase, localAccount);
//				}
//			}
//
//			accountList = syncResult.getToBeDeleted();
//			if (accountList!= null && accountList.size() > 0) {
//				//服务端删除的帐号，本地进行删除
//				for (Account account : accountList) {
//					localAccount = findByUser(sqLiteDatabase, account.getUser());
//					localAccount.setState(Account.STATE_DELETED);
//					localAccount.setCreatedAt(new Date());
//					update(sqLiteDatabase, localAccount);
//				}
//			}
//
//			accountList = syncResult.getToBeAdded();
//			if (accountList!= null && accountList.size() > 0) {
//				//服务端添加的帐号，本地新增
//				for (Account account : accountList) {
//					add(sqLiteDatabase, account);
//				}
//			}
//			
//			//删除逻辑删除的帐号
//			sqLiteDatabase.execSQL("delete from Account where State = " + Account.STATE_DELETED);
//			
//			//帐号表中所有帐号标记为已同步
//			sqLiteDatabase.execSQL("update Account set State = " + Account.STATE_SYNCED);
//			
//			sqLiteDatabase.setTransactionSuccessful();
//			isSynced = true;
//		} finally {
//			sqLiteDatabase.endTransaction();
//		}
//		return isSynced;
//	}
	
//	private void copyAccountProperties(Account sourceAccount, Account targetAccount) {
//		targetAccount.setAppKey(sourceAccount.getAppKey());
//		targetAccount.setAppSecret(sourceAccount.getAppSecret());
//		targetAccount.setAccessToken(sourceAccount.getAccessToken());
//		targetAccount.setAccessSecret(sourceAccount.getAccessSecret());
//		targetAccount.setAuthVersion(sourceAccount.getAuthVersion());
//		targetAccount.setCreatedAt(sourceAccount.getCreatedAt());
//		targetAccount.setDefault(sourceAccount.isDefault());
//		targetAccount.setRestProxyUrl(sourceAccount.getRestProxyUrl());
//		targetAccount.setSearchProxyUrl(sourceAccount.getSearchProxyUrl());
//		targetAccount.setTokenExpiredAt(sourceAccount.getTokenExpiredAt());
//		targetAccount.setUser(sourceAccount.getUser());
//	}

}
