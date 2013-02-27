package net.dev123.yibo.common;

import net.dev123.commons.ServiceProvider;
import net.dev123.commons.http.auth.NullAuthorization;
import net.dev123.commons.http.auth.OAuthAuthorization;
import net.dev123.commons.util.StringUtil;
import net.dev123.yibo.db.ConfigSystemDao;
import net.dev123.yibome.YiBoMe;
import net.dev123.yibome.YiBoMeImpl;
import android.content.Context;

/**
 * @author Weiping Ye
 * @version 创建时间：2011-10-21 下午2:29:29
 **/
public class YiBoMeUtil {

	public static YiBoMe getYiBoMeNullAuth() {
		NullAuthorization auth = new NullAuthorization(ServiceProvider.YiBoMe);
		return new YiBoMeImpl(auth);
	}
	
	public static YiBoMe getYiBoMeOAuth(Context context) {
		if (context == null) {
			return null;
		}
		
		ConfigSystemDao configDao = new ConfigSystemDao(context);
		String authToken = configDao.getString(Constants.PASSPORT_TOKEN);
		String authSecret = configDao.getString(Constants.PASSPORT_SECRET);
		if (StringUtil.isEmpty(authToken) || StringUtil.isEmpty(authSecret)) {
			return null;
		}
		
		OAuthAuthorization auth = new OAuthAuthorization(authToken, authSecret, ServiceProvider.YiBoMe);
		return new YiBoMeImpl(auth);
	}
}
