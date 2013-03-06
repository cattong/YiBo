package com.cattong.socialcat;

import java.lang.reflect.Constructor;

import com.cattong.commons.LibResultCode;
import com.cattong.commons.LibRuntimeException;
import com.cattong.commons.Logger;
import com.cattong.commons.ServiceProvider;
import com.cattong.commons.http.auth.Authorization;
import com.cattong.socialcat.impl.socialcat.SocialCat;

public class SocialCatFactory {

	public static SocialCat getInstance(Authorization auth, String clientVersion) {
		if (auth == null || auth.getServiceProvider() != ServiceProvider.SocialCat) {
			throw new LibRuntimeException(LibResultCode.E_PARAM_ERROR);
		}
		
		String packageName = SocialCatFactory.class.getPackage().getName();
		ServiceProvider sp = auth.getServiceProvider();
		packageName += ".impl." + sp.toString().toLowerCase();
		packageName += "." + sp.toString();
		
		SocialCat socialCatInstance = null;
		Class<?>[] constructorParams = {Authorization.class, String.class};
		try {
			Class<?> socialCatInstanceClass = Class.forName(packageName);
			Constructor<?> constructor = socialCatInstanceClass.getConstructor(constructorParams);
			socialCatInstance = (SocialCat)constructor.newInstance(auth, clientVersion);			
		} catch (Exception e) {
			Logger.error("SocialCatFactory:{}", sp, e);
		}
		
		return socialCatInstance;
	}

}
