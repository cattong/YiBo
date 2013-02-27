package net.dev123.mblog.conf;

import net.dev123.commons.Constants;
import net.dev123.commons.ServiceProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApiConfigurationFactory {

    private static final String CLASS_NAME_FORMAT = "net.dev123.mblog.%1$s.%2$sApiConfiguration";

    private static Logger logger = LoggerFactory.getLogger(ApiConfigurationFactory.class.getSimpleName());

    public static synchronized ApiConfiguration getApiConfiguration(ServiceProvider serviceProvider) {
        ApiConfiguration conf = null;

        try {
            String className =
                String.format(
                	CLASS_NAME_FORMAT,
                    serviceProvider.toString().toLowerCase(),
                    serviceProvider.toString()
                );

            conf = (ApiConfiguration) Class.forName(className).newInstance();
        } catch (Exception e) {
        	if (Constants.DEBUG) {
            	logger.debug("Get ApiConfiguration instance for {}", serviceProvider, e);
        	}
        }

        return conf;
    }
}
