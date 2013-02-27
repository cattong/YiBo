package net.dev123.commons.http.config;

import java.util.Hashtable;

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
public class HttpConfigurationFactory {
    private static final String CLASS_NAME_FORMAT = "net.dev123.%1$s%2$s.%3$sHttpConfiguration";

    private static Logger logger = LoggerFactory.getLogger(HttpConfigurationFactory.class.getSimpleName());
	private static Hashtable<ServiceProvider, HttpConfiguration> spHttpConfigs =
		new Hashtable<ServiceProvider, HttpConfiguration>();

	public static synchronized HttpConfiguration getHttpConfiguration(ServiceProvider serviceProvider) {

		HttpConfiguration conf = spHttpConfigs.get(serviceProvider);
		if (conf != null) {
			return conf;
		}

		try {
			if (serviceProvider == ServiceProvider.None) {
				conf = new HttpConfigurationBase();
			} else {
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

		        conf = (HttpConfiguration) Class.forName(className).newInstance();
			}

        } catch (Exception e) {
        	if (Constants.DEBUG) {
            	logger.debug("Get HttpConfiguration instance for {}", serviceProvider, e);
        	}
        	conf = new HttpConfigurationBase();
        }
		spHttpConfigs.put(serviceProvider, conf);

		return conf;
	}
}
