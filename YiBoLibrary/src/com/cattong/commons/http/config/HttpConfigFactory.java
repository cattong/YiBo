package com.cattong.commons.http.config;

import java.util.Hashtable;

import com.cattong.commons.Logger;
import com.cattong.commons.ServiceProvider;

public class HttpConfigFactory {
    private static final String CLASS_NAME_FORMAT = "com.cattong.%1$s.impl.%2$s.%3$sHttpConfig";

	private static Hashtable<ServiceProvider, HttpConfig> spHttpConfigs =
		new Hashtable<ServiceProvider, HttpConfig>();

	public static synchronized HttpConfig getHttpConfiguration(ServiceProvider serviceProvider) {

		HttpConfig conf = spHttpConfigs.get(serviceProvider);
		if (conf != null) {
			return conf;
		}

		try {
			if (serviceProvider == ServiceProvider.None) {
				conf = new HttpConfigBase();
			} else {
				String spCategoryPath = serviceProvider.getSpCategory().toLowerCase();
				
				String className =
	                String.format(
	                	CLASS_NAME_FORMAT,
	                	spCategoryPath,
	                    serviceProvider.toString().toLowerCase(),
	                    serviceProvider.toString()
	                );

		        conf = (HttpConfig) Class.forName(className).newInstance();
			}

        } catch (Exception e) {
            Logger.debug("Get HttpConfiguration instance for {}", serviceProvider, e);
        	conf = new HttpConfigBase();
        }
		spHttpConfigs.put(serviceProvider, conf);

		return conf;
	}
}
