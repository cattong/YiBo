package com.cattong.commons.http.auth;

import java.util.HashMap;
import java.util.Map;

import com.cattong.commons.LibException;
import com.cattong.commons.LibResultCode;
import com.cattong.commons.oauth.OAuthAccessor;
import com.cattong.commons.oauth.OAuthConsumer;
import com.cattong.commons.oauth.OAuthParameterStyle;
import com.cattong.commons.oauth.config.OAuthConfig;
import com.cattong.commons.util.StringUtil;


public class OAuthAccessorFactory {

	private static Map<Authorization, OAuthAccessor> accessorMap;
	private static Map<String, OAuthConsumer> consumerMap;

	static {
		accessorMap = new HashMap<Authorization, OAuthAccessor>();
		consumerMap = new HashMap<String, OAuthConsumer>();		
	}

	public synchronized static OAuthAccessor getOAuthAccessorInstance(Authorization auth) throws LibException {
		if (auth == null) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}

		OAuthAccessor accessor = accessorMap.get(auth);
		if (accessor == null) {
			OAuthConsumer consumer = consumerMap.get(auth.getoAuthConfig().getConsumerKey());
			if (consumer == null) {
				OAuthConfig oauthConfig = auth.getoAuthConfig();				
				
				consumer = new OAuthConsumer(oauthConfig.getCallbackUrl(), 
					oauthConfig.getConsumerKey(), 
					oauthConfig.getConsumerSecret());
				if (StringUtil.isNotEmpty(oauthConfig.getOAuthParameterStyle())) {
					OAuthParameterStyle paramStyle = OAuthParameterStyle.valueOf(oauthConfig.getOAuthParameterStyle());
					consumer.setParameterStyle(paramStyle);
				}
				
				consumerMap.put(oauthConfig.getConsumerKey(), consumer);				
			}

			accessor = new OAuthAccessor(consumer);
			accessor.setAuthorization(auth);
			accessorMap.put(auth, accessor);
		}

		return accessor;
	}
}
