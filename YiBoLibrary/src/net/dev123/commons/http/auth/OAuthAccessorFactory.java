package net.dev123.commons.http.auth;

import java.util.HashMap;
import java.util.Map;

import net.dev123.commons.ServiceProvider;
import net.dev123.commons.oauth.OAuthAccessor;
import net.dev123.commons.oauth.OAuthConsumer;
import net.dev123.commons.oauth.OAuthParameterStyle;
import net.dev123.commons.oauth.OAuthServiceProvider;
import net.dev123.commons.oauth.config.OAuthConfiguration;
import net.dev123.commons.oauth.config.OAuthConfigurationFactory;
import net.dev123.commons.util.StringUtil;
import net.dev123.exception.ExceptionCode;
import net.dev123.exception.LibException;

public class OAuthAccessorFactory {

	private static Map<OAuthAuthorization, OAuthAccessor> accessorMap;
	private static Map<String, OAuthConsumer> consumerMap;
	private static Map<ServiceProvider, OAuthServiceProvider> serviceProviderMap;

	static {
		accessorMap = new HashMap<OAuthAuthorization, OAuthAccessor>();
		consumerMap = new HashMap<String, OAuthConsumer>();
		serviceProviderMap = new HashMap<ServiceProvider, OAuthServiceProvider>();
		
		//事先提供一份YiBo.Android版的key
		registerOAuthConsumer(ServiceProvider.Sina, "3105114937", 
			"985e8f106a5db148d1a96abfabcd9043", "http://www.yibo.me/authorize/getAccessToken.do");
	}

	public synchronized static OAuthAccessor getOAuthAccessorInstance(OAuthAuthorization auth) throws LibException {
		if (auth == null) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}

		OAuthAccessor accessor = accessorMap.get(auth);
		if (accessor == null) {
			OAuthConsumer consumer = consumerMap.get(auth.getConsumerKey());
			if (consumer == null) {
				OAuthConfiguration conf =
					OAuthConfigurationFactory.getOAuthConfiguration(auth.getServiceProvider());
				String authConsumerKey = auth.getConsumerKey();
				if (authConsumerKey == null || "NULL".equalsIgnoreCase(authConsumerKey)) {
					authConsumerKey = conf.getOAuthConsumerKey();
				}
				if (StringUtil.isEquals(authConsumerKey, conf.getOAuthConsumerKey())) {
					registerOAuthConsumer(auth.getServiceProvider(),
						conf.getOAuthConsumerKey(), conf.getOAuthConsumerSecret(),
						conf.getOAuthCallbackURL());
				} else {
					registerOAuthConsumer(auth.getServiceProvider(), 
						auth.getConsumerKey(), auth.getConsumerSecret(), 
						conf.getOAuthCallbackURL());
				}
				consumer = consumerMap.get(authConsumerKey);
			}

			accessor = new OAuthAccessor(consumer);
			accessor.setOAuthToken(auth.getOAuthToken());
			accessorMap.put(auth, accessor);
		}

		return accessor;
	}

	public static void registerOAuthConsumer(ServiceProvider serviceProvider, String consumerKey,
		String consumerSecret, String callbackUrl) {
		if (consumerMap.containsKey(consumerKey)) {
			return;
		}

		OAuthConfiguration conf = OAuthConfigurationFactory.getOAuthConfiguration(serviceProvider);
		OAuthServiceProvider oauthSp = serviceProviderMap.get(serviceProvider);
		if (oauthSp == null) {
			oauthSp = new OAuthServiceProvider(conf.getOAuthRequestTokenURL(),
					conf.getOAuthAuthorizeURL(), conf.getOAuthAccessTokenURL());
			serviceProviderMap.put(serviceProvider, oauthSp);
		}

		OAuthConsumer consumer = new OAuthConsumer(callbackUrl, consumerKey,
				consumerSecret, oauthSp);
		if (StringUtil.isNotEmpty(conf.getOAuthParameterStyle())) {
			consumer.setParameterStyle(OAuthParameterStyle.valueOf(conf.getOAuthParameterStyle()));
		}
		consumerMap.put(consumerKey, consumer);
	}
}
