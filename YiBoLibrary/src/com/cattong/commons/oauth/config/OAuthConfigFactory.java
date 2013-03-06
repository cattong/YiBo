package com.cattong.commons.oauth.config;

import java.lang.reflect.Constructor;

import com.cattong.commons.Logger;
import com.cattong.commons.ServiceProvider;


public class OAuthConfigFactory {
	private static final String CLASS_NAME_FORMAT = "com.cattong.%1$s.impl.%2$s.%3$sOAuthConfig";
	
	public static synchronized OAuthConfig getOAuthConfig(ServiceProvider sp) {
		if (sp == ServiceProvider.None) {
			return null;
		}

		String spCategoryPath = sp.getSpCategory().toLowerCase();
		String packageName = String.format(CLASS_NAME_FORMAT, 
				spCategoryPath, sp.toString().toLowerCase(), sp.toString());
		
		OAuthConfig oauthConfig = null;
		try {
			//Class<?> oauthConfigInstanceClass = ScanPackageUtil.getAbstractExtendClass(packageName, OAuthConfigBase.class);
			Class<?> oauthConfigInstanceClass = Class.forName(packageName);
			Constructor<?> constructor = oauthConfigInstanceClass.getConstructor();
			oauthConfig = (OAuthConfig)constructor.newInstance();
		} catch (Exception e) {
            Logger.debug("OAuthConfigFactory: {}", sp, e);
		}
		
		return oauthConfig;
	}
}
