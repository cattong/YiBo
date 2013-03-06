package com.cattong.weibo.conf;

import java.lang.reflect.Constructor;

import com.cattong.commons.LibResultCode;
import com.cattong.commons.LibRuntimeException;
import com.cattong.commons.Logger;
import com.cattong.commons.ServiceProvider;
import com.cattong.commons.http.auth.Authorization;
import com.cattong.weibo.WeiboFactory;

public class ApiConfigFactory {
	
    public static synchronized ApiConfig getApiConfig(Authorization auth) {
    	if (auth == null) {
    		throw new LibRuntimeException(LibResultCode.E_PARAM_NULL);
    	}
    	
        String packageName = WeiboFactory.class.getPackage().getName();
		ServiceProvider sp = auth.getServiceProvider();
		packageName += ".impl." + sp.toString().toLowerCase();
		packageName += "." + sp.toString() + "ApiConfig";
		
		ApiConfig apiConfig = null; 
		try {
			//Class<?> apiConfigInstanceClass = ScanPackageUtil.getAbstractExtendClass(packageName, ApiConfigBase.class);
			Class<?> apiConfigInstanceClass = Class.forName(packageName);
			Constructor<?> constructor = apiConfigInstanceClass.getConstructor();
			apiConfig = (ApiConfig)constructor.newInstance();	
		} catch (Exception e) {
			Logger.error("ApiConfigFactory:{}", sp, e);
		}				

        return apiConfig;
    }
}
