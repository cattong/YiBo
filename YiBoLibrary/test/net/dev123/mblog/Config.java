package net.dev123.mblog;

import java.util.HashMap;
import java.util.Map;

import net.dev123.commons.ServiceProvider;
import net.dev123.commons.http.auth.Authorization;
import net.dev123.commons.http.auth.OAuthAuthorization;
import net.dev123.commons.util.EncryptUtil;

public class Config {
    public static final byte[] KEY_BYTES  = { 0x6f, 0x68, 0x6d, 0x79, 0x67, 0x6f, 0x64, 0x21 };

    public static Map<ServiceProvider, Authorization> oauthMap;
    public static final ServiceProvider currentProvider = ServiceProvider.Sina;

    public static String userName = "raise-0@163.com"; //"yibo.m9@gmail.com";
    public static String password = "24097410"; //"yibo2011";
    //来自测试帐号:yibo.m9@gmail.com
    public static final String[][] oauths = {
    	{"", ""},
    	//新浪;
    	{"sd8kykRAs8Th+Q9+3woQFYVbJLrOWoZty7duVLlKRjC7YQIy9zSatQ==", "fSBF/Ti3BwNVTh5PRIJlin5cGJxyo5HNLWQaCiMo2m+7YQIy9zSatQ=="},
    	//搜狐
    	{"aUGClc6OSX7T5aQA5SBLs6R2ivMgfr+GaOifF7ulTUq7YQIy9zSatQ==", "JsVd2rarf24VEbbDOUqztq+zhxIfPkJQzb1H2hujiWK7YQIy9zSatQ=="},
    	//网易:token, secret
    	{"R7gtoZRwGf9wKa1CdPZ1OV6w4KzSFQkqy7hn7aOD/fK7YQIy9zSatQ==", "mHau6hgZHOmqWVJGOKSf0qm21AZA3ZM0lIf5JkVJxBS7YQIy9zSatQ=="},
    	 //腾讯
    	{"a6xCAwy4yLm0WM4cD6e78xetz+nH42Nz/yGd12RmbTW7YQIy9zSatQ==", "k5ebdWtAPHmema0noBiBqrLVKz/wpjv7S5wrlhazZFm7YQIy9zSatQ=="},
    	//twitter
    	{"vkW9vzj0eegQoLnTaK45i8V8FRio7bxxz55RcHEyqS/gR1zhJZY7dQCfxtVQRs4pM9WnCGnbDu4=", "yqPgDd3+X/719eIVvjtdmVexdQBqbz4Ew9BqJfF4IIoxci9+AFXJ1IPbKxwSslPN"},
    	//饭否
    	{"hkcsnrezYEYOky/bkp3Hrkom8ugwKE6aJSrT9OToB5vd+xiDDjeAtLthAjL3NJq1", "ZVvJvoOk0QaqJk28TEFG7lxte0khHHM6AKQiJMOAWzi7YQIy9zSatQ=="}
    };

	static {
		oauthMap = new HashMap<ServiceProvider, Authorization>();

	    for (int i = 1; i < oauths.length; i++) {
	    	ServiceProvider sp = getServiceProvider(i);
			Authorization oauth = null;
			oauth = new OAuthAuthorization(
				EncryptUtil.desDecrypt(oauths[i][0], KEY_BYTES),
				EncryptUtil.desDecrypt(oauths[i][1], KEY_BYTES),
				sp
			);

			oauthMap.put(sp, oauth);
	    }
	}

	public static ServiceProvider getServiceProvider(int providerNo) {
		ServiceProvider[] providers = ServiceProvider.values();
		ServiceProvider sp = null;
		for (ServiceProvider temp : providers) {
			if (temp.getServiceProviderNo() == providerNo) {
				sp = temp;
				break;
			}
		}

		return sp;
	}

	public static MicroBlog getMicroBlog(ServiceProvider sp) {
		MicroBlog mBlog = null;
		if (sp == null) {
			return mBlog;
		}
		Authorization oauth = oauthMap.get(sp);
		mBlog = MicroBlogFactory.getInstance(oauth);
		return mBlog;
	}

}
