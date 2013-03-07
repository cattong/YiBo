package com.shejiaomao.weibo.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.content.Context;

import com.cattong.commons.ServiceProvider;
import com.cattong.commons.util.ListUtil;
import com.cattong.sns.Sns;
import com.cattong.sns.SnsFactory;
import com.cattong.weibo.Weibo;
import com.cattong.weibo.WeiboFactory;
import com.shejiaomao.common.ImageQuality;
import com.shejiaomao.common.NetType;
import com.shejiaomao.common.NetUtil.NetworkOperator;
import com.shejiaomao.weibo.db.LocalAccount;
import com.shejiaomao.weibo.db.LocalAccountDao;

public class GlobalVars {

	public static boolean IS_OBEY_SINA_AGREEMENT = false;  // 是否遵循新浪协议
    public static boolean IS_MOBILE_NET_UPDATE_VERSION = false; //2g/3g网络下是否提示更新
    
	//当前网络类型;
	public static NetworkOperator NET_OPERATOR = NetworkOperator.NONE;
	public static NetType NET_TYPE = NetType.NONE;

	//缓冲设置配置
	public static boolean IS_SHOW_HEAD;
	public static boolean IS_SHOW_THUMBNAIL;
	public static int UPDATE_COUNT;
	public static ImageQuality IMAGE_DOWNLOAD_QUALITY;
    public static boolean IS_ENABLE_GESTURE;
    public static Locale LOCALE;
    public static int FONT_SIZE_HOME_BLOG;
    public static int FONT_SIZE_HOME_RETWEET;
    public static boolean IS_DETECT_IAMGE_INFO;
    public static boolean IS_AUTO_LOAD_COMMENTS;
    public static boolean IS_FULLSCREEN;
    
	//运行时环境变量
	private static Map<Long, Weibo> microblogMap;
	private static Map<Long, Sns> snsMap;
	private static List<LocalAccount> accountList;

	static {
		microblogMap = new HashMap<Long, Weibo>();
		accountList = new ArrayList<LocalAccount>();
		snsMap = new HashMap<Long, Sns>();
	}

	public static void addAccount(LocalAccount account) {
		if (account == null || accountList.contains(account)) {
		    return;
		}

		//时间排序
		int i = 0;
		for (i = 0; i < accountList.size(); i++) {
			LocalAccount temp = accountList.get(i);
			if (account.getCreatedAt().before(temp.getCreatedAt())) {
				break;
			}
		}
		accountList.add(i, account);

		if (account.isSnsAccount()) {
			if (snsMap.containsKey(account.getAccountId())) {
				return;
			}
			Sns sns = SnsFactory.getInstance(account.getAuthorization());
			if (sns != null) {
				snsMap.put(account.getAccountId(), sns);
			}
		} else {
			if (!microblogMap.containsKey(account.getAccountId())) {
				Weibo microBlog = WeiboFactory.getInstance(account.getAuthorization());
				if (microBlog != null) {
					microblogMap.put(account.getAccountId(), microBlog);
				}
			}
		}

	}

	public static void addAccounts(List<LocalAccount> list) {
		if (list == null || list.size() <= 0) {
		    return;
		}

		for (LocalAccount account : list) {
			addAccount(account);
		}
	}

	public static void removeAccount(LocalAccount account) {
		if (!accountList.contains(account)) {
			return;
		}

		accountList.remove(account);
		microblogMap.remove(account.getAccountId());
	}

	public static LocalAccount getAccount(long accountID) {
		LocalAccount account = null;
		if (accountID < 0) {
			return account;
		}

		for (int i = 0; i < accountList.size(); i++) {
			if (accountID == accountList.get(i).getAccountId().longValue()) {
				account = accountList.get(i);
				break;
			}
		}

		return account;
	}

	public static Weibo getMicroBlog(long accountID) {
		return microblogMap.get(accountID);
	}

	public static Sns getSns(long accountID) {
		return snsMap.get(accountID);
	}

	public static Sns getSns(LocalAccount account) {
		Sns sns = null;
		if (account == null) {
			return sns;
		}

		sns = snsMap.get(account.getAccountId());
		return sns;
	}

	public static Weibo getMicroBlog(LocalAccount account) {
		Weibo microBlog = null;
		if (account == null) {
			return microBlog;
		}

		microBlog = microblogMap.get(account.getAccountId());
		return microBlog;
	}

    public static long getAccountID(ServiceProvider sp, String userID) {
    	long accountID = -1;

    	for (LocalAccount account : accountList) {
    		if (account.getServiceProvider() == sp
    			&& account.getUser().getUserId().equals(userID)) {
    			accountID = account.getAccountId();
    			break;
    		}
    	}

    	return accountID;
    }

    public static void clear() {
    	microblogMap.clear();
    	snsMap.clear();
		accountList.clear();
    }

	/**
	 * 获取帐号列表
	 *
	 * @param context 操作相关Context
	 * @param isReload 是否从数据库重新读取
	 * @return 帐号列表
	 */
	public static List<LocalAccount> getAccountList(Context context, boolean isReload) {
		if (isReload || accountList.size() == 0) {
			reloadAccounts(context);
		}
		return accountList;
	}

	/**
	 * 重新装载帐号
	 */
	public static void reloadAccounts(Context context) {
		if (context == null) {
			return;
		}
		LocalAccountDao accountDao = new LocalAccountDao(context);
		List<LocalAccount> listAccount = accountDao.findAllValid();
		GlobalVars.clear();
		GlobalVars.addAccounts(listAccount);
		
		//umeng收集用户数据
		gatherUser(context);
	}

	/**
	 * umeng 收集信息
	 */
	private static void gatherUser(Context context) {
		if (ListUtil.isEmpty(accountList)) {
			return;
		}
		
		for (LocalAccount account : accountList) {
			if (account.getServiceProvider() == ServiceProvider.Sina) {
				//MobclickAgent.setUserID(context, account.getUserId(), "weibo.com");
				break;
			}
		}
	}
}
