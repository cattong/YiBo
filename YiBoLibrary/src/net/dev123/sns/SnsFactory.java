package net.dev123.sns;

import java.lang.reflect.Constructor;

import net.dev123.commons.Constants;
import net.dev123.commons.ServiceProvider;
import net.dev123.commons.http.auth.Authorization;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SnsFactory {

    private static final String CLASS_NAME_FORMAT = "net.dev123.sns.%1$s.%2$s";
    private static Logger logger = LoggerFactory.getLogger(SnsFactory.class.getSimpleName());

	public static Sns getInstance(Authorization auth) {
		Sns sns = null;
		ServiceProvider serviceProvider = auth.getServiceProvider();

		try {
			String className =
	            String.format(
	            	CLASS_NAME_FORMAT,
	                serviceProvider.toString().toLowerCase(),
	                serviceProvider.toString()
	            );

			Constructor<?> constructor =
				Class.forName(className).getConstructor(Authorization.class);
			sns = (Sns) constructor.newInstance(auth);
		} catch (Exception e) {
			if (Constants.DEBUG) {
	        	logger.debug("Get MicroBlog instance for {}", serviceProvider, e);
			}
		}

		return sns;
	}

}
