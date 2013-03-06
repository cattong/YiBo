package com.cattong.commons.http.config;

import java.util.Hashtable;

import com.cattong.commons.Logger;
import com.cattong.commons.ServiceProvider;

public class HttpConfigurationFactory {
    private static final String CLASS_NAME_FORMAT = "com.cattong.%1$s%2$s.%3$sHttpConfiguration";

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
				String spCategoryPath = serviceProvider.getSpCategory().toLowerCase();
				
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
            Logger.debug("Get HttpConfiguration instance for {}", serviceProvider, e);
        	conf = new HttpConfigurationBase();
        }
		spHttpConfigs.put(serviceProvider, conf);

		return conf;
	}
}
