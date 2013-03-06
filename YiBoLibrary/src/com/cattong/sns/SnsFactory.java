package com.cattong.sns;

import java.lang.reflect.Constructor;

import com.cattong.commons.LibResultCode;
import com.cattong.commons.LibRuntimeException;
import com.cattong.commons.Logger;
import com.cattong.commons.ServiceProvider;
import com.cattong.commons.http.auth.Authorization;

public class SnsFactory {

	public static Sns getInstance(Authorization auth) {
		if (auth == null) {
			throw new LibRuntimeException(LibResultCode.E_PARAM_NULL);
		}
		
		String packageName = SnsFactory.class.getPackage().getName();
		ServiceProvider sp = auth.getServiceProvider();
		packageName += ".impl." + sp.toString().toLowerCase();
		packageName += "." + sp.toString();
		
		Sns snsInstance = null;
		Class<?>[] constructorParams = {Authorization.class};
		try {
			//Class<?> weiboInstanceClass = ScanPackageUtil.getAbstractExtendClass(packageName, Weibo.class);
			Class<?> weiboInstanceClass = Class.forName(packageName);
			Constructor<?> constructor = weiboInstanceClass.getConstructor(constructorParams);
			snsInstance = (Sns)constructor.newInstance(auth);			
		} catch (Exception e) {
			Logger.error("SnsFactory:{}", sp, e);
		}
		
		return snsInstance;
	}

}
