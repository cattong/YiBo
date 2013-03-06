package com.cattong.weibo;

import java.lang.reflect.Constructor;

import com.cattong.commons.LibResultCode;
import com.cattong.commons.LibRuntimeException;
import com.cattong.commons.Logger;
import com.cattong.commons.ServiceProvider;
import com.cattong.commons.http.auth.Authorization;

public class WeiboFactory {

	public static Weibo getInstance(Authorization auth) {
		if (auth == null) {
			throw new LibRuntimeException(LibResultCode.E_PARAM_NULL);
		}
		
		String packageName = WeiboFactory.class.getPackage().getName();
		ServiceProvider sp = auth.getServiceProvider();
		packageName += ".impl." + sp.toString().toLowerCase();
		packageName += "." + sp.toString();
		
		Weibo weiboInstance = null;
		Class<?>[] constructorParams = {Authorization.class};
		try {
			//Class<?> weiboInstanceClass = ScanPackageUtil.getAbstractExtendClass(packageName, Weibo.class);
			Class<?> weiboInstanceClass = Class.forName(packageName);
			Constructor<?> constructor = weiboInstanceClass.getConstructor(constructorParams);
			weiboInstance = (Weibo)constructor.newInstance(auth);			
		} catch (Exception e) {
			Logger.error("WeiboFactory:{}", sp, e);
		}
		
		return weiboInstance;
	}

}
