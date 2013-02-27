package net.dev123.commons.oauth.config;

import net.dev123.commons.Constants;
import net.dev123.commons.ServiceProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ConfigurationBase
 *
 * @version
 * @author 马庆升
 * @time 2010-7-29 下午02:50:29
 */
public class OAuthConfigurationFactory {
    private static final String CLASS_NAME_FORMAT = "net.dev123.%1$s%2$s.%3$sOAuthConfiguration";
    private static Logger logger = LoggerFactory.getLogger(OAuthConfigurationFactory.class.getSimpleName());

	public static synchronized OAuthConfiguration getOAuthConfiguration(ServiceProvider serviceProvider) {
		if (serviceProvider == ServiceProvider.None) {
			return null;
		}

		OAuthConfiguration conf = null;
		try {
			String spCategoryPath = serviceProvider.getServiceProviderCategory().toLowerCase();
			if (!ServiceProvider.CATEGORY_NONE.equals(spCategoryPath)) {
				spCategoryPath += ".";
			}

			String className =
	            String.format(
	            	CLASS_NAME_FORMAT,
	            	spCategoryPath,
	            	serviceProvider.toString().toLowerCase(),
	                serviceProvider.toString()
	            );

	        conf = (OAuthConfiguration) Class.forName(className).newInstance();
		} catch (Exception e) {
			if (Constants.DEBUG) {
            	logger.debug("Get ApiConfiguration instance for {}", serviceProvider, e);
        	}
		}
		return conf;
	}
}
