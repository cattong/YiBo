package net.dev123.commons.http.config;


/**
 * Configuration
 *
 * @version
 * @author 马庆升
 * @time 2010-8-1 上午10:50:18
 */
public interface HttpConfiguration extends java.io.Serializable {

	boolean isUseGzip();

	boolean isUseProxy();

	String getUserAgent();

	// methods for HttpClientConfiguration

	String getHttpProxyHost();

	String getHttpProxyUser();

	String getHttpProxyPassword();

	int getHttpProxyPort();

	int getHttpConnectionTimeout();

	int getHttpReadTimeout();

	int getHttpRetryCount();

	int getHttpRetryIntervalSeconds();

	String getClientVersion();

	String getClientURL();

}